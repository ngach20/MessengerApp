package ge.ngachechiladze.messengerapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ge.ngachechiladze.messengerapp.Hasher
import ge.ngachechiladze.messengerapp.databinding.SignInBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: SignInBinding

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

        binding = SignInBinding.inflate(LayoutInflater.from(this))

        binding.signUpButton.setOnClickListener {
            val intent = Intent(this@MainActivity, SignUpActivity::class.java)
            registerActivityResult.launch(intent)
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

    fun login(nickname: String, passwordHash: String){
        val usersRef = Firebase.database.getReference("users/$nickname")

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val passRef = Firebase.database.getReference("users/$nickname/password")

                    passRef.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(passSnapshot: DataSnapshot) {
                            val retrievedPasswordHash = passSnapshot.value

                            //Password is correct
                            if(retrievedPasswordHash == passwordHash){
                                /** TODO -------Authenticate----------- */

                                Toast.makeText(this@MainActivity, "Login successful", Toast.LENGTH_SHORT).show()

                                /** TODO --------Start new activity------ */

                                finish()
                            }else{
                                Toast.makeText(this@MainActivity, "Incorrect user information", Toast.LENGTH_SHORT).show()
                                Log.d("LOGIN", "Incorrect password")
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@MainActivity, "Failed to retrieve data from database", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(this@MainActivity, "Incorrect user information", Toast.LENGTH_SHORT).show()
                    Log.d("LOGIN", "Username does not exist")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to retrieve data from database", Toast.LENGTH_SHORT).show()
            }
        })
    }
}