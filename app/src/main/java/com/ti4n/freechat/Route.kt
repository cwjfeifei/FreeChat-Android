package com.ti4n.freechat

sealed class Route(val route: String) {
    object Splash : Route("splash")
    object MainLogin : Route("mainLogin")
    object Login : Route("login")
    object SetPassword : Route("setPassword/{words}") {
        fun jump(words: String) = "setPassword/$words"
    }

    object SetEmail : Route("setEmail/{userID}") {
        fun jump(userID: String) = "setEmail/$userID"
    }

    object PickFaceImage : Route("pickFaceImage")
    object ProfilePreview : Route("profilePreview/{xfaceURL}/{nickname}/{gender}") {
        fun jump(xfaceURL: String, nickname:String, gender:Int) = "profilePreview/$xfaceURL/$nickname/$gender"
    }
    object Register1 : Route("register1")
    object Register2 : Route("register2")
    object CompleteProfile : Route("completeProfile")
    object SetName : Route("setName")
    object Home : Route("home")
    object Profile : Route("profile/{id}") {
        fun jump(id: String) = "profile/$id"
    }

    object LookFriendApplication : Route("lookFriendApplication/{id}") {
        fun jump(id: String) = "lookFriendApplication/$id"
    }

    object ApproveFriendApplication : Route("approveFriendApplication/{id}")

    object SetRemark : Route("setRemark")
    object SendFriendApplication : Route("sendFriendApplication")

    object BigImage : Route("image")

    object ChooseImageSourceBottom : Route("chooseImageSource")
    object VideoVoiceChatBottom : Route("videoVoiceChat")
    object MeEdit : Route("meEdit")
    object EditPickFaceImage : Route("editPickFaceImage")
    object EditEmail : Route("editEmail")
    object EditNickName : Route("editNickName")
    object EditGender : Route("editGender")
    object Wallet : Route("Wallet")
    object SendMoney : Route("sendMoney")
    object SendRedPack : Route("sendMoney") {
        fun jump(toUserId: String) = "sendMoney?userId=$toUserId&redpack=true"
    }

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

    object AddFriend : Route("addFriend")

    object TransferRisk : Route("transferRisk")
    object Setting : Route("setting")
    object AccountSecurity : Route("accountSecurity")
    object LanguageSetting : Route("languageSetting")
    object ClearCache : Route("clearCache")
    object ChangePassword : Route("changePassword")

    object VerifyEmailRegister : Route("verifyEmailRegister/{userID}/{email}") {
        fun jump(userId: String, email: String) = "verifyEmailRegister/$userId/$email"
    }

    object VerifyEmailLogin : Route("verifyEmailLogin/{userID}/{email}") {
        fun jump(userId: String, email: String) = "verifyEmailLogin/$userId/$email"
    }
}