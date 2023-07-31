package ge.ngachechiladze.messengerapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ge.ngachechiladze.messengerapp.dao.MessageDAO
import ge.ngachechiladze.messengerapp.dao.OnCancel
import ge.ngachechiladze.messengerapp.models.Contact
import ge.ngachechiladze.messengerapp.models.Message

class MessageViewModel(private val uid: String, private val onCancel: OnCancel) : ViewModel() {

    private val contacts: MutableLiveData<List<Contact>> = MutableLiveData()
    private lateinit var messages: MutableList<Message>

    private val messageDAO: MessageDAO = MessageDAO()

    init{
        messageDAO.loadAllContacts(uid, contacts, onCancel)

    }

    fun getAllContacts(): LiveData<List<Contact>> = contacts



    fun getMessages(processor: (MutableList<Message>) -> Unit){
        messages.sortBy{ message -> message.time }
        processor(messages)
    }

    fun setMessagesWatcher(uid: String, target_uid: String, processor: (Unit) -> Unit){
        messageDAO.getMessages(uid, target_uid){
            messages = it
            processor(Unit)
        }
    }

    fun sendMessage(message: Message){
        messageDAO.sendMessage(message)
    }

    class Factory(private val uid: String, private val onCancel: OnCancel) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MessageViewModel(uid, onCancel) as T
        }
    }
}