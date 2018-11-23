package com.gt22.uadam.data

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.gt22.uadam.utils.arr
import com.gt22.uadam.utils.str
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

open class Album<T> : BaseData<T>() where T : BaseData<T>, T : IContext {
    val author: Author<T>
        get() = parent as Author<T>

    @Suppress("UNCHECKED_CAST")
    var songs: Map<String, Song<T>>
        get() = children as Map<String, Song<T>>
        set(value) {
            children = value
        }

    override val path: String
        get() = "${author.path}/$name"



    override fun load(json: JsonObject, name: String, parent: BaseData<T>?, path: Path) {
        if(parent !is Author<T>) {
            throw IllegalArgumentException("Album should be only created with a parent author")
        }
        super.load(json, name, parent, path)
        songs = loadSongs(path, format, this)
    }

    override fun createRoot(parent: BaseData<T>?, path: Path) {
        if(parent !is Author<T>) {
            throw IllegalArgumentException("Album should be only created with a parent author")
        }
        super.createRoot(parent, path)
        songs = loadSongs(path, format, this)
    }

    private fun loadSongs(path: Path, format: String, album: Album<T>): Map<String, Song<T>> {
        return mapOf(*Files.list(path)
                .filter { it.fileName.toString().endsWith(format) }
                .sorted()
                .map { Song<T>().apply { loadSong(it, album) } }
                .map { Pair(it.name, it) }.toList().toTypedArray())
    }


    override fun createRemote(json: JsonObject, name: String, parent: BaseData<T>?) {
        if(parent !is Author) {
            throw IllegalArgumentException("Album should be only created with a parent author")
        }
        super.createRemote(json, name, parent)
        val songs = mutableMapOf<String, Song<T>>()
        this.songs = songs
        json["children"].arr.map(JsonElement::str).forEach {
            val s = Song<T>()
            s.loadSongRemote(it, this)
            songs[it] = s
        }
    }

    override fun hasData(): Boolean {
        return songs.isNotEmpty()
    }
}