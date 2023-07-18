package ge.ngachechiladze.messengerapp.activities

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ge.ngachechiladze.messengerapp.dao.OnCancel
import ge.ngachechiladze.messengerapp.databinding.MessagesBinding
import ge.ngachechiladze.messengerapp.viewmodels.MessageViewModel
import ge.ngachechiladze.messengerapp.viewmodels.UserViewModel

class MessagesActivity : AppCompatActivity() {

    private val messageViewModel: MessageViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private lateinit var binding: MessagesBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        binding = MessagesBinding.inflate(LayoutInflater.from(this@MessagesActivity))

        val uid = getSharedPreferences("login", MODE_PRIVATE).getString("uid", "") ?: ""

        Log.d("MESSAGES ACTIVITY", "User id: $uid")

        if(uid != ""){
            messageViewModel.loadAllMessages(
                uid,
                object : OnCancel {
                    override fun onCancel() {
                        Toast.makeText(
                            this@MessagesActivity,
                            "Sorry, messages could not be loaded!",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                },
            )

            val messages = messageViewModel.getAllMessages().value
            if(messages != null){
                Log.d("MESSAGES", "Displaying all messages: ")
                for(message in messages){
                    Log.d("MESSAGE", message.message)
                }
            }else{
                Log.d("MESSAGES", "No messages to display!")
            }
        }

        setContentView(binding.root)
    }
}