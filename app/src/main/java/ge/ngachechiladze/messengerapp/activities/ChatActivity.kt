package ge.ngachechiladze.messengerapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import ge.ngachechiladze.messengerapp.CACHE_ID
import ge.ngachechiladze.messengerapp.CACHE_JOB
import ge.ngachechiladze.messengerapp.CACHE_NICKNAME
import ge.ngachechiladze.messengerapp.R
import ge.ngachechiladze.messengerapp.adapters.ChatAdapter
import ge.ngachechiladze.messengerapp.dao.OnCancel
import ge.ngachechiladze.messengerapp.databinding.ChatBinding
import ge.ngachechiladze.messengerapp.models.Message
import ge.ngachechiladze.messengerapp.models.UserPublicData
import ge.ngachechiladze.messengerapp.viewmodels.MessageViewModel
import ge.ngachechiladze.messengerapp.viewmodels.UserViewModel

class ChatActivity: AppCompatActivity() {
    private var messages = mutableListOf<Message>()
    private lateinit var binding: ChatBinding
    private lateinit var adapter: ChatAdapter
    private lateinit var messageViewModel: MessageViewModel
    lateinit var userViewModel: UserViewModel


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        val recepient = UserPublicData(intent.getStringExtra(CACHE_ID).toString(),intent.getStringExtra(CACHE_NICKNAME).toString(),intent.getStringExtra(CACHE_JOB).toString())
        val senderUid = getSharedPreferences("login", MODE_PRIVATE).getString("uid", "") ?: ""

        if(senderUid != ""){
            messageViewModel = ViewModelProvider(this, MessageViewModel.Factory(senderUid, object : OnCancel {
                override fun onCancel() {
                    Toast.makeText(this@ChatActivity, "Could not connect to database", Toast.LENGTH_SHORT).show()
                }
            }))[MessageViewModel::class.java]

            userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

            binding = ChatBinding.inflate(LayoutInflater.from(this))
        }else{
            Toast.makeText(this@ChatActivity, "Sorry! Profile not found.", Toast.LENGTH_SHORT).show()
        }

        setContentView(binding.root)


        binding.nicknameSmall.text = recepient.nickname

        binding.goBackSmall.setOnClickListener {
            val intent = Intent(this@ChatActivity, MessagesActivity::class.java)
            startActivity(intent)
            this.finish()
        }

        adapter = ChatAdapter(senderUid,messages)
        binding.recyclerChat.adapter = adapter

        messageViewModel.setMessagesWatcher(senderUid,recepient.id){
            fetchMessages()
        }

        binding.sendOutButton.setOnClickListener {
            var msg = binding.editFieldForMessage.text.toString()
            messageViewModel.sendMessage(Message("",msg,System.currentTimeMillis(),senderUid,recepient.id))
            binding.editFieldForMessage.setText("")
        }

        userViewModel.getPfp(recepient.id){
            if(it!=null){
                Glide.with(this)
                    .load(it)
                    .into(binding.pfpSmall)
            }else{
                binding.pfpSmall.setImageResource(R.drawable.avatar_image_placeholder)
            }
        }
    }

    private fun fetchMessages() {
        messageViewModel.getMessages() { param: MutableList<Message> ->
            adapter.messages = param
            adapter.notifyDataSetChanged()
        }
    }
}