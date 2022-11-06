package com.ti4n.freechat

import com.ti4n.freechat.db.UserBaseInfo

sealed class Route(val route: String) {
    object Splash : Route("splash")
    object MainLogin : Route("mainLogin")
    object Login : Route("login")
    object SetPassword : Route("setPassword/{words}") {
        fun jump(words: String) = "setPassword/$words"
    }
    object SetEmail : Route("setEmail/{words}") {
        fun jump(words: String) = "setEmail/$words"
    }
    object PickFaceImage : Route("pickFaceImage")
    object ProfilePreview : Route("profilePreview")
    object Register1 : Route("register1")
    object Register2 : Route("register2")
    object CompleteProfile : Route("completeProfile")
    object SetName : Route("setName")
    object Home : Route("home")
    object Profile : Route("profile/{id}") {
        fun jump(id: String) = "profile/$id"
    }

    object SetRemark : Route("setRemark")
    object SendFriendApplication : Route("sendFriendApplication")

    object BigImage : Route("image")

    object ChooseImageSourceBottom : Route("chooseImageSource")
    object VideoVoiceChatBottom : Route("videoVoiceChat")
    object MeDetails : Route("meDetails")
    object Wallet : Route("Wallet")
    object SendMoney : Route("sendMoney")
    object ReceiveMoney : Route("receiveMoney")
    object SendMoneyInputDetail : Route("sendMoneyInputDetail")
    object TokenDetailSimply : Route("tokenSimply/{tokenSymbol}/{address}") {
        fun jump(tokenSymbol: String, address: String) = "tokenSimply/$tokenSymbol/$address"
    }

    object TokenDetail : Route("token/{tokenSymbol}/{address}") {
        fun jump(tokenSymbol: String, address: String) = "token/$tokenSymbol/$address"
    }

    object PermissionIntro : Route("permissionIntro")
    object NoInternet : Route("noInternet")

    object Swap : Route("swap")

    object ConfirmTransaction : Route("ConfirmTransaction")

    object PrivateChat : Route("privateChat/{id}/{conversationId}") {
        fun jump(id: String, conversationId: String) = "privateChat/$id/$conversationId"
    }

    object NewContact : Route("newContact")

}