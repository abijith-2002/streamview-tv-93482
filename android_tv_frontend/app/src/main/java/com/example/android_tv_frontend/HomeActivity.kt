package com.example.android_tv_frontend

import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.FragmentActivity
import com.example.android_tv_frontend.R

/**
 * PUBLIC_INTERFACE
 * HomeActivity is the TV app entry that hosts the Leanback-based HomeFragment.
 * It ensures TV compatibility (FragmentActivity) and forwards back press and DPAD events.
 */
class HomeActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Use a simple container layout that hosts the HomeFragment
        setContentView(R.layout.activity_home)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container, HomeFragment.newInstance())
                .commitNow()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Allow Fragment to handle DPAD navigation nuances if needed
        val fragment = supportFragmentManager.findFragmentById(R.id.home_fragment_container)
        if (fragment is HomeFragment && fragment.onKeyDown(keyCode, event)) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * PUBLIC_INTERFACE
     * For overscan safety, provides a common Rect used by children to compute safe paddings.
     */
    fun getOverscanSafeInsets(): Rect {
        // Assume ~8% overscan; we keep critical UI 48dp from edges and allow flexible rails.
        val left = resources.getDimensionPixelSize(R.dimen.overscan_safe_margin)
        val top = resources.getDimensionPixelSize(R.dimen.overscan_safe_margin)
        val right = resources.getDimensionPixelSize(R.dimen.overscan_safe_margin)
        val bottom = resources.getDimensionPixelSize(R.dimen.overscan_safe_margin)
        return Rect(left, top, right, bottom)
    }
}
