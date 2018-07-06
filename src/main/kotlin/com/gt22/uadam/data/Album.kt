package com.gt22.uadam.data

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.gt22.uadam.Loader
import com.gt22.uadam.utils.arr
import com.gt22.uadam.utils.str
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

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
        songs = loadSongs(path, format, this)
    }

    override fun createRoot(parent: BaseData?, path: Path) {
        if(parent !is Author) {
            throw IllegalArgumentException("Album should be only created with a parent author")
        }
        super.createRoot(parent, path)
        songs = loadSongs(path, format, this)
    }

    private fun loadSongs(path: Path, format: String, album: Album): Map<String, Song> {
        return mapOf(*Files.list(path)
                .filter { it.fileName.toString().endsWith(format) }
                .sorted()
                .map { Song().apply { loadSong(it, album) } }
                .map { Pair(it.name, it) }.toList().toTypedArray())
    }


    override fun createRemote(json: JsonObject, name: String, parent: BaseData?) {
        if(parent !is Author) {
            throw IllegalArgumentException("Album should be only created with a parent author")
        }
        super.createRemote(json, name, parent)
        val songs = mutableMapOf<String, Song>()
        this.songs = songs
        json["children"].arr.map(JsonElement::str).forEach {
            val s = Song()
            s.loadSongRemote(it, this)
            songs[it] = s
        }
    }

    override fun hasData(): Boolean {
        return songs.isNotEmpty()
    }
}