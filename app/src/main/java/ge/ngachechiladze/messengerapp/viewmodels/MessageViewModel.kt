package ge.ngachechiladze.messengerapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ge.ngachechiladze.messengerapp.dao.MessageDAO
import ge.ngachechiladze.messengerapp.dao.OnCancel
import ge.ngachechiladze.messengerapp.models.Message

class MessageViewModel() : ViewModel() {

    private val messages : MutableLiveData<List<Message>> = MutableLiveData()
    private val messageDAO: MessageDAO = MessageDAO()

    fun getAllMessages() : LiveData<List<Message>> = messages

    fun loadAllMessages(uid: String, onCancel: OnCancel){
        messageDAO.loadAllMessages(uid, messages, onCancel)
    }
}