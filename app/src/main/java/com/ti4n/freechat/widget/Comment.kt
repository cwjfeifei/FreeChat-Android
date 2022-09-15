package com.ti4n.freechat.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ti4n.freechat.widget.ratingbar.RatingBar
import com.ti4n.freechat.widget.ratingbar.RatingBarConfig
import com.ti4n.freechat.widget.ratingbar.RatingBarStyle
import com.ti4n.freechat.widget.ratingbar.StepSize
import java.text.SimpleDateFormat

val staredRatingBarConfig = RatingBarConfig()
    .activeColor(Color(0xFFFFA726))
    .hideInactiveStars(false)
    .inactiveColor(Color(0xFFE6E6E6))
    .stepSize(StepSize.HALF)
    .numStars(5)
    .isIndicator(true)
    .size(8.dp)
    .padding(1.dp)
    .style(RatingBarStyle.Normal)

val noStarRatingBarConfig = RatingBarConfig()
    .activeColor(Color(0xFFFFA726))
    .hideInactiveStars(false)
    .inactiveColor(Color(0xFFCCCCCC))
    .stepSize(StepSize.HALF)
    .isIndicator(true)
    .size(6.dp)
    .padding(0.5.dp)
    .style(RatingBarStyle.Normal)

@Composable
fun CommentGrade(
    grade: Float,
    totalCount: Int,
    fiveCount: Int,
    fourCount: Int,
    threeCount: Int,
    twoCount: Int,
    oneCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "评分及评论",
                fontSize = 12.sp,
                color = Color(0xFF1A1A1A),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "查看全部",
                fontSize = 10.sp,
                color = Color(0xFF6F5FFC)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 27.dp)
        ) {
            Text(
                text = "$grade",
                fontSize = 28.sp,
                color = Color(0xFF1B1B1B),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(27.dp))
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                RatingBar(
                    value = 0f,
                    config = noStarRatingBarConfig.numStars(5),
                    onRatingChanged = {},
                    onValueChange = {},
                    modifier = Modifier.height(6.dp)
                )
                RatingBar(
                    value = 0f,
                    config = noStarRatingBarConfig.numStars(4),
                    onRatingChanged = {},
                    onValueChange = {},
                    modifier = Modifier.height(6.dp)
                )
                RatingBar(
                    value = 0f,
                    config = noStarRatingBarConfig.numStars(3),
                    onRatingChanged = {},
                    onValueChange = {},
                    modifier = Modifier.height(6.dp)
                )
                RatingBar(
                    value = 0f,
                    config = noStarRatingBarConfig.numStars(2),
                    onRatingChanged = {},
                    onValueChange = {},
                    modifier = Modifier.height(6.dp)
                )
                RatingBar(
                    value = 0f,
                    config = noStarRatingBarConfig.numStars(1),
                    onRatingChanged = {},
                    onValueChange = {},
                    modifier = Modifier.height(6.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                LinearProgressIndicator(
                    progress = fiveCount / totalCount.toFloat(),
                    color = Color(0xFFFFA726),
                    backgroundColor = Color(0xFFF5F5F5),
                    modifier = Modifier
                        .height(6.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(2.dp))
                )
                LinearProgressIndicator(
                    progress = fourCount / totalCount.toFloat(),
                    color = Color(0xFFFFA726),
                    backgroundColor = Color(0xFFF5F5F5),
                    modifier = Modifier
                        .height(6.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(2.dp))
                )
                LinearProgressIndicator(
                    progress = threeCount / totalCount.toFloat(),
                    color = Color(0xFFFFA726),
                    backgroundColor = Color(0xFFF5F5F5),
                    modifier = Modifier
                        .height(6.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(2.dp))
                )
                LinearProgressIndicator(
                    progress = twoCount / totalCount.toFloat(),
                    color = Color(0xFFFFA726),
                    backgroundColor = Color(0xFFF5F5F5),
                    modifier = Modifier
                        .height(6.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(2.dp))
                )
                LinearProgressIndicator(
                    progress = oneCount / totalCount.toFloat(),
                    color = Color(0xFFFFA726),
                    backgroundColor = Color(0xFFF5F5F5),
                    modifier = Modifier
                        .height(6.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(2.dp))
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 2.dp)
        ) {
            RatingBar(
                value = grade,
                config = staredRatingBarConfig,
                onRatingChanged = {},
                onValueChange = {}
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${totalCount}评分",
                fontSize = 10.sp,
                color = Color(0xFF999999)
            )
        }
    }
}

@Composable
fun CommentItem(name: String, comment: String, grade: Float, time: Long) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(
                    text = name,
                    fontSize = 14.sp,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                RatingBar(
                    value = grade,
                    config = staredRatingBarConfig,
                    onRatingChanged = {},
                    onValueChange = {}
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = SimpleDateFormat("yyyy-MM-dd").format(time),
                fontSize = 10.sp,
                color = Color(0xFF999999)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = comment,
            fontSize = 14.sp,
            color = Color(0xFF4D4D4D)
        )
    }
}