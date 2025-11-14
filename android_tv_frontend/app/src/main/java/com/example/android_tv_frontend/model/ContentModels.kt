package com.example.android_tv_frontend.model

import androidx.annotation.DrawableRes

/**
 * Data model representing a single content item shown as a TV card in carousels.
 */
data class ContentItem(
    val id: String,
    val title: String,
    @DrawableRes val posterRes: Int,
    val progress: Float = 0f
)

/**
 * Data model representing a horizontal section (row) with a title and content items.
 */
data class Section(
    val id: String,
    val title: String,
    val items: List<ContentItem>
)
