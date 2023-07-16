package ge.ngachechiladze.messengerapp.models

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName

@IgnoreExtraProperties
data class User(var nickname: String, @get:PropertyName("password") @set:PropertyName("password") var passwordHash: String, var occupation: String)