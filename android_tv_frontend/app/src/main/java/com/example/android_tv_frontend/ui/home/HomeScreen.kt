package com.example.android_tv_frontend.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.android_tv_frontend.data.PlaceholderRepository
import com.example.android_tv_frontend.model.ContentItem
import com.example.android_tv_frontend.model.Section

/**
 * PUBLIC_INTERFACE
 * HomeRoot renders the Android TV Home screen with:
 * - Top navigation bar
 * - Multiple content rows (carousels) with focus scaling
 * Parameters:
 *  - onNavSelected: callback for top nav interactions
 */
@Composable
fun HomeRoot(
    onNavSelected: (String) -> Unit = {}
) {
    val sections = remember { PlaceholderRepository.getHomeSections() }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 48.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            TopNavBar(
                items = listOf("Home", "Películas", "Series", "TV en vivo", "Kids", "Mis Contenidos", "Perfil"),
                selectedIndex = 0,
                onItemSelected = onNavSelected
            )

            sections.forEach { section ->
                SectionRow(section = section)
            }
        }
    }
}

@Composable
private fun TopNavBar(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (String) -> Unit,
) {
    val scroll = rememberScrollState()
    val homeFocusRequester = remember { FocusRequester() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(34.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .horizontalScroll(scroll),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, text ->
            val isSelected = index == selectedIndex
            NavPill(
                text = text,
                selected = isSelected,
                onClick = { onItemSelected(text) },
                modifier = if (isSelected) Modifier.focusRequester(homeFocusRequester) else Modifier
            )
        }
    }

    // Ensure initial focus starts on Home pill
    LaunchedEffect(Unit) {
        homeFocusRequester.requestFocus()
    }
}

@Composable
private fun NavPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (focused) 1.06f else 1f, label = "nav-scale")

    val bg = when {
        focused -> MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
        selected -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f)
        else -> Color.Transparent
    }

    Box(
        modifier = modifier
            .scale(scale)
            .background(bg, RoundedCornerShape(28.dp))
            .padding(horizontal = 18.dp, vertical = 10.dp)
            .focusable(true)
            .onFocusChanged { state -> focused = state.isFocused },
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        )
    }
}

@Composable
private fun SectionRow(section: Section) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BasicText(
            text = section.title,
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
        )
        HorizontalCards(items = section.items)
        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
    }
}

@Composable
private fun HorizontalCards(items: List<ContentItem>) {
    val scroll = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scroll),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items.forEach { item ->
            ContentCard(item = item)
        }
    }
}

@Composable
private fun ContentCard(
    item: ContentItem,
    width: Dp = 260.dp,
    ratio: Float = 16f / 9f,
    cornerRadius: Dp = 12.dp,
    shadowElevation: Dp = 10.dp,
    shape: Shape = RoundedCornerShape(12.dp),
) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (focused) 1.08f else 1f, label = "card-scale")

    Box(
        modifier = Modifier
            .scale(scale)
            .shadow(if (focused) shadowElevation else 2.dp, shape = shape, clip = true)
            .background(MaterialTheme.colorScheme.surface, shape)
            .width(width)
            .aspectRatio(ratio)
            .focusable(true)
            .onFocusChanged { state -> focused = state.isFocused }
            .padding(0.dp),
    ) {
        // Placeholder visual using a colored surface and the app vector launcher as "poster"
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = item.posterRes),
            contentDescription = item.title
        )

        // Title bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            BasicText(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Progress bar (if any)
        if (item.progress > 0f) {
            ProgressBar(
                progress = item.progress,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 6.dp,
    trackColor: Color = Color(0xFF2C2C2C),
    progressColor: Color = MaterialTheme.colorScheme.secondary // amber
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(trackColor, RoundedCornerShape(percent = 50))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                .height(height)
                .background(progressColor, RoundedCornerShape(percent = 50))
        )
    }
}
