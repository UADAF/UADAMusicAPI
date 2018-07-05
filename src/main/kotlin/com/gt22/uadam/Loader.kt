package com.gt22.uadam

import com.gt22.randomutils.Instances
import com.gt22.uadam.data.*
import com.gt22.uadam.utils.obj
import com.gt22.uadam.utils.str
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

internal object Loader {

    private fun getDataClass(type: String): BaseData {
        return when (type) {
            "album" -> Album()
            "author" -> Author()
            "group" -> Group()
            else -> throw IllegalArgumentException("Invalid type '$type'")
        }
    }

    private fun getRootElement(type: String, parent: BaseData?, path: Path): BaseData {
        return when (type) {
            "album" -> object : Album() {
                override val path: String
                    get() = author.path
            }

            "author" -> object : Author() {
                override val path: String
                    get() = group.path
            }
            "group" -> object : Group() {
                override val path: String
                    get() = context.path
            }
            else -> throw IllegalArgumentException("Invalid type '$type'")
        }.apply { createRoot(parent, path) }
    }

    internal fun <T : BaseData> load(path: Path, parent: BaseData?, requiredType: String): Map<String, T> {
        val ret = mutableMapOf<String, T>()
        Files.list(path)
                .filter { Files.isDirectory(it) } //Songs are handled separately, and json is already loaded
                .sorted()
                .forEach { dataPath ->
                    val json = Instances.getParser().parse(
                            Files.newBufferedReader(dataPath.resolve("music.info.json"), Charsets.UTF_8)).obj
                    val type = json["type"]!!.str
                    if (type == requiredType) { //Root element should load everything else
                        val data = getDataClass(type)
                        data.load(json, dataPath.fileName.toString(), parent, dataPath)
                        //Class is directly derived from type, so, if this fails, something is wrong at caller site (wrong generic)
                        @Suppress("UNCHECKED_CAST")
                        ret[data.name] = data as T
                    }
                }
        @Suppress("UNCHECKED_CAST")
        val rootElem: T = getRootElement(requiredType, parent, path) as T
        if (rootElem.hasData()) {
            ret["_root"] = rootElem
        }
        return ret.toMap()
    }

    internal fun loadSongs(path: Path, format: String, album: Album): Map<String, Song> {
        return mapOf(*Files.list(path)
                .filter { it.fileName.toString().endsWith(format) }
                .sorted()
                .map { Song().apply { loadSong(it, album) } }
                .map { Pair(it.name, it) }.toList().toTypedArray())
    }

}