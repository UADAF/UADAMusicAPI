package com.gt22.uadam.data

import com.google.gson.JsonObject
import java.nio.file.Path

class Song<T> : BaseData<T>() where T : BaseData<T>, T : IContext {
    val album: Album<T>
        get() = parent as Album<T>
    override val path: String
        get() = "${album.path}/$name"

    override var children: Map<String, BaseData<T>> = emptyMap()
        set(value) {}

    override fun load(json: JsonObject, name: String, parent: BaseData<T>?, path: Path) {
        throw UnsupportedOperationException("Song can't be loaded from json, use loadSong")
    }

    override fun createRoot(parent: BaseData<T>?, path: Path) {
        throw UnsupportedOperationException("Song can't be _root")
    }

    fun loadSong(path: Path, parent: Album<T>) {
        loadSongRemote(path.fileName.toString(), parent)
    }

    fun loadSongRemote(name: String, parent: Album<T>) {
        this.parent = parent
        this.name = name
        img = parent.img
        format = parent.format
        title = name.removeSuffix(format)
    }

}
