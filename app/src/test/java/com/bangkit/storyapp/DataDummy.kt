package com.bangkit.storyapp

import com.bangkit.storyapp.data.remote.model.Story

object DataDummy {
    fun generateDummyStoryResponse(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val quote = Story(
                i.toString(),
                "name $i",
                "description $i",
                "photo $i",
                "timestamp $i",
                null,
                null
            )
            items.add(quote)
        }
        return items
    }
}