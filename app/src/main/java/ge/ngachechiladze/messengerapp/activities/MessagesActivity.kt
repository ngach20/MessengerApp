package ge.ngachechiladze.messengerapp.activities

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import ge.ngachechiladze.messengerapp.dao.OnCancel
import ge.ngachechiladze.messengerapp.databinding.MessagesBinding
import ge.ngachechiladze.messengerapp.viewmodels.MessageViewModel
import kotlin.math.abs


class MessagesActivity : AppCompatActivity() {

    private lateinit var messageViewModel: MessageViewModel
    //private val userViewModel: UserViewModel by viewModels()

    private lateinit var binding: MessagesBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        val uid = getSharedPreferences("login", MODE_PRIVATE).getString("uid", "") ?: ""

        messageViewModel = ViewModelProvider(this, MessageViewModel.Factory(uid, object : OnCancel{
            override fun onCancel() {
                Toast.makeText(this@MessagesActivity, "Could not connect to database", Toast.LENGTH_SHORT).show()
            }
        }))[MessageViewModel::class.java]

        binding = MessagesBinding.inflate(LayoutInflater.from(this@MessagesActivity))


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
                }
            )
        }

        messageViewModel.getAllMessages().observe(this@MessagesActivity) {messageList ->
            if(messageList != null){
                Log.d("MESSAGES", "Displaying all messages: ")
                for(message in messageList){
                    Log.d("MESSAGE", message.message)
                }
            }else{
                Log.d("MESSAGES", "No messages to display!")
            }
        }

        val appBar = binding.appBar
        appBar.addOnOffsetChangedListener { _, verticalOffset ->
            binding.appBarBackground.alpha = 1.0f - abs(
                verticalOffset /
                        appBar.totalScrollRange
            )
        }

        setContentView(binding.root)
    }
}