package com.bangkit.storyapp.data.model

data class HeaderStories(
    val error: Boolean,
    val message: String,
    val listStory: List<Story>
)

data class HeaderStory(
    val error: Boolean,
    val message: String,
    val story: Story
)

data class Story(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Float?,
    val lon: Float?
)
