package com.gt22.uadam.data

import com.google.gson.JsonObject
import com.gt22.uadam.utils.obj
import com.gt22.uadam.utils.str
import java.nio.file.Path

abstract class BaseData<T> where T : BaseData<T>, T : IContext{
    lateinit var name: String
        protected set
    lateinit var img: String
        protected set
    lateinit var title: String
        protected set
    lateinit var format: String
        protected set
    open var parent: BaseData<T>? = null
        protected set
    open lateinit var children: Map<String, BaseData<T>>
        protected set

    open val namedElements: Map<String, BaseData<T>> by lazy(::doIndex)

    @Suppress("UNCHECKED_CAST")
    val context: T
        get() {
            var c = this
            while(c !is IContext) {
                c = c.parent!!
            }
            return c as T
        }

    abstract val path: String

    internal open fun load(json: JsonObject, name: String, parent: BaseData<T>?, path: Path) {
        this.parent = parent
        this.name = name
        img = json["img"]?.str ?: parent?.img ?: ""
        format = json["format"]?.str ?: parent?.format ?: ".mp3"

        title = json["title"]?.str ?: name
    }

    internal open fun createRoot(parent: BaseData<T>?, path: Path) {
        this.parent = parent
        name = "_root"
        img = parent?.img ?: ""
        title = ""
        format = parent?.format ?: ".mp3"
    }

    internal open fun createRemote(json: JsonObject, name: String, parent: BaseData<T>?) {
        this.parent = parent
        this.name = name
        val meta = json["meta"].obj
        img = meta["img"]?.str ?: parent?.img ?: ""
        format = meta["format"]?.str ?: parent?.format ?: ".mp3"

        title = meta["title"]?.str ?: name
    }

    internal open fun hasData(): Boolean {
        return true
    }

    operator fun get(vararg names: String): BaseData<T>? {
        var cur: BaseData<T>? = this
        names.forEach {
            cur = cur?.get(it)
        }
        return cur
    }

    operator fun get(name: String) = children[name]

    protected open fun doIndex(): Map<String, BaseData<T>> {
        val m = mutableMapOf<String, BaseData<T>>()
        children.values.map {
            it.namedElements.toMap()
        }.forEach(m::putAll)
        m.putAll(children)
        m.remove("_root")
        return m
    }

    open fun search(s: String): List<BaseData<T>> {
        if(s.isEmpty()) {
            return children.values.toList()
        }
        if('/' in s) {
            return search(*s.split('/').toTypedArray())
        }
        val lower = s.toLowerCase()
        val ret = mutableListOf<BaseData<T>>()
        namedElements.forEach { (name, data) ->
            if(lower in name.toLowerCase()) {
                ret.add(data)
            }
        }
        return ret
    }

    open fun search(vararg s: String): List<BaseData<T>> {
        var cur = listOf(this)
        var next = mutableListOf<BaseData<T>>()
        s.forEach { name ->
            cur.forEach { data ->
                next.addAll(data.search(name))
            }
            cur = next
            next = mutableListOf()
        }
        return cur
    }

}