package ge.ngachechiladze.messengerapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = SignInBinding.inflate(LayoutInflater.from(this))

        binding.signUpButton.setOnClickListener {
            val intent = Intent(this@MainActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.signInButton.setOnClickListener {
            if(binding.usernameEditText.text.toString().trim().isNotEmpty() ||
                binding.passwordEditText.text.toString().trim().isNotEmpty()){
                val nickname = binding.usernameEditText.text.toString().trim()
                val password = binding.passwordEditText.text.toString().trim()

                login(nickname, password)
            }else{
                Toast.makeText(this@MainActivity, "Please fill all fields!", Toast.LENGTH_SHORT).show()
            }
        }
//        val myRef = database.getReference("message")
//
//        myRef.setValue("Hello, World!")



        setContentView(binding.root)
    }

    private fun login(nickname: String, password: String){
        val passwordHash = Hasher.hashString(password)

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
                                /** -------Authenticate----------- */
                                Toast.makeText(this@MainActivity, "Login successful", Toast.LENGTH_SHORT).show()
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