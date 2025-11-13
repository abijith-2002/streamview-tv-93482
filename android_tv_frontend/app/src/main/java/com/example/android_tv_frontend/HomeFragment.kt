package com.example.android_tv_frontend

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android_tv_frontend.R

/**
 * PUBLIC_INTERFACE
 * HomeFragment builds the TV Home using Leanback rows:
 * - Custom pill-shaped top navigation bar
 * - Hero banner carousel rail (W=1744, H=444 @1080p)
 * - Multiple landscape rails with scale-on-focus and white stroke border
 * - DPAD bounded navigation on navbar, default "Inicio" focus when navbar is focused
 * - Responsive using dimension resources
 */
class HomeFragment : Fragment(R.layout.fragment_home_container) {

    private lateinit var rowsFragment: RowsSupportFragment
    private lateinit var topNavView: TopNavBarView

    private val navItems = listOf(
        NavItem(icon = R.drawable.ic_nav_search, label = "Search", isSearch = true),
        NavItem(icon = 0, label = "Inicio"),
        NavItem(icon = 0, label = "Peliculas"),
        NavItem(icon = 0, label = "Series"),
        NavItem(icon = 0, label = "TV en vivo"),
        NavItem(icon = 0, label = "Kids"),
        NavItem(icon = 0, label = "Mis Contenidos"),
    )

    private val rowsAdapter: ArrayObjectAdapter by lazy {
        ArrayObjectAdapter(ListRowPresenter().apply {
            shadowEnabled = false
            selectEffectEnabled = false
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topNavView = view.findViewById(R.id.top_nav)
        rowsFragment = childFragmentManager.findFragmentById(R.id.rows_fragment) as RowsSupportFragment

        rowsFragment.adapter = rowsAdapter
        rowsFragment.setOnItemViewClickedListener { _, item, _, _ ->
            // Handle clicks if needed (playback or open detail)
        }

        // Setup top nav with pills and bounded DPAD
        topNavView.setItems(navItems)
        topNavView.setOnNavEventListener(object : TopNavBarView.OnNavEventListener {
            override fun onItemSelected(position: Int, label: String) {
                // Scroll to sections if desired; default selects "Inicio"
            }
        })

        buildRows()
        applyWindowInsets()
    }

    private fun buildRows() {
        // Row 0: Hero banner carousel rail between navbar and "Seguí viendo"
        val heroPresenter = BannerPresenter()
        val heroAdapter = ArrayObjectAdapter(heroPresenter)
        heroAdapter.addAll(0, sampleHeroBanners())

        rowsAdapter.add(ListRow(null, heroAdapter))

        // Row 1: "Seguí viendo"
        val landscapePresenter = LandscapeCardPresenter()
        val continueAdapter = ArrayObjectAdapter(landscapePresenter)
        continueAdapter.addAll(0, sampleContinueWatching())
        rowsAdapter.add(ListRow(createHeader(getString(R.string.row_continue_watching)), continueAdapter))

        // Row 2: "Para ti"
        val forYouAdapter = ArrayObjectAdapter(landscapePresenter)
        forYouAdapter.addAll(0, sampleForYou())
        rowsAdapter.add(ListRow(createHeader(getString(R.string.row_for_you)), forYouAdapter))

        // Row 3: "TV en vivo" sample rail
        val liveAdapter = ArrayObjectAdapter(landscapePresenter)
        liveAdapter.addAll(0, sampleLiveChannels())
        rowsAdapter.add(ListRow(createHeader(getString(R.string.row_live_tv)), liveAdapter))
    }

    private fun createHeader(title: String): HeaderItem {
        return HeaderItem(title)
    }

    private fun applyWindowInsets() {
        val container: ViewGroup = requireView().findViewById(R.id.home_root)
        ViewCompat.setOnApplyWindowInsetsListener(container) { v, insets ->
            // We do padding via dimen resources already; keep minimal changes here
            insets
        }
    }

    fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Let top navbar bound ArrowLeft/ArrowRight at ends
        return topNavView.handleKey(keyCode)
    }

    // Sample data builders
    private fun sampleHeroBanners(): List<BannerItem> = listOf(
        BannerItem(title = "Banner 1", imageUrl = null, imageResId = R.drawable.banner_sample_1),
        BannerItem(title = "Banner 2", imageUrl = null, imageResId = R.drawable.banner_sample_2),
        BannerItem(title = "Banner 3", imageUrl = null, imageResId = R.drawable.banner_sample_3),
    )

    private fun sampleContinueWatching(): List<LandscapeItem> = listOf(
        LandscapeItem("Rogue One", R.drawable.land_rogue_one),
        LandscapeItem("Ex Machina", R.drawable.land_ex_machina),
        LandscapeItem("Sing Street", R.drawable.land_sing_street),
        LandscapeItem("2012", R.drawable.land_2012),
        LandscapeItem("Ad Astra", R.drawable.land_ad_astra),
    )

    private fun sampleForYou(): List<LandscapeItem> = listOf(
        LandscapeItem("Arrival", R.drawable.land_arrival),
        LandscapeItem("Interstellar", R.drawable.land_interstellar),
        LandscapeItem("Blade Runner 2049", R.drawable.land_blade_runner),
        LandscapeItem("Gravity", R.drawable.land_gravity),
        LandscapeItem("Passengers", R.drawable.land_passengers),
    )

    private fun sampleLiveChannels(): List<LandscapeItem> = listOf(
        LandscapeItem("HBO Channel", R.drawable.land_live_hbo),
        LandscapeItem("Claro Sports", R.drawable.land_live_claro),
        LandscapeItem("News 24", R.drawable.land_live_news),
        LandscapeItem("Kids Club", R.drawable.land_live_kids),
    )

    companion object {
        // PUBLIC_INTERFACE
        fun newInstance(): HomeFragment = HomeFragment()
    }
}

/**
 * Top pill-shaped navigation data model.
 */
data class NavItem(val icon: Int, val label: String, val isSearch: Boolean = false)

/**
 * Banner rail item.
 */
data class BannerItem(val title: String, val imageUrl: String? = null, val imageResId: Int? = null)

/**
 * Landscape card item.
 */
data class LandscapeItem(val title: String, val imageResId: Int)

/**
 * BannerPresenter shows the hero banner carousel items.
 * Size adapts using dimens (approx 1744x444 on 1080p).
 */
class BannerPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = BannerView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val banner = item as BannerItem
        val view = viewHolder.view as BannerView
        view.bind(banner)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        // no-op
    }
}

/**
 * LandscapeCardPresenter shows landscape content posters with elevation-on-focus and white stroke on focus.
 */
class LandscapeCardPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = LandscapeCardView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val v = viewHolder.view as LandscapeCardView
        val landscape = item as LandscapeItem
        v.bind(landscape)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
}

/**
 * Diff util option if we later switch to ListAdapter; shown here for potential future optimization.
 */
/**
 * Type-safe DiffUtil callback for BannerItem.
 */
class BannerItemDiff : DiffUtil.ItemCallback<BannerItem>() {
    override fun areItemsTheSame(oldItem: BannerItem, newItem: BannerItem): Boolean {
        // Use title as a pseudo ID for placeholder demo; replace with real IDs when available
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: BannerItem, newItem: BannerItem): Boolean {
        return oldItem.title == newItem.title && oldItem.imageResId == newItem.imageResId && oldItem.imageUrl == newItem.imageUrl
    }
}

/**
 * Type-safe DiffUtil callback for LandscapeItem.
 */
class LandscapeItemDiff : DiffUtil.ItemCallback<LandscapeItem>() {
    override fun areItemsTheSame(oldItem: LandscapeItem, newItem: LandscapeItem): Boolean {
        // Title used as stable key for demo data
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: LandscapeItem, newItem: LandscapeItem): Boolean {
        return oldItem.title == newItem.title && oldItem.imageResId == newItem.imageResId
    }
}
