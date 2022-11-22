package com.ti4n.freechat.home

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DatePickerColumn(
    //	列表
    pairList: List<Pair<Int, String>>,
    itemHeight: Dp,
    itemWidth: Dp? = null,
    valueState: MutableState<Int>,
    focusColor: Color = Color.White, //MaterialTheme.colors.primary,
    unfocusColor: Color = Color.White.copy(alpha = 0.7f)
) {
    val dataPickerCoroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    var selectedKey by remember {
        valueState
    }
    val density = LocalDensity.current
    LaunchedEffect(Unit) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }.collectLatest {
            val realItem =
                it.filter { it.key is Int && it.offset >= 0 && it.offset <= with(density) { (itemHeight * 4).toPx() } }
            when (realItem.size) {
                3 -> if (realItem.first().key == pairList.first().first) selectedKey =
                    pairList.first().first else if (realItem.last().key == pairList.last().first) selectedKey =
                    pairList.last().first

                4 -> if (realItem.first().key == pairList.first().first) selectedKey =
                    pairList[1].first
                else if (realItem.last().key == pairList.last().first) selectedKey =
                    pairList[pairList.size - 2].first

                5 -> selectedKey = realItem[2].key as Int
            }
        }
    }
    LazyColumn(
        state = listState,
        modifier = Modifier
            .padding(top = itemHeight / 2, bottom = itemHeight / 2)
            .height(itemHeight * 5),
        flingBehavior = snapFlingBehavior,
    ) {
        item {
            Surface(Modifier.height(itemHeight)) {}
        }
        item {
            Surface(Modifier.height(itemHeight)) {}
        }
        itemsIndexed(items = pairList, key = { index, pair -> pair.first }) { index, pair ->
            val widthModifier = itemWidth?.let { Modifier.width(itemWidth) } ?: Modifier
            Box(
                modifier = Modifier
                    .height(itemHeight)
                    .then(widthModifier)
                    .padding(start = 5.dp, end = 5.dp), Alignment.Center
            ) {
                Text(
                    text = pair.second,
                    color = if (selectedKey == pair.first) focusColor else unfocusColor
                )
            }
        }
        item {
            Surface(Modifier.height(itemHeight)) {}
        }
        item {
            Surface(Modifier.height(itemHeight)) {}
        }
    }

    //  选择初始值
    LaunchedEffect(Unit) {

        var initIndex = 0

        for (index in pairList.indices) {
            if (selectedKey == pairList[index].first) {
                initIndex = index
                break
            }
        }
        dataPickerCoroutineScope.launch {
            listState.animateScrollToItem(initIndex)
        }
    }
}
