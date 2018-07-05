package com.gt22.uadam.data

import com.google.gson.JsonObject
import com.gt22.uadam.Loader
import java.nio.file.Path

open class Author : BaseData() {
    val group: Group
        get() = parent as Group

    @Suppress("UNCHECKED_CAST")
    var albums: Map<String, Album>
        get() = children as Map<String, Album>
        set(value) {
            children = value
        }

    override val path: String
        get() = "${group.path}/$name"

    override fun load(json: JsonObject, name: String, parent: BaseData?, path: Path) {
        if(parent !is Group) {
            throw IllegalArgumentException("Author should be only created with a parent group")
        }
        super.load(json, name, parent, path)
        albums = Loader.load(path, this, "album")
    }

    override fun createRoot(parent: BaseData?, path: Path) {
        if(parent !is Group) {
            throw IllegalArgumentException("Author should be only created with a parent group")
        }
        super.createRoot(parent, path)
        albums = Loader.load(path, this, "album")
    }

    override fun hasData(): Boolean {
        return albums.isNotEmpty()
    }
}