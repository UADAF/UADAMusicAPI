package com.gt22.uadam.utils

import com.google.gson.JsonParser

object Instances {

    private val PARSER = JsonParser()

    fun getParser(): JsonParser = PARSER
}