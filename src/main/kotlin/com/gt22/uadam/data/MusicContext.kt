package com.gt22.uadam.data

import com.google.gson.JsonObject
import com.gt22.randomutils.Instances
import com.gt22.uadam.Loader
import com.gt22.uadam.utils.obj
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path

open class MusicContext private constructor() : BaseData() {


    @Suppress("UNCHECKED_CAST")
    var groups: Map<String, Group>
        get() = children as Map<String, Group>
        set(value) {
            children = value
        }

    override val path: String
        get() = "/"

    lateinit var dirPath: Path
        private set

    override fun load(json: JsonObject, name: String, parent: BaseData?, path: Path) {
        super.load(json, name, parent, path)
        groups = Loader.load(path, this, "group")
        dirPath = path
    }

    override fun createRoot(parent: BaseData?, path: Path) = throw UnsupportedOperationException("Context can't be root")

    override fun createRemote(json: JsonObject, name: String, parent: BaseData?) = throw UnsupportedOperationException("Local context can't be remote")

    companion object {
        fun create(dir: Path): MusicContext {
            val json = dir.resolve("music.info.json")
            if (!Files.exists(json)) {
                throw FileNotFoundException(json.toString())
            }
            val data = Instances.getParser().parse(Files.newBufferedReader(json)).obj
            val context = MusicContext()
            context.load(data, dir.fileName.toString(), null, dir)
            return context
        }
    }

}