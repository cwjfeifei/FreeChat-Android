package com.ti4n.freechat.home

import android.icu.text.SimpleDateFormat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.util.IM
import com.ti4n.freechat.widget.HomeTitle
import com.ti4n.freechat.widget.Image
import io.openim.android.sdk.models.FriendInfo
import projekt.cloud.piece.c2.pinyin.C2Pinyin.pinyin
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactView(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberLazyListState()
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color(0xFFF0F0F0)
        )
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
    ) {
        TopAppBar(backgroundColor = Color(0xFFF0F0F0), title = {
            HomeTitle(R.string.contact)
        }, elevation = 0.dp, modifier = Modifier.statusBarsPadding())
        Spacer(modifier = Modifier.height(6.dp))
        Box {
            val items = IM.friends.groupBy {
                it.nickname.pinyin.first()
            }
            LazyColumn(modifier = Modifier.background(Color.White)) {
                item {
                    ItemNewFriend {

                    }
                }
                items.forEach {
                    stickyHeader(key = it.key) {
                        ItemLetter(letter = it.key)
                    }
                    items(it.value) {
                        ItemFriend(friendInfo = it) {
                            navController.navigate(Route.Profile.jump(it.userID))
                        }
                    }
                }
            }
            Column(modifier = Modifier.align(Alignment.CenterEnd)) {
                items.map { it.key }.forEach {
                    Text(
                        text = it.uppercase(),
                        color = Color(0xFF4D4D4D),
                        fontSize = 9.sp,
                        modifier = modifier
                            .clickable {

                            }
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
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
fun ItemFriend(friendInfo: FriendInfo, click: () -> Unit) {
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
            text = friendInfo.nickname, color = Color(0xFF1A1A1A), fontSize = 16.sp
        )
    }
}

@Composable
fun ItemLetter(letter: Char) {
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

