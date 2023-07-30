package ge.ngachechiladze.messengerapp.activities

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import ge.ngachechiladze.messengerapp.adapters.UsersViewAdapter
import ge.ngachechiladze.messengerapp.dao.OnCancel
import ge.ngachechiladze.messengerapp.databinding.MessagesBinding
import ge.ngachechiladze.messengerapp.models.Contact
import ge.ngachechiladze.messengerapp.viewmodels.MessageViewModel
import kotlin.math.abs


class MessagesActivity : AppCompatActivity() {

    private lateinit var messageViewModel: MessageViewModel

    private lateinit var binding: MessagesBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        val uid = getSharedPreferences("login", MODE_PRIVATE).getString("uid", "") ?: ""


        if(uid != ""){
            messageViewModel = ViewModelProvider(this, MessageViewModel.Factory(uid, object : OnCancel{
                override fun onCancel() {
                    Toast.makeText(this@MessagesActivity, "Could not connect to database", Toast.LENGTH_SHORT).show()
                }
            }))[MessageViewModel::class.java]

            binding = MessagesBinding.inflate(LayoutInflater.from(this@MessagesActivity))
        }else{
            Toast.makeText(this@MessagesActivity, "Sorry! Profile not found.", Toast.LENGTH_SHORT).show()
        }



        val usersRecyclerView = binding.usersRecyclerView
        val contactsAdapter = UsersViewAdapter()
        usersRecyclerView.adapter = contactsAdapter

        messageViewModel.getAllContacts().observe(this@MessagesActivity) { contacts ->
            contactsAdapter.users = contacts
            contactsAdapter.notifyDataSetChanged()

            if(contacts.isEmpty()){
                binding.scrollView.visibility = View.GONE
                binding.noContacts.visibility = View.VISIBLE
            }else{
                binding.scrollView.visibility = View.VISIBLE
                binding.noContacts.visibility = View.GONE
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


        binding.bottomHome.settingsButton.setOnClickListener {
            val intent = Intent(this@MessagesActivity, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}