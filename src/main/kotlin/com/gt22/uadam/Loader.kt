package com.gt22.uadam

import com.google.gson.JsonObject
import com.gt22.uadam.data.*
import com.gt22.uadam.utils.PARSER

import com.gt22.uadam.utils.obj
import com.gt22.uadam.utils.str
import java.nio.file.Files
import java.nio.file.Path

internal object Loader {

    private fun <T> getDataClass(type: String): BaseData<T> where T : BaseData<T>, T : IContext {
        return when (type) {
            "album" -> Album()
            "author" -> Author()
            "group" -> Group()
            else -> throw IllegalArgumentException("Invalid type '$type'")
        }
    }

    private fun <T> getRootElement(type: String, parent: BaseData<T>?, path: Path): BaseData<T>
            where T : BaseData<T>, T : IContext {
        return when (type) {
            "album" -> object : Album<T>() {
                override val path: String
                    get() = author.path
            }

            "author" -> object : Author<T>() {
                override val path: String
                    get() = group.path
            }
            "group" -> object : Group<T>() {
                override val path: String
                    get() = context.path
            }
            else -> throw IllegalArgumentException("Invalid type '$type'")
        }.apply { createRoot(parent, path) }
    }

    internal fun <C, T : BaseData<C>> load(path: Path, parent: BaseData<C>?, requiredType: String): Map<String, T> where C : BaseData<C>, C : IContext {
        val ret = mutableMapOf<String, T>()
        Files.list(path)
                .filter { Files.isDirectory(it) } //Songs are handled separately, and json is already loaded
                .sorted()
                .forEach { dataPath ->
                    val json = PARSER.parse(
                            Files.newBufferedReader(dataPath.resolve("music.info.json"), Charsets.UTF_8)).obj
                    val type = json["type"]!!.str
                    if (type == requiredType) { //Root element should load everything else
                        //Class is directly derived from type, so, if this fails, something is wrong at caller site (wrong generic)
                        @Suppress("UNCHECKED_CAST")
                        val data = getDataClass<C>(type) as T
                        data.load(json, dataPath.fileName.toString(), parent, dataPath)
                        ret[data.name] = data
                    }
                }
        @Suppress("UNCHECKED_CAST")
        val rootElem = getRootElement(requiredType, parent, path) as T
        if (rootElem.hasData()) {
            ret["_root"] = rootElem
        }
        return ret.toMap()
    }

    internal fun <C, T : BaseData<C>> loadRemote(json: JsonObject, parent: BaseData<C>?, requiredType: String): MutableMap<String, T> where C : BaseData<C>, C : IContext {
        val ret = mutableMapOf<String, T>()
        json.entrySet().forEach { (k, v) ->
            @Suppress("UNCHECKED_CAST")
            ret[k] = getDataClass<C>(requiredType).apply { createRemote(v.obj, k, parent) } as T
        }
        return ret
    }



}