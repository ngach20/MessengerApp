package ge.ngachechiladze.messengerapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import ge.ngachechiladze.messengerapp.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import ge.ngachechiladze.messengerapp.CACHE_ID
import ge.ngachechiladze.messengerapp.CACHE_JOB
import ge.ngachechiladze.messengerapp.CACHE_NICKNAME
import ge.ngachechiladze.messengerapp.adapters.UsersViewAdapter
import ge.ngachechiladze.messengerapp.adapters.UsersViewListener
import ge.ngachechiladze.messengerapp.dao.OnCancel
import ge.ngachechiladze.messengerapp.databinding.MessagesBinding
import ge.ngachechiladze.messengerapp.models.Contact
import ge.ngachechiladze.messengerapp.viewmodels.MessageViewModel
import ge.ngachechiladze.messengerapp.viewmodels.UserViewModel
import kotlin.math.abs


class MessagesActivity : AppCompatActivity(), UsersViewListener {

    private lateinit var messageViewModel: MessageViewModel
    lateinit var userViewModel: UserViewModel


    private lateinit var binding: MessagesBinding

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        val uid = getSharedPreferences("login", MODE_PRIVATE).getString("uid", "") ?: ""


        if(uid != ""){
            messageViewModel = ViewModelProvider(this, MessageViewModel.Factory(uid, object : OnCancel{
                override fun onCancel() {
                    Toast.makeText(this@MessagesActivity, "Could not connect to database", Toast.LENGTH_SHORT).show()
                }
            }))[MessageViewModel::class.java]

            userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

            binding = MessagesBinding.inflate(LayoutInflater.from(this@MessagesActivity))
        }else{
            Toast.makeText(this@MessagesActivity, "Sorry! Profile not found.", Toast.LENGTH_SHORT).show()
        }



        val usersRecyclerView = binding.usersRecyclerView
        val contactsAdapter = UsersViewAdapter(this,this)
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
        menuInflater.inflate(R.menu.bottom_bar_menu_left_selected, binding.home.menu)

        binding.bottomAppBar.setOnMenuItemClickListener { item ->
            when(item.itemId){
                R.id.action_settings -> {
                    val settingsIntent = Intent(this@MessagesActivity, SettingsActivity::class.java)
                    startActivity(settingsIntent)
                    finish()
                    true
                }

                else -> {
                    true
                }
            }
        }

        binding.searchFab.setOnClickListener {
            val searchIntent = Intent(this@MessagesActivity, SearchActivity::class.java)
            startActivity(searchIntent)
        }

        setContentView(binding.root)
    }

    private fun prepareJump(contact: Contact): Intent{
        return Intent(this@MessagesActivity, ChatActivity::class.java).putExtra(CACHE_NICKNAME, contact.nickname).putExtra(CACHE_ID, contact.id).
        putExtra(CACHE_JOB, contact.occupation)
    }

    override fun onClickListener(contact: Contact) {
        startActivity(prepareJump(contact))
    }
}