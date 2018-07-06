package com.gt22.uadam.data

import com.google.gson.JsonObject
import java.nio.file.Path

class Song : BaseData() {
    val album: Album
        get() = parent as Album
    override val path: String
        get() = "${album.path}/$name"

    override var children: Map<String, BaseData> = mapOf()
        set(value) {}

    override fun load(json: JsonObject, name: String, parent: BaseData?, path: Path) {
        throw UnsupportedOperationException("Song can't be loaded from json, use loadSong")
    }

    override fun createRoot(parent: BaseData?, path: Path) {
        throw UnsupportedOperationException("Song can't be _root")
    }

    fun loadSong(path: Path, parent: Album) {
        loadSongRemote(path.toString(), parent)
    }

    fun loadSongRemote(name: String, parent: Album) {
        this.parent = parent
        this.name = name
        img = parent.img
        format = parent.format
        title = name.removeSuffix(format)
    }

}
