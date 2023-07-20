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

    private val messageDAO: MessageDAO = MessageDAO()

    init{
        messageDAO.loadAllContacts(uid, contacts, onCancel)

    }

    fun getAllContacts(): LiveData<List<Contact>> = contacts

    class Factory(private val uid: String, private val onCancel: OnCancel) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MessageViewModel(uid, onCancel) as T
        }
    }
}