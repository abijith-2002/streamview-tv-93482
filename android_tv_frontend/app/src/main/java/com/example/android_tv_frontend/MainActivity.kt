package com.example.android_tv_frontend

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import com.example.android_tv_frontend.ui.home.HomeRoot
import com.example.android_tv_frontend.ui.theme.OceanTVTheme

/**
 * Main Activity for Android TV
 * Extends FragmentActivity for Leanback compatibility
 * Renders the Home screen using Compose for TV.
 */
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OceanTVTheme(darkTheme = true) {
                HomeRoot(
                    onNavSelected = { /* Stub: hook up navigation destinations here */ }
                )
            }
        }
    }
}
