package ge.ngachechiladze.messengerapp.activities

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ge.ngachechiladze.messengerapp.Hasher
import ge.ngachechiladze.messengerapp.databinding.SignUpBinding
import ge.ngachechiladze.messengerapp.models.User

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding : SignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignUpBinding.inflate(LayoutInflater.from(this))

        binding.signUpButton.setOnClickListener {
            val nickname = binding.usernameEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val occupation = binding.whatIDoEditText.text.toString().trim()

            if(nickname.isEmpty() || password.isEmpty() || occupation.isEmpty()){
                Toast.makeText(this@SignUpActivity, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            }else{
                val user = User(nickname, Hasher.hashString(password), occupation)
                register(user)
            }
        }

        setContentView(binding.root)
    }

    private fun register(user: User){
        val userRef = Firebase.database.getReference("users/${user.nickname}")

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(this@SignUpActivity, "Username already exists!", Toast.LENGTH_SHORT).show()
                } else {
                    userRef.setValue(user)
                    val result = Intent()
                    result.putExtra("nickname", user.nickname)
                    result.putExtra("passwordHash", user.passwordHash)
                    setResult(Activity.RESULT_OK, result)
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SignUpActivity, "Failed to retrieve data from database", Toast.LENGTH_SHORT).show()
            }
        })
    }
}