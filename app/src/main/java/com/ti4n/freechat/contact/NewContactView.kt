package com.ti4n.freechat.contact

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.Badge
import androidx.compose.material.BadgedBox
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.widget.Image
import io.openim.android.sdk.models.FriendApplicationInfo

@Composable
fun NewContactView(navController: NavController, viewModel: NewContactViewModel = hiltViewModel()) {
    val friendApplications by viewModel.friendApplications.collectAsState()
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.White
        )
    }
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        TopAppBar(navigationIcon = {
            Image(
                R.mipmap.nav_back,
                modifier = Modifier
                    .clickable { navController.navigateUp() }
                    .padding(14.dp)
            )
        }, title = {
            Text(
                text = stringResource(id = R.string.new_friend),
                fontSize = 17.sp,
                color = Color(0xFF1A1A1A),
                fontWeight = FontWeight.SemiBold
            )
        }, backgroundColor = Color.White, elevation = 0.dp)
        LazyColumn {
            items(friendApplications) {
                ItemFriendApplication(
                    friendApplicationInfo = it,
                    click = { navController.navigate(Route.Profile.jump(it.fromUserID)) }
                )
            }
        }
    }
}

@Composable
fun ItemFriendApplication(friendApplicationInfo: FriendApplicationInfo, click: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(78.dp)
        .background(Color.White)
        .clickable { click() }
        .padding(horizontal = 24.dp, vertical = 16.dp)) {
        AsyncImage(
            model = friendApplicationInfo.fromFaceURL,
            contentDescription = null,
            modifier = Modifier.size(44.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = friendApplicationInfo.fromNickname,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = friendApplicationInfo.reqMsg,
                fontSize = 12.sp,
                color = Color(0xFFA2A6B2),
                maxLines = 1
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}