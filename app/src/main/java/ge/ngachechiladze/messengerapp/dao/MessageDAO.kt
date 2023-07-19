package ge.ngachechiladze.messengerapp.dao

import android.util.Log
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
                    @Suppress("UNCHECKED_CAST")
                    val map : HashMap<String, HashMap<String, Any>> = snapshot.value as HashMap<String, HashMap<String, Any>>
                    val list = arrayListOf<Message>()

                    for(message in map){
                        val messageId = message.value["messageId"] as String
                        val senderId = message.value["senderId"] as String
                        val receiverId = message.value["receiverId"] as String
                        val msg = message.value["message"] as String
                        val time = message.value["time"] as Long

                        list.add(Message(messageId, msg, time, senderId, receiverId))
                    }

                    messages.postValue(list)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onCancel.onCancel()
            }
        })
    }
}