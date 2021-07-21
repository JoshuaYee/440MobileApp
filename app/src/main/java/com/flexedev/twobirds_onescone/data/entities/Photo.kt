package com.flexedev.twobirds_onescone.data.entities

import java.io.File

data class Photo(val file: File) {
    val year: Int
    private val month: Int
    private val day: Int
    val time: Int

    init {
        year = file.parentFile!!.name.toInt()
        val matches = "^(\\d+)_(\\d+)_(\\d+)$".toRegex().matchEntire(file.nameWithoutExtension)
        if (matches != null) {
            month = matches.groupValues[1].toInt()
            day = matches.groupValues[2].toInt()
            time = matches.groupValues[3].toInt()
        } else {
            month = 1
            day = 1
            time = 1
        }
    }

    override fun toString(): String {
        return "Looks like an image"
    }
}
