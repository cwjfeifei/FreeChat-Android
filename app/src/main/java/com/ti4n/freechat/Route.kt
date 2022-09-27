package com.ti4n.freechat

sealed class Route(val route: String) {
    object Splash : Route("splash")
    object MainLogin : Route("mainLogin")
    object Login : Route("login")
    object SetPassword : Route("setPassword/{words}") {
        fun jump(words: String) = "setPassword/$words"
    }

    object Register1 : Route("register1")
    object Register2 : Route("register2")
    object CompleteProfile : Route("completeProfile")
    object SetName : Route("setName")
    object Home : Route("home")
    object Profile : Route("profile")

    object BigImage : Route("image")

    object ChooseImageSourceBottom : Route("chooseImageSource")
    object VideoVoiceChatBottom : Route("videoVoiceChat")
    object Wallet : Route("Wallet")
    object SendMoney : Route("sendMoney")
    object SendMoneyInputDetail : Route("sendMoneyInputDetail")
    object TokenDetail : Route("token/{words}") {
        fun jump(tokenSymbol: String) = "token/$tokenSymbol"
    }

    object PermissionIntro : Route("permissionIntro")
    object NoInternet : Route("noInternet")
}