package com.example.android_tv_frontend.data

import com.example.android_tv_frontend.R
import com.example.android_tv_frontend.model.ContentItem
import com.example.android_tv_frontend.model.Section

/**
 * Provides placeholder sections and items for the Home screen.
 */
object PlaceholderRepository {

    fun getHomeSections(): List<Section> {
        val continuarViendo = Section(
            id = "continue",
            title = "Seguí viendo",
            items = listOf(
                ContentItem("rogue-one", "Rogue One", R.drawable.ic_launcher_fallback, 0.4f),
                ContentItem("ex-machina", "Ex Machina", R.drawable.ic_launcher_fallback, 0.3f),
                ContentItem("sing-street", "Sing Street", R.drawable.ic_launcher_fallback, 0.6f),
                ContentItem("2012", "2012", R.drawable.ic_launcher_fallback, 0.1f),
                ContentItem("ad-astra", "Ad Astra", R.drawable.ic_launcher_fallback, 0.2f),
            )
        )

        val destacados = Section(
            id = "featured",
            title = "Destacados",
            items = (1..10).map {
                ContentItem("featured-$it", "Destacado $it", R.drawable.ic_launcher_fallback)
            }
        )

        val populares = Section(
            id = "popular",
            title = "Populares",
            items = (1..12).map {
                ContentItem("popular-$it", "Popular $it", R.drawable.ic_launcher_fallback)
            }
        )

        val tendencias = Section(
            id = "trending",
            title = "Tendencias",
            items = (1..12).map {
                ContentItem("trend-$it", "Tendencia $it", R.drawable.ic_launcher_fallback)
            }
        )

        return listOf(continuarViendo, destacados, populares, tendencias)
    }
}
