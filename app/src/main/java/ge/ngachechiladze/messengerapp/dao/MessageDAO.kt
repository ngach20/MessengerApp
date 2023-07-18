package ge.ngachechiladze.messengerapp.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ge.ngachechiladze.messengerapp.models.Message

class MessageDAO {

    private fun getMessagesRef(uid: String) = Firebase.database.getReference("users/$uid/messages")

    fun sendMessage(message: Message){
        val newMessageRef = getMessagesRef(message.senderId).push()

        getMessagesRef(message.receiverId).child(newMessageRef.key.toString()).setValue(message)
    }

    fun loadAllMessages(uid: String, messages: MutableLiveData<List<Message>>, onCancel: OnCancel){
        getMessagesRef(uid).orderByChild("time").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value != null){
                    val list : List<Message> = snapshot.value as List<Message>

                    messages.postValue(list)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onCancel.onCancel()
            }

        })
    }
}