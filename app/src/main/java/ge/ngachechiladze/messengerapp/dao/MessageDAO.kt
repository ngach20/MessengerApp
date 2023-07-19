package ge.ngachechiladze.messengerapp.dao

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ge.ngachechiladze.messengerapp.models.Contact
import ge.ngachechiladze.messengerapp.models.Message

class MessageDAO {

    private fun getMessagesRef(uid: String) = Firebase.database.getReference("users/$uid/messages")
    private fun getUserRef(uid: String) = Firebase.database.getReference("users/$uid")

    fun sendMessage(message: Message){
        val newMessageRef = getMessagesRef(message.senderId).push()

        getMessagesRef(message.receiverId).child(newMessageRef.key.toString()).setValue(message)
    }

//    fun loadAllMessages(uid: String, messages: MutableLiveData<HashMap<String, ArrayList<Message>>>, onCancel: OnCancel){
//        getMessagesRef(uid).orderByChild("time").addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if(snapshot.value != null){
//                    @Suppress("UNCHECKED_CAST")
//                    val map : HashMap<String, HashMap<String, Any>> = snapshot.value as HashMap<String, HashMap<String, Any>>
//                    val list = arrayListOf<Message>()
//
//                    for(message in map){
//                        val messageId = message.value["messageId"] as String
//                        val senderId = message.value["senderId"] as String
//                        val receiverId = message.value["receiverId"] as String
//                        val msg = message.value["message"] as String
//                        val time = message.value["time"] as Long
//
//                        list.add(Message(messageId, msg, time, senderId, receiverId))
//                    }
//
//                    //Get all messages in the descending order (so we get the most resent ones in the beginning)
//                    list.reverse()
//
//                    val chats = hashMapOf<String, ArrayList<Message>>()
//
//                    list.forEach { message ->
//                        val contactId = if (message.receiverId == uid) message.senderId else message.receiverId
//                        if(chats.containsKey(contactId)){
//                            chats[contactId]?.add(message)
//                        }else{
//                            val newList = arrayListOf<Message>()
//                            newList.add(message)
//                            chats[contactId] = newList
//                        }
//                    }
//
//                    messages.postValue(chats)
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                onCancel.onCancel()
//            }
//        })
//    }

    fun loadAllContacts(uid: String, contacts: MutableLiveData<List<Contact>>, onCancel: OnCancel){
        getUserRef(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child("messages").value != null){
                    @Suppress("UNCHECKED_CAST")
                    val map : HashMap<String, HashMap<String, Any>> = snapshot.child("messages").value as HashMap<String, HashMap<String, Any>>
                    val list = arrayListOf<Message>()

                    for(message in map){
                        val messageId = message.value["messageId"] as String
                        val senderId = message.value["senderId"] as String
                        val receiverId = message.value["receiverId"] as String
                        val msg = message.value["message"] as String
                        val time = message.value["time"] as Long

                        list.add(Message(messageId, msg, time, senderId, receiverId))
                    }

                    //Get all messages in the descending order (so we get the most resent ones in the beginning)
                    list.sortBy { -it.time }


                    val contactsSet = hashSetOf<Contact>()

                    val listIt = list.iterator()
                    while(listIt.hasNext()) {
                        val message = listIt.next()
                        val contactId = if(uid == message.senderId) message.receiverId else message.senderId


                        getUserRef(contactId).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot2: DataSnapshot) {
                                val nickname = snapshot2.child("nickname").value.toString()
                                val occupation = snapshot2.child("occupation").value.toString()

                                contactsSet.add(Contact(contactId, nickname, occupation, message.message, message.time))

                                //If this is the last message
                                if(!listIt.hasNext()){
                                    contacts.postValue(contactsSet.toList())
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                onCancel.onCancel()
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onCancel.onCancel()
            }
        })
    }
}