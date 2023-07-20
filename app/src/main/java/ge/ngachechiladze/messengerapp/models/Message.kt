package ge.ngachechiladze.messengerapp.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Message(var messageId: String, var message: String, var time: Long, var senderId: String, var receiverId: String)