package com.example.android_tv_frontend.home

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
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

        // Recycler/Row adapter
        rowsAdapter = ArrayObjectAdapter(ListRowPresenter().apply {
            shadowEnabled = true
            selectEffectEnabled = true
        })

        // Data stub to mirror design
        val continueWatching = buildContinueWatchingItems(requireContext())
        val tvChannels = buildTvChannelItems(requireContext())

        // Row 1: Seguí viendo
        rowsAdapter.add(buildListRow(requireContext(), "Seguí viendo", continueWatching))

        // Row 2: Canales de TV
        rowsAdapter.add(buildListRow(requireContext(), "Canales de TV", tvChannels))

        adapter = rowsAdapter

        setOnItemViewClickedListener { _, item, _, _ ->
            // Handle OK/Enter selection: for now, no-op
        }

        setOnItemViewSelectedListener { _, item, _, _ ->
            // Optionally update background or hero when selection changes
        }
    }

    private fun buildListRow(context: Context, header: String, items: List<CardItem>): ListRow {
        val presenterSelector = CardPresenterSelector(context)
        val listRowAdapter = ArrayObjectAdapter(presenterSelector)
        items.forEach { listRowAdapter.add(it) }
        val headerItem = HeaderItem(header)
        return ListRow(headerItem, listRowAdapter)
    }

    private fun buildContinueWatchingItems(context: Context): List<CardItem> {
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

    private fun buildTvChannelItems(context: Context): List<CardItem> {
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
 * PresenterSelector that returns our custom card presenter
 */
class CardPresenterSelector(private val context: Context) : PresenterSelector() {
    private val presenter: Presenter by lazy { OPImageCardPresenter(context) }
    override fun getPresenter(item: Any?): Presenter = presenter
}

/**
 * Custom ImageCardPresenter with Ocean Professional colors and DPAD focus scaling/highlight,
 * and an optional progress bar display under the image.
 */
class OPImageCardPresenter(private val context: Context) : ImageCardViewPresenter() {

    init {
        setThemeColor(context)
    }

    private fun setThemeColor(context: Context) {
        // The base ImageCardViewPresenter uses default colors; we adjust in onCreateViewHolder
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
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

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val card = item as CardItem
        val cardView = viewHolder.view as ImageCardView
        cardView.titleText = card.title
        // Ensure styles.xml defines OP.CardTitleText using Leanback's TextAppearance
        cardView.setTitleTextAppearance(R.style.OP_CardTitleText)

        // Load images from resources (we copied Figma PNGs as drawables)
        cardView.mainImage = ContextCompat.getDrawable(cardView.context, card.imageRes)

        // Attach progress bar to the info area
        val infoField = cardView.findViewById<View>(androidx.leanback.R.id.info_field)
        infoField?.let { container ->
            if (container is ViewGroup) {
                // Remove previous progress bar if any
                for (i in container.childCount - 1 downTo 0) {
                    val v = container.getChildAt(i)
                    if (v.tag == "progress_bar") container.removeViewAt(i)
                }
                val progressBar = buildProgressBar(cardView.context, card.progress)
                progressBar.tag = "progress_bar"
                container.addView(progressBar)
            }
        }
    }

    private fun buildProgressBar(context: Context, progress: Float): View {
        val dp = context.resources.displayMetrics.density
        val barHeight = (8 * dp).toInt()

        val container = android.widget.FrameLayout(context)
        val params = android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
            barHeight
        )
        params.topMargin = (8 * dp).toInt()

        // Background
        val bg = View(context)
        bg.setBackgroundColor(ContextCompat.getColor(context, R.color.op_card_bg_focus))
        val bgParams = android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
            barHeight
        )
        container.addView(bg, bgParams)

        // Foreground with primary color width based on progress
        val fg = View(context)
        fg.setBackgroundColor(ContextCompat.getColor(context, R.color.op_secondary))
        val widthPercent = progress.coerceIn(0f, 1f)
        val fgParams = android.widget.FrameLayout.LayoutParams(
            (widthPercent * 600).toInt(), // arbitrary width; FrameLayout width is wrap, but LB info field is full width
            barHeight
        )
        container.addView(fg, fgParams)

        container.layoutParams = params
        return container
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val cardView = viewHolder.view as ImageCardView
        // Clear image to free memory
        cardView.mainImage = null
    }
}
