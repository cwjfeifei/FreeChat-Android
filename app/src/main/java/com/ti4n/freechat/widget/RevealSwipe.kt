package com.ti4n.freechat.widget

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.SnapSpec
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import com.ti4n.freechat.util.clickableSingle
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ResistanceConfig
import androidx.compose.material.Surface
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.SwipeableState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
typealias RevealState = SwipeableState<RevealValue>

/**
 * Return an alternative value if whenClosure is true. Replaces if/else
 */
private fun <T> T.or(orValue: T, whenClosure: T.() -> Boolean): T {
    return if (whenClosure()) orValue else this
}

/**
 * @param onContentClick called on click
 * @param closeOnContentClick if true, returns to unrevealed state on content click
 */
@ExperimentalMaterialApi
@Composable
fun RevealSwipe(
    modifier: Modifier = Modifier,
    enableSwipe: Boolean = true,
    onContentClick: (() -> Unit)? = null,
    onBackgroundStartClick: () -> Unit = {},
    onBackgroundEndClick: () -> Unit = {},
    closeOnContentClick: Boolean = true,
    closeOnBackgroundClick: Boolean = true,
    animateBackgroundCardColor: Boolean = true,
    alphaEasing: Easing = CubicBezierEasing(0.4f, 0.4f, 0.17f, 0.9f),
    maxRevealDp: Dp = 156.dp,
    maxAmountOfOverflow: Dp = 250.dp,
    directions: Set<RevealDirection> = setOf(
        RevealDirection.StartToEnd,
        RevealDirection.EndToStart
    ),
    contentColor: Color = LocalContentColor.current,
    backgroundCardModifier: Modifier = modifier,
    backgroundCardElevation: Dp = 0.dp,
    backgroundCardStartColor: Color = MaterialTheme.colors.secondaryVariant,
    backgroundCardEndColor: Color = MaterialTheme.colors.secondary,
    backgroundCardContentColor: Color = MaterialTheme.colors.onSecondary,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    state: RevealState = rememberRevealState(),
    hiddenContentEnd: @Composable RowScope.() -> Unit = {},
    hiddenContentStart: @Composable RowScope.() -> Unit = {},
    content: @Composable () -> Unit
) {
    val closeOnContentClickHandler = remember(coroutineScope, state) {
        {
            if (state.targetValue != RevealValue.Default) {
                coroutineScope.launch {
                    state.reset()
                }
            }
        }
    }

    val backgroundStartClick = remember(coroutineScope, state, onBackgroundStartClick) {
        {
            if (state.targetValue == RevealValue.FullyRevealedEnd && closeOnBackgroundClick) {
                coroutineScope.launch {
                    state.reset()
                }
            }
            onBackgroundStartClick()
        }
    }

    val backgroundEndClick = remember(coroutineScope, state, onBackgroundEndClick) {
        {
            if (state.targetValue == RevealValue.FullyRevealedStart && closeOnBackgroundClick) {
                coroutineScope.launch {
                    state.reset()
                }
            }
            onBackgroundEndClick()
        }
    }

    Box {

        // alpha for background
        val maxRevealPx = with(LocalDensity.current) { maxRevealDp.toPx() }
        val draggedRatio =
            (state.offset.value.absoluteValue / maxRevealPx.absoluteValue).coerceIn(0f, 1f)

        // cubic parameters can be evaluated here https://cubic-bezier.com/
        val alpha = alphaEasing.transform(draggedRatio)

        val animatedBackgroundEndColor =
            if (alpha in 0f..1f && animateBackgroundCardColor) backgroundCardEndColor.copy(
                alpha = alpha
            ) else backgroundCardEndColor

        val animatedBackgroundStartColor =
            if (alpha in 0f..1f && animateBackgroundCardColor) backgroundCardStartColor.copy(
                alpha = alpha
            ) else backgroundCardStartColor

        // non swipable with hidden content
        Card(
            contentColor = backgroundCardContentColor,
            backgroundColor = Color.Transparent,
            modifier = backgroundCardModifier
                .matchParentSize(),
            elevation = backgroundCardElevation
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(alpha),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .fillMaxHeight()
                        .background(if (directions.contains(RevealDirection.StartToEnd)) animatedBackgroundStartColor else Color.Transparent)
                        .clickableSingle(onClick = backgroundStartClick),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    content = hiddenContentStart
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .fillMaxHeight()
                        .background(if (directions.contains(RevealDirection.EndToStart)) animatedBackgroundEndColor else Color.Transparent)
                        .clickableSingle(onClick = backgroundEndClick),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    content = hiddenContentEnd
                )
            }
        }

        CompositionLocalProvider(
            LocalContentColor provides contentColor,
        ) {
            Box(
                modifier = modifier
                    .then(
                        if (enableSwipe) Modifier
                            .offset {
                                IntOffset(
                                    state.offset.value.roundToInt(),
                                    0
                                )
                            }
                            .revealSwipable(
                                state = state,
                                maxRevealPx = maxRevealPx,
                                maxAmountOfOverflow = maxAmountOfOverflow,
                                directions = directions
                            ) else Modifier
                    )
                    .then(
                        if (onContentClick != null && !closeOnContentClick) {
                            Modifier.clickableSingle(
                                onClick = onContentClick
                            )
                        } else if (onContentClick == null && closeOnContentClick) {
                            // if no onContentClick handler passed, add click handler with no indication to enable close on content click
                            Modifier.clickableSingle(
                                onClick = closeOnContentClickHandler,
                            )
                        } else if (onContentClick != null && closeOnContentClick) {
                            // decide based on state:
                            // 1. if open, just close without indication
                            // 2. if closed, call click handler
                            Modifier.clickable(
                                onClick =
                                {
                                    val isOpen = state.targetValue != RevealValue.Default
                                    // if open, just close. No click event.
                                    if (isOpen) {
                                        closeOnContentClickHandler()
                                    } else {
                                        onContentClick()
                                    }
                                },
                                // no indication if just closing
//                                indication = if (state.targetValue != RevealValue.Default) null else LocalIndication.current,
                            )
                        } else Modifier
                    )
            ) {
                content()
            }
            // This box is used to determine shape size.
            // The box is sized to match it's parent, which in turn is sized according to its first child - the card.
            BoxWithConstraints(modifier = Modifier.matchParentSize()) {

            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
private fun Modifier.revealSwipable(
    maxRevealPx: Float,
    maxAmountOfOverflow: Dp,
    directions: Set<RevealDirection>,
    state: RevealState,
) = composed {

    val maxAmountOfOverflowPx = with(LocalDensity.current) { maxAmountOfOverflow.toPx() }

    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    val anchors = mutableMapOf(0f to RevealValue.Default)

    if (RevealDirection.StartToEnd in directions) anchors += maxRevealPx to RevealValue.FullyRevealedEnd
    if (RevealDirection.EndToStart in directions) anchors += -maxRevealPx to RevealValue.FullyRevealedStart

    val thresholds = { _: RevealValue, _: RevealValue ->
        FractionalThreshold(0.5f)
    }

    val minFactor =
        if (RevealDirection.EndToStart in directions) SwipeableDefaults.StandardResistanceFactor else SwipeableDefaults.StiffResistanceFactor
    val maxFactor =
        if (RevealDirection.StartToEnd in directions) SwipeableDefaults.StandardResistanceFactor else SwipeableDefaults.StiffResistanceFactor

    Modifier.swipeable(
        state = state,
        anchors = anchors,
        thresholds = thresholds,
        orientation = Orientation.Horizontal,
        enabled = true, // state.value == RevealValue.Default,
        reverseDirection = isRtl,
        resistance = ResistanceConfig(
            basis = maxAmountOfOverflowPx,
            factorAtMin = minFactor,
            factorAtMax = maxFactor
        )
    )
}

private fun Float.nonNaNorZero() = if (isNaN()) 0f else this

enum class RevealDirection {
    /**
     * Can be dismissed by swiping in the reading direction.
     */
    StartToEnd,

    /**
     * Can be dismissed by swiping in the reverse of the reading direction.
     */
    EndToStart
}

/**
 * Possible values of [RevealState].
 */
enum class RevealValue {
    /**
     * Indicates the component has not been revealed yet.
     */
    Default,

    /**
     * Fully revealed to end
     */
    FullyRevealedEnd,

    /**
     * Fully revealed to start
     */
    FullyRevealedStart,
}

/**
 * Create and [remember] a [RevealState] with the default animation clock.
 *
 * @param initialValue The initial value of the state.
 * @param confirmStateChange Optional callback invoked to confirm or veto a pending state change.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberRevealState(
    initialValue: RevealValue = RevealValue.Default,
    confirmStateChange: (RevealValue) -> Boolean = { true },
): RevealState {
    return rememberSwipeableState(
        initialValue = initialValue,
        confirmStateChange = confirmStateChange
    )
}

/**
 * Reset the component to the default position, with an animation.
 */
@ExperimentalMaterialApi
suspend fun RevealState.reset() {
    animateTo(
        targetValue = RevealValue.Default,
    )
}

/**
 * Reset the component to the default position, with an animation.
 */
@ExperimentalMaterialApi
suspend fun RevealState.resetFast() {
    animateTo(
        targetValue = RevealValue.Default,
        anim = SnapSpec(1)
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
private fun RevealSwipegPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier
                .width(400.dp)
                .height(400.dp)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(10.dp)
            ) {
                item {
                    RevealSwipe(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        directions = setOf(
                            RevealDirection.StartToEnd,
                            RevealDirection.EndToStart
                        ),
                        hiddenContentStart = {
                            Icon(
                                modifier = Modifier.padding(horizontal = 25.dp),
                                imageVector = Icons.Outlined.Star,
                                contentDescription = null,
                                tint = Color.White
                            )
                        },
                        hiddenContentEnd = {
                            Icon(
                                modifier = Modifier.padding(horizontal = 25.dp),
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null
                            )
                        }
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .requiredHeight(80.dp),
                            backgroundColor = Color(0xFF505160),
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    modifier = Modifier.padding(start = 20.dp),
                                    text = "Both directions"
                                )
                            }
                        }
                    }
                }
                item {
                    RevealSwipe(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        closeOnContentClick = false,
                        closeOnBackgroundClick = false,
                        directions = setOf(
                            RevealDirection.StartToEnd,
                            RevealDirection.EndToStart
                        ),
                        hiddenContentStart = {
                            Icon(
                                modifier = Modifier.padding(horizontal = 25.dp),
                                imageVector = Icons.Outlined.Star,
                                contentDescription = null,
                                tint = Color.White
                            )
                        },
                        hiddenContentEnd = {
                            Icon(
                                modifier = Modifier.padding(horizontal = 25.dp),
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null
                            )
                        }
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .requiredHeight(80.dp),
                            backgroundColor = Color(0xFF68829E),
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    modifier = Modifier.padding(start = 20.dp),
                                    text = "Both directions.\ncloseOnClick = false"
                                )
                            }
                        }
                    }
                }
                item {
                    RevealSwipe(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        directions = setOf(
                            RevealDirection.StartToEnd,
                        ),
                        hiddenContentStart = {
                            Icon(
                                modifier = Modifier.padding(horizontal = 25.dp),
                                imageVector = Icons.Outlined.Star,
                                contentDescription = null,
                                tint = Color.White
                            )
                        },
                        hiddenContentEnd = {
                            Icon(
                                modifier = Modifier.padding(horizontal = 25.dp),
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null
                            )
                        }
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .requiredHeight(80.dp),
                            backgroundColor = Color(0xFFAEBD38),
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    modifier = Modifier.padding(start = 20.dp),
                                    text = "StartToEnd"
                                )
                            }
                        }
                    }
                }
                item {
                    RevealSwipe(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        animateBackgroundCardColor = false,
                        directions = setOf(
                            RevealDirection.EndToStart,
                        ),
                        hiddenContentStart = {
                            Icon(
                                modifier = Modifier.padding(horizontal = 25.dp),
                                imageVector = Icons.Outlined.Star,
                                contentDescription = null,
                                tint = Color.White
                            )
                        },
                        hiddenContentEnd = {
                            Icon(
                                modifier = Modifier.padding(horizontal = 25.dp),
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null
                            )
                        }
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .requiredHeight(80.dp),
                            backgroundColor = Color(0xFF598234),
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    modifier = Modifier.padding(start = 20.dp),
                                    text = "EndToStart"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}