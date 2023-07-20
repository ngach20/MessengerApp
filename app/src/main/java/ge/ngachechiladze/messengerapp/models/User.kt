package ge.ngachechiladze.messengerapp.models

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName

@IgnoreExtraProperties
data class User(var id: String, var nickname: String, @get:PropertyName("password") @set:PropertyName("password") var passwordHash: String, var occupation: String)

@IgnoreExtraProperties
data class Contact(var id: String, var nickname: String, var occupation: String, var mostRecentMessage: String, var time: Long){
    override fun equals(other: Any?): Boolean {
        return (other as Contact).id == this.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}