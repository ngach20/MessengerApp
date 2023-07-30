package ge.ngachechiladze.messengerapp.dao

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import ge.ngachechiladze.messengerapp.models.User

class UserDAO {

    private fun getUserRef(id: String) = Firebase.database.getReference("users/$id")
    private fun getUsersRef() = Firebase.database.getReference("users")

    private fun getUserPasswordRef(id: String) = Firebase.database.getReference("users/$id/password")

    private fun getPairRef(nickname: String) = Firebase.database.getReference("pairs/$nickname")

    /** Creates a user with the given information.
     *  Ignores the parsed id. */
    fun createUser(user: User, onNicknameExists: OnNicknameExists, onCancel: OnCancel, onAuthorizationSuccess: OnAuthorizationSuccess) : User{
        val pairRef = getPairRef(user.nickname)

        pairRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists() && snapshot.value != null){
                    onNicknameExists.onNicknameExists()
                }else{
                    val newObjRef = getUsersRef().push()
                    val id = newObjRef.key.orEmpty()
                    if (id != "") {
                        user.id = id
                        newObjRef.setValue(user)
                        createNicknameIdPair(user.nickname, id)
                        onAuthorizationSuccess.onAuthorizationSuccess()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onCancel.onCancel()
            }
        })

        return user
    }

    fun fetchUserWithId(id: String, userData: MutableLiveData<User>, onCancel: OnCancel){
        getUserInfo(id, object : OnGetUserInfo {
            override fun onGetUserInfo(user: User) {
                userData.postValue(user)
            }
        }, onCancel)
    }

    /** Only safe before user is authorized. */
    fun fetchUserWithNickname(nickname: String, userData: MutableLiveData<User>, onCancel: OnCancel){
        val pairRef = getPairRef(nickname)

        pairRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val id = snapshot.value.toString()

                getUserInfo(id, object : OnGetUserInfo {
                    override fun onGetUserInfo(user: User) {
                        userData.postValue(user)
                    }
                }, onCancel)
            }

            override fun onCancelled(error: DatabaseError) {
                onCancel.onCancel()
            }
        })
    }

    private fun getUserInfo(id: String, onGetUserInfo: OnGetUserInfo, onCancel: OnCancel){
        val userRef = getUserRef(id)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nickname = snapshot.child("nickname").value.toString()
                val passwordHash = snapshot.child("passwordHash").value.toString()
                val occupation = snapshot.child("occupation").value.toString()
                val user = User(id, nickname, passwordHash, occupation)

                onGetUserInfo.onGetUserInfo(user)
            }

            override fun onCancelled(error: DatabaseError) {
                onCancel.onCancel()
            }
        })
    }

    fun checkCredentials(nickname: String, passwordHash: String, onCancel: OnCancel, onAuthorizationSuccess: OnAuthorizationSuccess, onAuthorizationFail: OnAuthorizationFail){
        val pairRef = getPairRef(nickname)

        pairRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists() && snapshot.value != null){
                    val id = snapshot.value.toString()

                    checkPassword(id, passwordHash, onCancel, onAuthorizationSuccess, onAuthorizationFail)
                }else{
                    onAuthorizationFail.onAuthorizationFail()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onCancel.onCancel()
            }
        })
    }

    /** Updates the user nickname.
     *  Id is required. */
    fun updateNickname(user: User, processor: (Boolean) -> Unit){
        createNicknameIdPair(user.nickname, user.id)
        Firebase.database.getReference("users/${user.id}/nickname").setValue(user.nickname).addOnSuccessListener {
            processor(true)
        }.addOnFailureListener {
            processor(false)
        }
    }

    /** Updates the user occupation.
     *  Id is required. */
    fun updateOccupation(user: User, processor: (Boolean) -> Unit){
        Firebase.database.getReference("users/${user.id}/occupation").setValue(user.occupation).addOnSuccessListener {
            processor(true)
        }.addOnFailureListener {
            processor(false)
        }
    }

    fun updatePfp(uid: String, pfp: Uri, processor: (Boolean) -> Unit){
        FirebaseStorage.getInstance().reference.child("pfps/$uid").putFile(pfp).addOnSuccessListener{
            processor(true)
        }.addOnFailureListener{
            processor(false)
        }
    }

    fun getPfp(uid: String,  processor: (Uri?) -> Unit){
        FirebaseStorage.getInstance().reference.child("pfps/$uid").downloadUrl.addOnSuccessListener {
            processor(it)
        }.addOnFailureListener {
            processor(null)
        }
    }

    /** Updates the user entry.
     *  Creates a new one if it does not exist.
     *  Id is required. */
    private fun updateUser(user: User){

    }

    /** Updates the nickname : id pair. */
    private fun createNicknameIdPair(nickname: String, id: String){
        val pairRef = getPairRef(nickname)

        pairRef.setValue(id)
    }

    /** Removes the nickname : id pair. */
    private fun removeNicknameIdPair(nickname: String){
        val pairRef = getPairRef(nickname)

        pairRef.ref.removeValue()
    }

    private fun checkPassword(id: String, passwordHash: String, onCancel: OnCancel, onAuthorizationSuccess: OnAuthorizationSuccess, onAuthorizationFail: OnAuthorizationFail){
        val userPasswordRef = getUserPasswordRef(id)
        userPasswordRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value.toString() == passwordHash){
                    onAuthorizationSuccess.onAuthorizationSuccess()
                }else{
                    onAuthorizationFail.onAuthorizationFail()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onCancel.onCancel()
            }
        })

    }
}

interface OnNicknameExists {
    fun onNicknameExists()
}

interface OnCancel {
    fun onCancel()
}

interface OnAuthorizationFail{
    fun onAuthorizationFail()
}

interface OnAuthorizationSuccess{
    fun onAuthorizationSuccess()
}

interface OnGetUserInfo{
    fun onGetUserInfo(user: User)
}