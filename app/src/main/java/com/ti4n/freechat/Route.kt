package com.ti4n.freechat

sealed class Route(val route: String) {
    object MainLogin : Route("mainLogin")
    object Login : Route("login")
    object SetPassword : Route("setPassword")
    object Register1 : Route("register1")
    object Register2 : Route("register2")
    object CompleteProfile : Route("completeProfile")
    object SetName : Route("setName")
    object Home : Route("home")
    object Profile : Route("profile")

    object BigImage : Route("image")

    object ChooseImageSourceBottom : Route("chooseImageSource")
    object VideoVoiceChatBottom : Route("videoVoiceChat")
}