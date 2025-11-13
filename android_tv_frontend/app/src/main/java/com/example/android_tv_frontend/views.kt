package com.example.android_tv_frontend

import android.content.Context
import android.graphics.*
import android.graphics.Outline
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.bumptech.glide.Glide

/**
 * PUBLIC_INTERFACE
 * BannerView renders a hero banner image with rounded corners and white focus border.
 */
class BannerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val radius = resources.getDimension(R.dimen.radius_lg)
    private val borderWidthFocused = resources.getDimension(R.dimen.focus_stroke)
    private val img = ImageView(context).apply {
        scaleType = ImageView.ScaleType.CENTER_CROP
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
    }

    private val paintBorder = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = borderWidthFocused
    }

    init {
        val w = resources.getDimensionPixelSize(R.dimen.banner_width)
        val h = resources.getDimensionPixelSize(R.dimen.banner_height)
        layoutParams = LayoutParams(w, h)
        setWillNotDraw(false)
        addView(img)
        background = ContextCompat.getDrawable(context, R.drawable.bg_surface_elevated)
        isFocusable = true
        isFocusableInTouchMode = true

        // Clip the entire banner to rounded outline
        clipToOutline = true
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, radius)
            }
        }

        elevation = resources.getDimension(R.dimen.elevation_default)
        setOnFocusChangeListener { _, hasFocus ->
            animate().scaleX(if (hasFocus) 1.02f else 1.0f)
                .scaleY(if (hasFocus) 1.02f else 1.0f)
                .setDuration(120)
                .start()
            invalidate()
        }
    }

    fun bind(item: BannerItem) {
        val placeholder = R.drawable.banner_placeholder
        if (item.imageResId != null) {
            img.setImageResource(item.imageResId)
        } else if (item.imageUrl != null) {
            Glide.with(context).load(item.imageUrl).placeholder(placeholder).into(img)
        } else {
            img.setImageResource(placeholder)
        }
        contentDescription = item.title
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isFocused) {
            val inset = borderWidthFocused / 2
            val rect = RectF(inset, inset, width - inset, height - inset)
            canvas.drawRoundRect(rect, radius, radius, paintBorder)
        }
    }
}

/**
 * PUBLIC_INTERFACE
 * LandscapeCardView renders a landscape-oriented poster with title overlay and white focus border.
 */
class LandscapeCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val radius = resources.getDimension(R.dimen.radius_md)
    private val borderWidth = resources.getDimension(R.dimen.focus_stroke)

    private val image = ImageView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        scaleType = ImageView.ScaleType.CENTER_CROP
    }

    private val titleOverlay = TextView(context).apply {
        setPadding(resources.getDimensionPixelSize(R.dimen.spacing_md))
        setTextColor(Color.WHITE)
        textSize = 18f
        typeface = Typeface.DEFAULT_BOLD
        setBackgroundColor(Color.parseColor("#66000000"))
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = android.view.Gravity.BOTTOM
        }
    }

    private val paintBorder = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = borderWidth
    }

    init {
        val w = resources.getDimensionPixelSize(R.dimen.land_card_width)
        val h = resources.getDimensionPixelSize(R.dimen.land_card_height)
        layoutParams = LayoutParams(w, h)
        setWillNotDraw(false)
        addView(image)
        addView(titleOverlay)
        background = ContextCompat.getDrawable(context, R.drawable.bg_surface_elevated)
        elevation = resources.getDimension(R.dimen.elevation_default)

        // Clip the card to a rounded outline
        clipToOutline = true
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, radius)
            }
        }

        isFocusable = true
        isFocusableInTouchMode = true

        setOnFocusChangeListener { _, hasFocus ->
            animate().scaleX(if (hasFocus) 1.05f else 1.0f)
                .scaleY(if (hasFocus) 1.05f else 1.0f)
                .setDuration(120)
                .start()
            invalidate()
        }
    }

    fun bind(item: LandscapeItem) {
        image.setImageResource(item.imageResId)
        titleOverlay.text = item.title
        contentDescription = item.title
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isFocused) {
            val inset = borderWidth / 2
            val rect = RectF(inset, inset, width - inset, height - inset)
            canvas.drawRoundRect(rect, radius, radius, paintBorder)
        }
    }
}

/**
 * PUBLIC_INTERFACE
 * TopNavBarView renders the pill-shaped navbar with bounded left/right DPAD and default focus on "Inicio".
 * The brand area is left-aligned; the pills are centered horizontally.
 */
class TopNavBarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    interface OnNavEventListener {
        fun onItemSelected(position: Int, label: String)
    }

    private var listener: OnNavEventListener? = null
    private var items: List<NavItem> = emptyList()
    private var selectedIndex = 1 // default to "Inicio" index

    private val pillBgColor = Color.parseColor("#9B0F0F")
    private val textColor = Color.parseColor("#111827")
    private val textColorInactive = Color.parseColor("#7f8282")

    init {
        orientation = HORIZONTAL
        clipToPadding = false
        clipChildren = false
        setPadding(resources.getDimensionPixelSize(R.dimen.spacing_lg))
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            resources.getDimensionPixelSize(R.dimen.navbar_height)
        )
        gravity = android.view.Gravity.CENTER_HORIZONTAL or android.view.Gravity.CENTER_VERTICAL
        isFocusable = true
        isFocusableInTouchMode = true
    }

    fun setOnNavEventListener(l: OnNavEventListener) {
        listener = l
    }

    fun setItems(navItems: List<NavItem>) {
        removeAllViews()
        items = navItems

        val brand = TextView(context).apply {
            text = context.getString(R.string.app_name)
            setTextColor(textColor)
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            val lp = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
            layoutParams = lp
        }
        addView(brand)

        val pillsContainer = LinearLayout(context).apply {
            orientation = HORIZONTAL
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 2f)
            gravity = android.view.Gravity.CENTER_HORIZONTAL
        }
        addView(pillsContainer)

        navItems.forEachIndexed { index, item ->
            val pill = NavPillView(context).apply {
                setText(item.label)
                setIsSearch(item.isSearch)
                setSelectedVisual(index == selectedIndex)
                contentDescription = item.label
                setOnClickListener {
                    setSelectedIndex(index)
                }
                isFocusable = true
                isFocusableInTouchMode = true
            }
            val spacing = resources.getDimensionPixelSize(R.dimen.spacing_md)
            pill.layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                resources.getDimensionPixelSize(R.dimen.nav_pill_height)
            ).apply {
                leftMargin = spacing
                rightMargin = spacing
            }
            pillsContainer.addView(pill)
        }

        // default focus to "Inicio"
        post {
            val focusView = getPillAt(selectedIndex)
            focusView?.requestFocus()
        }
    }

    private fun getPillAt(index: Int): View? {
        // child 0 = brand, 1 = pillsContainer
        val pillsContainer = getChildAt(1) as? ViewGroup ?: return null
        if (index in 0 until pillsContainer.childCount) return pillsContainer.getChildAt(index)
        return null
    }

    fun setSelectedIndex(index: Int) {
        val pillsContainer = getChildAt(1) as? ViewGroup ?: return
        if (index !in 0 until pillsContainer.childCount) return

        val old = selectedIndex
        selectedIndex = index

        for (i in 0 until pillsContainer.childCount) {
            val pill = pillsContainer.getChildAt(i) as NavPillView
            pill.setSelectedVisual(i == selectedIndex)
        }
        listener?.onItemSelected(index, items[index].label)
        if (old != index) {
            getPillAt(index)?.requestFocus()
        }
    }

    fun handleKey(keyCode: Int): Boolean {
        val pillsContainer = getChildAt(1) as? ViewGroup ?: return false
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if (selectedIndex > 0) {
                    setSelectedIndex(selectedIndex - 1)
                }
                // bounded at left end
                return true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (selectedIndex < pillsContainer.childCount - 1) {
                    setSelectedIndex(selectedIndex + 1)
                }
                // bounded at right end
                return true
            }
        }
        return false
    }
}

/**
 * A pill button with rounded background and optional search icon.
 */
class NavPillView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val radius = resources.getDimension(R.dimen.radius_pill)
    private val focusStroke = resources.getDimension(R.dimen.focus_stroke)
    private val textView = TextView(context).apply {
        setPadding(
            resources.getDimensionPixelSize(R.dimen.spacing_lg),
            resources.getDimensionPixelSize(R.dimen.spacing_sm),
            resources.getDimensionPixelSize(R.dimen.spacing_lg),
            resources.getDimensionPixelSize(R.dimen.spacing_sm),
        )
        textSize = 18f
        setTextColor(Color.WHITE)
    }
    private val iconView = ImageView(context).apply {
        setImageResource(R.drawable.ic_nav_search)
        visibility = GONE
    }

    private var selectedVisual = false

    private val paintBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#9B0F0F")
        style = Paint.Style.FILL
    }
    private val paintStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = focusStroke
    }

    init {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            resources.getDimensionPixelSize(R.dimen.nav_pill_height)
        )
        setWillNotDraw(false)
        addView(iconView, LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        addView(textView)
        isFocusable = true
        isFocusableInTouchMode = true

        setOnFocusChangeListener { _, _ -> invalidate() }
    }

    fun setText(text: String) {
        textView.text = text
    }

    fun setIsSearch(isSearch: Boolean) {
        iconView.visibility = if (isSearch) VISIBLE else GONE
        if (isSearch) {
            textView.visibility = GONE
            setPadding(resources.getDimensionPixelSize(R.dimen.spacing_md))
        } else {
            textView.visibility = VISIBLE
            setPadding(0)
        }
    }

    fun setSelectedVisual(selected: Boolean) {
        selectedVisual = selected
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // ensure pill has minimum width for aesthetics
        val minW = resources.getDimensionPixelSize(R.dimen.nav_pill_min_width)
        if (measuredWidth < minW) {
            setMeasuredDimension(minW, measuredHeight)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (selectedVisual) {
            val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
            canvas.drawRoundRect(rect, radius, radius, paintBg)
        }
        if (isFocused) {
            val inset = focusStroke / 2
            val rect = RectF(inset, inset, width - inset, height - inset)
            canvas.drawRoundRect(rect, radius, radius, paintStroke)
        }
    }
}

/**
 * Utility extension to set image by res with optional Glide future support.
 */
fun ImageView.setImageResCompat(@DrawableRes res: Int) {
    setImageResource(res)
}
