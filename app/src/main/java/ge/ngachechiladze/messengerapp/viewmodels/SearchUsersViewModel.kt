package ge.ngachechiladze.messengerapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ge.ngachechiladze.messengerapp.dao.UserDAO
import ge.ngachechiladze.messengerapp.models.UserPublicData

class SearchUsersViewModel: ViewModel() {

    private val userDao : UserDAO = UserDAO()

    private var lastUserId = ""
    private var filter : String = ""

    private var searchedUserData = MutableLiveData<List<UserPublicData>>()

    fun getSearchedUsers() : LiveData<List<UserPublicData>> = searchedUserData

    fun loadNextUserBatch(batchSize: Int, page: Int, onCancel: () -> Unit){
        userDao.fetchUserRange(batchSize, page, filter, searchedUserData,
            onCancel)
    }

    fun filterUsers(filter: String){
        this.filter = filter
    }

    fun reset(){
        searchedUserData.postValue(arrayListOf())
        lastUserId = ""
        filter = ""
    }
}