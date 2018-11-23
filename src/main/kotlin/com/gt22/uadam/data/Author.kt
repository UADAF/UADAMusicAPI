package com.gt22.uadam.data

import com.google.gson.JsonObject
import com.gt22.uadam.Loader
import com.gt22.uadam.utils.obj
import java.nio.file.Path

open class Author<T> : BaseData<T>() where T : BaseData<T>, T : IContext {
    val group: Group<*>
        get() = parent as Group<*>

    @Suppress("UNCHECKED_CAST")
    var albums: Map<String, Album<T>>
        get() = children as Map<String, Album<T>>
        set(value) {
            children = value
        }

    override val path: String
        get() = "${group.path}/$name"

    override fun load(json: JsonObject, name: String, parent: BaseData<T>?, path: Path) {
        if(parent !is Group<T>) {
            throw IllegalArgumentException("Author should be only created with a parent group")
        }
        super.load(json, name, parent, path)
        @Suppress("UNCHECKED_CAST")
        albums = Loader.load(path, this, "album")
    }

    override fun createRoot(parent: BaseData<T>?, path: Path) {
        if(parent !is Group<T>) {
            throw IllegalArgumentException("Author should be only created with a parent group")
        }
        super.createRoot(parent, path)
        @Suppress("UNCHECKED_CAST")
        albums = Loader.load(path, this, "album")
    }

    override fun createRemote(json: JsonObject, name: String, parent: BaseData<T>?) {
        if(parent !is Group<T>) {
            throw IllegalArgumentException("Author should be only created with a parent group")
        }
        super.createRemote(json, name, parent)
        @Suppress("UNCHECKED_CAST")
        albums = Loader.loadRemote(json["children"].obj, this, "album")
    }

    override fun hasData(): Boolean {
        return albums.isNotEmpty()
    }
}