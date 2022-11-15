package com.ti4n.freechat.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ti4n.freechat.util.*
import java.util.*
import kotlin.collections.ArrayList

@Composable
fun DataTimePicker(
    viewModel: MeEditViewModel
) {
    val date = if (viewModel.birth.value >0) Date(viewModel.birth.value) else Date()
    val itemHeight = 50.dp

    Box(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        Alignment.Center
    ) {
        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface),
            Arrangement.SpaceEvenly,
            Alignment.CenterVertically
        ) {
            val year = date.getYearr()
            val selectYear = rememberSaveable { mutableStateOf(year) }
            val years = LinkedList<Pair<Int, String>>().apply {
                for (i in year downTo 1980) {
                    add(Pair(i, "${i}年"))
                }
            }
            DatePickerColumn(years, itemHeight, 70.dp, selectYear)

            //  月份
            val month = date.getMonthh()
            val selectMonth = rememberSaveable { mutableStateOf(month) }
            val months = ArrayList<Pair<Int, String>>(12).apply {
                for (i in 1..12) {
                    add(Pair(i, "${i}月"))
                }
            }
            DatePickerColumn(months, itemHeight, 50.dp, selectMonth)

            //  月份的天数
            val dayOfMon = date.getDayOfMonth()
            val selectDay = rememberSaveable { mutableStateOf(dayOfMon) }
            val dayCountOfMonth = DateUtil.getDayCountOfMonth(selectYear.value, selectMonth.value)

            //  提前定义好
            val day31 = ArrayList<Pair<Int, String>>().apply {
                for (i in 1..31)
                    add(Pair(i, "${i}日"))
            }
            val day30 = ArrayList<Pair<Int, String>>().apply {
                for (i in 1..30)
                    add(Pair(i, "${i}日"))
            }
            val day29 = ArrayList<Pair<Int, String>>().apply {
                for (i in 1..29)
                    add(Pair(i, "${i}日"))
            }
            val day28 = ArrayList<Pair<Int, String>>().apply {
                for (i in 1..28)
                    add(Pair(i, "${i}日"))
            }

            //  快速切换
            val dayOfMonList = when (dayCountOfMonth) {
                28 -> day28
                29 -> day29
                30 -> day30
                else -> day31
            }

            DatePickerColumn(
                pairList = dayOfMonList,
                itemHeight = itemHeight,
                valueState = selectDay
            )

//            //  小时
//            val hour = date.getHour()
//            val selectHour = rememberSaveable { mutableStateOf(hour) }
//            val hours = ArrayList<Pair<Int, String>>(24).apply {
//                for (i in 0..23) {
//                    add(Pair(i, "${i}时"))
//                }
//            }
//            DatePickerColumn(hours, itemHeight, 50.dp, selectHour)
//
//            //  分
//            val minute = date.getMinute()
//            val selectMinute = rememberSaveable { mutableStateOf(minute) }
//            val minutes = ArrayList<Pair<Int, String>>(60).apply {
//                for (i in 0..59) {
//                    add(Pair(i, "${i}分"))
//                }
//            }
//            DatePickerColumn(minutes, itemHeight, 50.dp, selectMinute)
//
//            //  秒
//            val second = date.getSecond()
//            val selectSecond = rememberSaveable { mutableStateOf(second) }
//            val seconds = ArrayList<Pair<Int, String>>(60).apply {
//                for (i in 0..59) {
//                    add(Pair(i, "${i}秒"))
//                }
//            }
//            DatePickerColumn(seconds, itemHeight, 50.dp, selectSecond)
        }

        //  放在后面使得不会被遮住
        Column {
            Divider(
                Modifier.padding(
                    start = 15.dp,
                    end = 15.dp,
                    bottom = itemHeight
                ),
                thickness = 1.dp
            )
            Divider(
                Modifier.padding(
                    start = 15.dp,
                    end = 15.dp
                ),
                thickness = 1.dp
            )
        }
    }
}
