package com.ti4n.freechat.home

import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.model.im.BaseInfo
import com.ti4n.freechat.util.IM
import com.ti4n.freechat.widget.HomeTitle
import com.ti4n.freechat.widget.Image
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import projekt.cloud.piece.c2.pinyin.C2Pinyin.pinyin
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ContactView(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberLazyListState()
    val systemUiController = rememberSystemUiController()
    val friends by IM.friends.collectAsState()
    var letter by remember {
        mutableStateOf("")
    }
    var y by remember {
        mutableStateOf(0.dp)
    }
    var showLetter by remember {
        mutableStateOf(false)
    }
    val items = buildList {
        friends.groupBy {
            val pinyin = it.remark.ifEmpty {
                it.nickname.ifEmpty { "*" }  // empty cause exception
            }.pinyin.first().uppercase()
            if (pinyin in letters) {
                pinyin
            } else "#"
        }.toSortedMap().mapKeys {
            add(ItemLetterData(it.key.toString()))
            addAll(it.value.map { ItemContactData(it) })
        }
    }
    val itemLetters = items.filterIsInstance<ItemLetterData>().map { it.letter }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color(0xFFF0F0F0)
        )
        systemUiController.setNavigationBarColor(
            color = Color.White
        )
    }
    LaunchedEffect(Unit) {
        snapshotFlow { scrollState.firstVisibleItemIndex }.filter { items.isNotEmpty() }
            .collectLatest {
                letter = when (val l = items[it]) {
                    is ItemContactData -> {
                        val p = l.contact.remark.ifEmpty {
                            l.contact.nickname.ifEmpty { "*" }  // empty cause exception
                        }.pinyin.first().uppercase()
                        if (p in letters) {
                            p
                        } else "#"
                    }

                    is ItemLetterData -> l.letter
                }
            }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0)), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color(0xFFF0F0F0)
            ), title = {
                HomeTitle(R.string.contact)
            }, modifier = Modifier.statusBarsPadding()
        )
        Spacer(modifier = Modifier.height(6.dp))
        if (items.isNotEmpty()) {
            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(modifier = Modifier.background(Color.White), state = scrollState) {
                    item {
                        ItemNewFriend {
                            navController.navigate(Route.NewContact.route)
                        }
                    }
                    items.forEach {
                        when (it) {
                            is ItemContactData -> item {
                                ItemFriend(friendInfo = it.contact) {
                                    navController.navigate(Route.Profile.jump(it.contact.userID))
                                }
                            }

                            is ItemLetterData -> stickyHeader(key = it.letter) {
                                ItemLetter(letter = it.letter)
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                ) {
                    if (showLetter && letter.isNotEmpty())
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.offset(y = y)
                        ) {
                            Image(mipmap = R.mipmap.letter_bg)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = letter,
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(end = 5.dp)
                            )
                        }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .height((itemLetters.size * 16).dp)
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = {
                                        showLetter = true
                                        if (it.y.toDp() in 0.dp..(itemLetters.size * 14).dp) {
                                            letter =
                                                itemLetters[(it.y.toDp().value / 14).toInt()]
                                            y = it.y.toDp()
                                        }
                                    },
                                    onDragCancel = {
                                        showLetter = false
                                    },
                                    onDragEnd = {
                                        showLetter = false

                                    }) { change, dragAmount ->
                                    with(density) {
                                        if (change.position.y.toDp() in 0.dp..(itemLetters.size * 14).dp) {
                                            letter =
                                                itemLetters[(change.position.y.toDp().value / 14).toInt()]
                                            y = change.position.y.toDp()
                                            scope.launch {
                                                scrollState.animateScrollToItem(items.indexOfFirst { it is ItemLetterData && it.letter == letter })
                                            }
                                        }
                                    }
                                }
                            }) {
                        itemLetters.forEach {
                            Text(
                                text = it,
                                color = if (letter == it) Color.White else Color(0xFF4D4D4D),
                                fontSize = 9.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(
                                        if (letter == it) Color(0xFF3879FD) else Color.Transparent,
                                        CircleShape
                                    )
                                    .padding(horizontal = 2.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
            Image(mipmap = R.mipmap.contact_list_default)
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = stringResource(id = R.string.no_contact), color = Color(0xFF999999))
            Spacer(modifier = Modifier.height(24.dp))
            TextButton(
                onClick = { navController.navigate(Route.AddFriend.route) },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = Color(0xFF3879FD),
                    contentColor = Color.White
                ),
                modifier = Modifier.size(160.dp, 40.dp),
                elevation = ButtonDefaults.elevation()
            ) {
                Text(text = stringResource(id = R.string.go_to_contact), fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun ItemNewFriend(click: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable { click() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Image(mipmap = R.mipmap.friend_new)
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(id = R.string.new_friend),
            color = Color(0xFF1A1A1A),
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(mipmap = R.mipmap.right_arrow)
    }
}

@Composable
fun ItemFriend(friendInfo: BaseInfo, click: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable { click() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = friendInfo.faceURL, contentDescription = null,
            Modifier
                .size(36.dp)
                .clip(
                    RoundedCornerShape(4.dp)
                )
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = friendInfo.remark.ifEmpty { friendInfo.nickname },
            color = Color(0xFF1A1A1A),
            fontSize = 16.sp
        )
    }
}

@Composable
fun ItemLetter(letter: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(30.dp)
            .background(Color(0xFFF0F0F0))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = letter.uppercase(), color = Color(0xFF666666), fontSize = 12.sp
        )
    }
}

val letters = listOf(
    "A",
    "B",
    "C",
    "D",
    "E",
    "F",
    "G",
    "H",
    "I",
    "J",
    "K",
    "L",
    "M",
    "N",
    "O",
    "P",
    "Q",
    "R",
    "S",
    "T",
    "U",
    "V",
    "W",
    "X",
    "Y",
    "Z"
)

sealed interface ItemContact
data class ItemLetterData(val letter: String) : ItemContact
data class ItemContactData(val contact: BaseInfo) : ItemContact

