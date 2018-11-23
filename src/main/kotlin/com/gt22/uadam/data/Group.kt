package com.gt22.uadam.data

import com.google.gson.JsonObject
import com.gt22.uadam.Loader
import com.gt22.uadam.utils.obj
import java.nio.file.Path

open class Group<T>: BaseData<T>() where T : BaseData<T>, T : IContext {

    @Suppress("UNCHECKED_CAST")
    var authors: Map<String, Author<T>>
        get() = children as Map<String, Author<T>>
        set(value) {
            children = value
        }

    override val path: String
        get() = "${context.path}$name"

    override fun load(json: JsonObject, name: String, parent: BaseData<T>?, path: Path) {
        if(parent !is IContext) {
            throw UnsupportedOperationException("Group should only be created with IContext parent")
        }
        super.load(json, name, parent, path)
        authors = Loader.load(path, this, "author")
    }

    override fun createRoot(parent: BaseData<T>?, path: Path) {
        if(parent !is IContext) {
            throw UnsupportedOperationException("Group should only be created with IContext parent")
        }
        super.createRoot(parent, path)
        authors = Loader.load(path, this, "author")
    }

    override fun createRemote(json: JsonObject, name: String, parent: BaseData<T>?) {
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