package com.genthur.storyapp

import com.genthur.storyapp.data.remote.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                photoUrl = "photoUrl $i",
                createdAt =  "created at $i",
                name = "name $i",
                description = "description $i",
                lat = i.toDouble(),
                lon = i.toDouble(),
                id = "id $i",
            )
            items.add(story)
        }
        return items
    }
}