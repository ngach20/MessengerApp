package ge.ngachechiladze.messengerapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ge.ngachechiladze.messengerapp.Hasher
import ge.ngachechiladze.messengerapp.dao.OnCancel
import ge.ngachechiladze.messengerapp.databinding.SignInBinding
import ge.ngachechiladze.messengerapp.dao.OnAuthorizationFail
import ge.ngachechiladze.messengerapp.dao.OnAuthorizationSuccess
import ge.ngachechiladze.messengerapp.viewmodels.UserViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: SignInBinding
    private lateinit var userViewModel: UserViewModel

    private val registerActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.let {
                val nickname = it.getStringExtra("nickname")
                val passwordHash = it.getStringExtra("passwordHash")

                if (nickname != null && passwordHash != null) {
                    login(nickname, passwordHash)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userViewModel = ViewModelProvider(this@MainActivity)[UserViewModel::class.java]

        binding = SignInBinding.inflate(LayoutInflater.from(this))

        binding.signUpButton.setOnClickListener {
            val signUpIntent = Intent(this@MainActivity, SignUpActivity::class.java)
            registerActivityResult.launch(signUpIntent)
        }

        //This executes when login is successful
        val userData = userViewModel.getUserData()
        userData.observe(this) { user ->
            getSharedPreferences("login", MODE_PRIVATE).edit().putString("uid", user.id).apply()

            val loginIntent = Intent(this@MainActivity, MessagesActivity::class.java)

            startActivity(loginIntent)
            finish()
        }

        binding.signInButton.setOnClickListener {
            if(binding.usernameEditText.text.toString().trim().isNotEmpty() ||
                binding.passwordEditText.text.toString().trim().isNotEmpty()){
                val nickname = binding.usernameEditText.text.toString().trim()
                val password = binding.passwordEditText.text.toString().trim()

                login(nickname, Hasher.hashString(password))
            }else{
                Toast.makeText(this@MainActivity, "Please fill all fields!", Toast.LENGTH_SHORT).show()
            }
        }
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()

        val uid = getSharedPreferences("login", MODE_PRIVATE).getString("uid", "") ?: ""

        //User was already authorized
        if(uid != ""){
            val messagesIntent = Intent(this@MainActivity, MessagesActivity::class.java)
            startActivity(messagesIntent)
            finish()
        }
    }

    private fun login(nickname: String, passwordHash: String){

        userViewModel.login(nickname, passwordHash, object : OnCancel {
            override fun onCancel() {
                Toast.makeText(this@MainActivity, "Failed to retrieve data from database", Toast.LENGTH_SHORT).show()
            }
        }, object : OnAuthorizationFail {
            override fun onAuthorizationFail() {
                Toast.makeText(this@MainActivity, "Incorrect user information", Toast.LENGTH_SHORT).show()
            }
        }, object : OnAuthorizationSuccess {
            override fun onAuthorizationSuccess() {
                Toast.makeText(this@MainActivity, "Login success!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}