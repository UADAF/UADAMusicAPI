package com.gt22.uadam.data

import com.google.gson.JsonObject
import com.gt22.uadam.Loader
import com.gt22.uadam.utils.obj
import java.nio.file.Path

open class Group<T>: BaseData() where T : BaseData, T : IContext {

    @Suppress("UNCHECKED_CAST")
    val context: T
        get() = parent as T

    @Suppress("UNCHECKED_CAST")
    var authors: Map<String, Author>
        get() = children as Map<String, Author>
        set(value) {
            children = value
        }

    override val path: String
        get() = "${context.path}$name"

    override fun load(json: JsonObject, name: String, parent: BaseData?, path: Path) {
        if(parent !is IContext) {
            throw UnsupportedOperationException("Group should only be created with IContext parent")
        }
        super.load(json, name, parent, path)
        authors = Loader.load(path, this, "author")
    }

    override fun createRoot(parent: BaseData?, path: Path) {
        if(parent !is IContext) {
            throw UnsupportedOperationException("Group should only be created with IContext parent")
        }
        super.createRoot(parent, path)
        authors = Loader.load(path, this, "author")
    }

    override fun createRemote(json: JsonObject, name: String, parent: BaseData?) {
        if(parent !is IContext && parent !is RemoteMusicContext) {
            throw UnsupportedOperationException("Group should only be created with IContext parent")
        }
        super.createRemote(json, name, parent)
        authors = Loader.loadRemote(json["children"].obj, this, "author")
    }

    override fun hasData(): Boolean {
        return authors.isNotEmpty()
    }

}