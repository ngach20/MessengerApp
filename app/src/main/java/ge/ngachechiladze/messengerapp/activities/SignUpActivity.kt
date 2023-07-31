package ge.ngachechiladze.messengerapp.activities

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ge.ngachechiladze.messengerapp.Hasher
import ge.ngachechiladze.messengerapp.R
import ge.ngachechiladze.messengerapp.dao.OnCancel
import ge.ngachechiladze.messengerapp.dao.OnNicknameExists
import ge.ngachechiladze.messengerapp.databinding.SignUpBinding
import ge.ngachechiladze.messengerapp.models.User
import ge.ngachechiladze.messengerapp.dao.OnAuthorizationSuccess
import ge.ngachechiladze.messengerapp.viewmodels.UserViewModel

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding : SignUpBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        binding = SignUpBinding.inflate(LayoutInflater.from(this))

        binding.signUpButton.setOnClickListener {
            val nickname = binding.usernameEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val occupation = binding.whatIDoEditText.text.toString().trim()

            /** All fields are required to be filled */
            if(nickname.isEmpty() || password.isEmpty() || occupation.isEmpty()){
                Toast.makeText(this@SignUpActivity, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            }else{
                val user = User("", nickname, Hasher.hashString(password), occupation)
                register(user)
            }
        }

        setContentView(binding.root)
    }

    private fun register(user: User){
        userViewModel.register(user, object : OnNicknameExists {
            override fun onNicknameExists() {
                Toast.makeText(this@SignUpActivity, "Nickname already exists!", Toast.LENGTH_SHORT).show()
            }
        }, object : OnCancel {
            override fun onCancel() {
                Toast.makeText(this@SignUpActivity, "Failed to connect to database", Toast.LENGTH_SHORT).show()
            }
        }, object : OnAuthorizationSuccess {
            override fun onAuthorizationSuccess() {
                userViewModel.setBatmanImage(user.id, Uri.parse("android.resource://${packageName}/${R.drawable.avatar_image_placeholder}"))

                val result = Intent()
                result.putExtra("nickname", user.nickname)
                result.putExtra("passwordHash", user.passwordHash)
                setResult(Activity.RESULT_OK, result)
                finish()
            }
        })
    }
}