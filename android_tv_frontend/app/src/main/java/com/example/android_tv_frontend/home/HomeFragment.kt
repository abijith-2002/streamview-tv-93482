package com.example.android_tv_frontend.home

import android.content.Context
import android.graphics.Outline
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
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

        // Configure ListRowPresenter with precise item spacing and paddings.
        // Use default constructor compatible with our leanback version.
        val listRowPresenter = object : ListRowPresenter() {
            override fun createRowViewHolder(parent: ViewGroup): ViewHolder {
                val vh = super.createRowViewHolder(parent)

                // Apply safe margins and vertical padding to the row root view
                val safeH = parent.resources.getDimensionPixelSize(R.dimen.tv_safe_margin_horizontal)
                val vPad = parent.resources.getDimensionPixelSize(R.dimen.row_vertical_padding)
                vh.view.setPadding(safeH, vPad, safeH, vPad)
                vh.view.clipToPadding = false

                return vh
            }
        }.apply {
            shadowEnabled = false
            selectEffectEnabled = false
        }

        // Recycler/Row adapter using ListRowPresenter
        rowsAdapter = ArrayObjectAdapter(listRowPresenter)

        // Data stub to mirror design
        val continueWatching = buildContinueWatchingItems()
        val tvChannels = buildTvChannelItems()

        // Row 1: Seguí viendo
        rowsAdapter.add(buildListRow(getString(R.string.row_continue_watching), continueWatching))

        // Row 2: Canales de TV
        rowsAdapter.add(buildListRow(getString(R.string.row_tv_channels), tvChannels))

        adapter = rowsAdapter

        setOnItemViewClickedListener(OnItemViewClickedListener { _, _, _, _ ->
            // Handle OK/Enter selection: for now, no-op
        })

        setOnItemViewSelectedListener { _, _, _, _ ->
            // Background or hero updates could be placed here
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
                progress = 0.40f,
                preferLarge = true
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
    val progress: Float = 0f,
    val preferLarge: Boolean = false
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
                // Focus scaling and highlight/outline
                val scale = if (selected) 1.06f else 1.0f
                animate().scaleX(scale).scaleY(scale).setDuration(120L).start()

                // Info area tint
                setInfoAreaBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        if (selected) R.color.op_card_bg_focus else R.color.op_card_bg
                    )
                )

                // Apply outline/glow
                foreground = if (selected)
                    ContextCompat.getDrawable(context, R.drawable.op_focus_outline)
                else null

                // Elevation for subtle shadow
                elevation = if (selected)
                    context.resources.getDimension(R.dimen.focus_shadow_elevation)
                else 0f
            }
        }

        // Rounded corners to match design
        val corner = context.resources.getDimensionPixelSize(R.dimen.card_corner_radius)
        cardView.clipToOutline = true
        cardView.setBackgroundColor(ContextCompat.getColor(context, R.color.op_card_bg))
        cardView.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, corner.toFloat())
            }
        }

        // Default size; will be updated per item (large vs normal)
        val defaultW = context.resources.getDimensionPixelSize(R.dimen.card_width)
        val defaultH = context.resources.getDimensionPixelSize(R.dimen.card_height)
        cardView.setMainImageDimensions(defaultW, defaultH)

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true

        // Colors
        cardView.setInfoAreaBackgroundColor(ContextCompat.getColor(context, R.color.op_card_bg))
        cardView.titleText = ""
        cardView.contentText = ""

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
        val vh = viewHolder as ViewHolder
        val cardView = vh.cardView
        val card = item as CardItem

        // Size according to Figma: first card in continue watching is larger
        val width = if (card.preferLarge)
            cardView.context.resources.getDimensionPixelSize(R.dimen.card_width_large)
        else
            cardView.context.resources.getDimensionPixelSize(R.dimen.card_width)
        val height = if (card.preferLarge)
            cardView.context.resources.getDimensionPixelSize(R.dimen.card_height_large)
        else
            cardView.context.resources.getDimensionPixelSize(R.dimen.card_height)
        cardView.setMainImageDimensions(width, height)

        // Title
        cardView.titleText = card.title

        // Title style adjustments via internal TextView if present
        val titleView = cardView.findViewById<View>(androidx.leanback.R.id.title_text)
        if (titleView is TextView) {
            titleView.setTextColor(ContextCompat.getColor(cardView.context, R.color.op_surface))
            titleView.textSize = 18f
            titleView.setTypeface(titleView.typeface, android.graphics.Typeface.BOLD)
        }

        // Load images from resources (keep as currently wired)
        cardView.mainImage = ContextCompat.getDrawable(cardView.context, card.imageRes)

        // Add progress bar to info area
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
        val container = FrameLayout(context)

        val barHeight = context.resources.getDimensionPixelSize(R.dimen.progress_height)
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            barHeight
        )
        params.topMargin = context.resources.getDimensionPixelSize(R.dimen.progress_top_margin)

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
        val fg = View(context).apply {
            setBackgroundColor(ContextCompat.getColor(context, R.color.op_secondary))
        }
        val fgParams = FrameLayout.LayoutParams(0, barHeight)
        container.addView(fg, fgParams)

        // Post to measure after layout to compute width precisely
        container.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View?,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                container.removeOnLayoutChangeListener(this)
                val total = container.width
                val target = (total * clamped).toInt()
                fg.updateLayoutParams<FrameLayout.LayoutParams> {
                    width = target
                }
            }
        })

        container.layoutParams = params
        return container
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {
        val vh = viewHolder as ViewHolder
        // Clear image to free memory
        vh.cardView.mainImage = null
        vh.cardView.foreground = null
    }
}
