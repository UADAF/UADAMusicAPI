package com.gt22.uadam.data

import com.google.gson.JsonObject
import com.gt22.uadam.Loader
import com.gt22.uadam.utils.Instances
import com.gt22.uadam.utils.obj
import java.net.URL
import java.nio.file.Path

open class RemoteMusicContext private constructor() : BaseData() {


    @Suppress("UNCHECKED_CAST")
    var groups: Map<String, Group>
        get() = children as Map<String, Group>
        set(value) {
            children = value
        }

    override val path: String
        get() = "/"

    lateinit var url: URL
        private set

    override fun load(json: JsonObject, name: String, parent: BaseData?, path: Path)  = throw UnsupportedOperationException("Remote context can't be local")

    override fun createRoot(parent: BaseData?, path: Path) = throw UnsupportedOperationException("Context can't be root")

    override fun createRemote(json: JsonObject, name: String, parent: BaseData?) {
        super.createRemote(json, name, parent)
        groups = Loader.loadRemote(json["children"].obj, this, "group")
    }


    companion object {
        fun create(url: URL, name: String): RemoteMusicContext {
            val ret = RemoteMusicContext()
            val json = Instances.getParser().parse(url.openStream().bufferedReader())
            ret.createRemote(json.obj, name, null)
            return ret
        }
    }

}