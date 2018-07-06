package com.gt22.uadam.data

import com.google.gson.JsonObject
import com.gt22.uadam.Loader
import com.gt22.uadam.utils.obj
import java.nio.file.Path

open class Group: BaseData() {

    val context: MusicContext
        get() = parent as MusicContext

    @Suppress("UNCHECKED_CAST")
    var authors: Map<String, Author>
        get() = children as Map<String, Author>
        set(value) {
            children = value
        }

    override val path: String
        get() = "${context.path}$name"

    override fun load(json: JsonObject, name: String, parent: BaseData?, path: Path) {
        if(parent !is MusicContext) {
            throw UnsupportedOperationException("Group should only be created with MusicContext paretn")
        }
        super.load(json, name, parent, path)
        authors = Loader.load(path, this, "author")
    }

    override fun createRoot(parent: BaseData?, path: Path) {
        if(parent !is MusicContext) {
            throw UnsupportedOperationException("Group should only be created with GroupParent (MusicContext) parent")
        }
        super.createRoot(parent, path)
        authors = Loader.load(path, this, "author")
    }

    override fun createRemote(json: JsonObject, name: String, parent: BaseData?) {
        if(parent !is MusicContext && parent !is RemoteMusicContext) {
            throw UnsupportedOperationException("Group should only be created with (Remote)MusicContext parent")
        }
        super.createRemote(json, name, parent)
        authors = Loader.loadRemote(json["children"].obj, this, "author")
    }

    override fun hasData(): Boolean {
        return authors.isNotEmpty()
    }

}