package com.ti4n.freechat.contact

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
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

@OptIn(ExperimentalMaterial3Api::class)
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
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color(0xFFF0F0F0)
            ), navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Image(mipmap = R.mipmap.nav_back)
                }
            }, title = {
                Text(
                    text = stringResource(id = R.string.new_friend),
                    fontSize = 17.sp,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.SemiBold
                )
            })
        LazyColumn {
            items(friendApplications) {
                ItemFriendApplication(friendApplicationInfo = it, navController)
            }
        }
    }
}

@Composable
fun ItemFriendApplication(
    friendApplicationInfo: FriendApplicationInfo,
    navController: NavController
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(78.dp)
        .background(Color.White)
        .clickable {
            navController.navigate(
                if (friendApplicationInfo.handleResult != -1)
                    Route.LookFriendApplication.jump(
                        friendApplicationInfo.fromUserID
                    ) else Route.Profile.jump(
                    friendApplicationInfo.fromUserID
                )
            )
        }
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
        when (friendApplicationInfo.handleResult) {
            0 -> {
                TextButton(
                    onClick = {
                        navController.navigate(
                            Route.LookFriendApplication.jump(
                                friendApplicationInfo.fromUserID
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFF5F5F5),
                        contentColor = Color(0xFF58BF6B)
                    ), shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 3.dp)
                ) {
                    Text(text = stringResource(id = R.string.have_a_look), fontSize = 12.sp)
                }
            }

            1 -> {
                Image(mipmap = R.mipmap.new_contact_send)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(id = R.string.already_accept),
                    color = Color(0xFFB3B3B3),
                    fontSize = 12.sp
                )
            }

            -1 -> {
                Image(mipmap = R.mipmap.new_contact_send)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(id = R.string.already_refuse),
                    color = Color(0xFFB3B3B3),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ItemTime(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF4F4F4))
            .padding(vertical = 6.dp, horizontal = 12.dp),
        color = Color(0xFF666666),
        fontSize = 12.sp
    )
}