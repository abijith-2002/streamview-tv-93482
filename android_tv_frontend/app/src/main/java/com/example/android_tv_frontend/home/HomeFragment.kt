package com.example.android_tv_frontend.home

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.PresenterSelector
import com.example.android_tv_frontend.R

/**
 * PUBLIC_INTERFACE
 * HomeFragment
 * This Leanback BrowseSupportFragment composes the Home screen with multiple horizontal rows
 * (e.g., "Seguí viendo", "Canales de TV"), TV-optimized ImageCards, and DPAD focus behavior.
 *
 * Parameters: none
 * Returns: A fragment UI with rows and carousels ready for DPAD navigation.
 */
class HomeFragment : BrowseSupportFragment() {

    private lateinit var rowsAdapter: ArrayObjectAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        buildUI()
    }

    private fun buildUI() {
        // Headers and colors
        title = getString(R.string.app_name)
        brandColor = ContextCompat.getColor(requireContext(), R.color.op_primary)
        searchAffordanceColor = ContextCompat.getColor(requireContext(), R.color.op_secondary)

        // Set headers style visible
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        // Recycler/Row adapter using ListRowPresenter
        rowsAdapter = ArrayObjectAdapter(ListRowPresenter().apply {
            shadowEnabled = true
            selectEffectEnabled = true
        })

        // Data stub to mirror design
        val continueWatching = buildContinueWatchingItems()
        val tvChannels = buildTvChannelItems()

        // Row 1: Seguí viendo
        rowsAdapter.add(buildListRow("Seguí viendo", continueWatching))

        // Row 2: Canales de TV
        rowsAdapter.add(buildListRow("Canales de TV", tvChannels))

        adapter = rowsAdapter

        setOnItemViewClickedListener { _, _, _, _ ->
            // Handle OK/Enter selection: for now, no-op
        }

        setOnItemViewSelectedListener { _, _, _, _ ->
            // Optionally update background or hero when selection changes
        }
    }

    private fun buildListRow(header: String, items: List<CardItem>): ListRow {
        val presenterSelector = CardPresenterSelector(requireContext())
        val listRowAdapter = ArrayObjectAdapter(presenterSelector)
        items.forEach { listRowAdapter.add(it) }
        val headerItem = HeaderItem(header)
        return ListRow(headerItem, listRowAdapter)
    }

    private fun buildContinueWatchingItems(): List<CardItem> {
        // Map to the design-provided images
        return listOf(
            CardItem(
                title = "Rogue One",
                imageRes = R.drawable.figma_image_1_41,
                progress = 0.4f
            ),
            CardItem(
                title = "Ex Machina",
                imageRes = R.drawable.figma_image_1_68,
                progress = 0.35f
            ),
            CardItem(
                title = "Sing Street",
                imageRes = R.drawable.figma_image_1_85,
                progress = 0.35f
            ),
            CardItem(
                title = "2012",
                imageRes = R.drawable.figma_image_1_102,
                progress = 0.35f
            ),
            CardItem(
                title = "Ad Astra",
                imageRes = R.drawable.figma_image_1_119,
                progress = 0.35f
            ),
        )
        }

    private fun buildTvChannelItems(): List<CardItem> {
        return listOf(
            CardItem(
                title = "Marca Claro Radio",
                imageRes = R.drawable.figma_image_1_154,
                progress = 0.25f
            ),
            CardItem(
                title = "E.T.",
                imageRes = R.drawable.figma_image_1_179,
                progress = 0.25f
            ),
            CardItem(
                title = "Canal TV",
                imageRes = R.drawable.figma_image_1_218,
                progress = 0.25f
            ),
        )
    }
}

/**
 * Model for a card item in the rows
 */
data class CardItem(
    val title: String,
    val imageRes: Int,
    val progress: Float = 0f
)

/**
 * PresenterSelector that returns our custom card presenter.
 */
class CardPresenterSelector(private val context: Context) : PresenterSelector() {
    private val presenter: Presenter = OPImageCardPresenter(context)
    override fun getPresenter(item: Any?): Presenter = presenter
}

/**
 * Custom presenter using ImageCardView directly to avoid dependency on ImageCardViewPresenter.
 * Provides Ocean Professional colors and DPAD focus scaling/highlight, and an optional progress bar.
 */
class OPImageCardPresenter(private val context: Context) : Presenter() {

    // ViewHolder that wraps an ImageCardView
    class ViewHolder(val cardView: ImageCardView) : Presenter.ViewHolder(cardView)

    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        val cardView = object : ImageCardView(parent.context) {
            override fun setSelected(selected: Boolean) {
                super.setSelected(selected)
                // Focus scaling and highlight
                val scale = if (selected) 1.06f else 1.0f
                this.animate().scaleX(scale).scaleY(scale).setDuration(120L).start()
                setInfoAreaBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        if (selected) R.color.op_primary else R.color.op_card_bg
                    )
                )
            }
        }

        // Size based on TV-friendly card size close to design
        cardView.setMainImageDimensions(412, 232)
        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true

        // Colors
        cardView.setBackgroundColor(ContextCompat.getColor(context, R.color.op_card_bg))
        cardView.setInfoAreaBackgroundColor(ContextCompat.getColor(context, R.color.op_card_bg))
        cardView.titleText = ""
        cardView.contentText = ""

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
        val vh = viewHolder as ViewHolder
        val cardView = vh.cardView
        val card = item as CardItem

        // Title
        cardView.titleText = card.title

        // Title style adjustments via internal TextView if present
        val titleView = cardView.findViewById<View>(androidx.leanback.R.id.title_text)
        if (titleView is TextView) {
            titleView.setTextColor(ContextCompat.getColor(cardView.context, R.color.op_surface))
            titleView.textSize = 18f
            titleView.setTypeface(titleView.typeface, android.graphics.Typeface.BOLD)
        }

        // Load images from resources
        cardView.mainImage = ContextCompat.getDrawable(cardView.context, card.imageRes)

        // Ensure an info area progress bar exists
        val infoField = cardView.findViewById<View>(androidx.leanback.R.id.info_field)
        if (infoField is ViewGroup) {
            // Remove previous progress bar if any
            for (i in infoField.childCount - 1 downTo 0) {
                val v = infoField.getChildAt(i)
                if (v.tag == "progress_bar") infoField.removeViewAt(i)
            }
            val progressBar = buildProgressBar(cardView.context, card.progress)
            progressBar.tag = "progress_bar"
            infoField.addView(progressBar)
        }
    }

    private fun buildProgressBar(context: Context, progress: Float): View {
        val dp = context.resources.displayMetrics.density
        val barHeight = (8 * dp).toInt()

        val container = FrameLayout(context)
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            barHeight
        )
        params.topMargin = (8 * dp).toInt()

        // Background bar
        val bg = View(context).apply {
            setBackgroundColor(ContextCompat.getColor(context, R.color.op_card_bg_focus))
        }
        val bgParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            barHeight
        )
        container.addView(bg, bgParams)

        // Foreground bar width based on progress
        val clamped = progress.coerceIn(0f, 1f)
        val fgWidth = (clamped * 600).toInt().coerceAtLeast((24 * dp).toInt()) // ensure small but visible
        val fg = View(context).apply {
            setBackgroundColor(ContextCompat.getColor(context, R.color.op_secondary))
        }
        val fgParams = FrameLayout.LayoutParams(fgWidth, barHeight)
        container.addView(fg, fgParams)

        container.layoutParams = params
        return container
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {
        val vh = viewHolder as ViewHolder
        // Clear image to free memory
        vh.cardView.mainImage = null
    }
}
