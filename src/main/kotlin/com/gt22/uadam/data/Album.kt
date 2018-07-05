package com.gt22.uadam.data

import com.google.gson.JsonObject
import com.gt22.uadam.Loader
import java.nio.file.Path

open class Album : BaseData() {
    val author: Author
        get() = parent as Author

    @Suppress("UNCHECKED_CAST")
    var songs: Map<String, Song>
        get() = children as Map<String, Song>
        set(value) {
            children = value
        }

    override val path: String
        get() = "${author.path}/$name"



    override fun load(json: JsonObject, name: String, parent: BaseData?, path: Path) {
        if(parent !is Author) {
            throw IllegalArgumentException("Album should be only created with a parent author")
        }
        super.load(json, name, parent, path)
        songs = Loader.loadSongs(path, format, this)
    }

    override fun createRoot(parent: BaseData?, path: Path) {
        if(parent !is Author) {
            throw IllegalArgumentException("Album should be only created with a parent author")
        }
        super.createRoot(parent, path)
        songs = Loader.loadSongs(path, format, this)
    }

    override fun hasData(): Boolean {
        return songs.isNotEmpty()
    }
}