package ge.ngachechiladze.messengerapp.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ge.ngachechiladze.messengerapp.dao.*
import ge.ngachechiladze.messengerapp.models.User

class UserViewModel : ViewModel() {

    private val userData = MutableLiveData<User>()
    private val userDAO : UserDAO = UserDAO()

    fun getUserData() : LiveData<User> = userData

    fun getUserData(id: String, onCancel: OnCancel) : LiveData<User>{
        userDAO.fetchUserWithId(id, userData, onCancel)
        return userData
    }

    fun login(nickname: String, passwordHash: String, onCancel: OnCancel, onAuthorizationFail: OnAuthorizationFail, onAuthorizationSuccess: OnAuthorizationSuccess){
        userDAO.checkCredentials(nickname, passwordHash, onCancel, object : OnAuthorizationSuccess {
            override fun onAuthorizationSuccess() {
                onAuthorizationSuccess.onAuthorizationSuccess()
                userDAO.fetchUserWithNickname(nickname, userData, onCancel)
            }
        }, onAuthorizationFail)
    }

    fun register(user: User, onNicknameExists: OnNicknameExists, onCancel: OnCancel, onAuthorizationSuccess: OnAuthorizationSuccess){
        userDAO.createUser(user, onNicknameExists, onCancel, onAuthorizationSuccess)
    }

    fun updateUserData(user: User, processor: (Boolean) -> Unit){
        userDAO.updateNickname(user){it ->
            if(it){
                userDAO.updateOccupation(user){it ->
                    processor(it)
                }
            }else{
                processor(false)
            }
        }
    }

    fun updatePfp(uid: String, pfp: Uri, processor: (Boolean) -> Unit){
        userDAO.updatePfp(uid,pfp){it->processor(it)}
    }

    fun getPfp(uid: String,  processor: (Uri?) -> Unit){
        userDAO.getPfp(uid){uri->processor(uri)}
    }
}