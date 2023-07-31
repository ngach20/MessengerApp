package ge.ngachechiladze.messengerapp.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ge.ngachechiladze.messengerapp.CACHE_ID
import ge.ngachechiladze.messengerapp.CACHE_JOB
import ge.ngachechiladze.messengerapp.CACHE_NICKNAME
import ge.ngachechiladze.messengerapp.adapters.SearchUsersViewAdapter
import ge.ngachechiladze.messengerapp.databinding.MessagesBinding
import ge.ngachechiladze.messengerapp.EndlessRecyclerViewScrollListener
import ge.ngachechiladze.messengerapp.databinding.SearchPageBinding
import ge.ngachechiladze.messengerapp.adapters.SearchUsersViewListener
import ge.ngachechiladze.messengerapp.models.Contact
import ge.ngachechiladze.messengerapp.models.UserPublicData
import ge.ngachechiladze.messengerapp.viewmodels.SearchUsersViewModel
import ge.ngachechiladze.messengerapp.viewmodels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity(), SearchUsersViewListener {

    private lateinit var binding: SearchPageBinding

    private lateinit var searchUsersViewModel: SearchUsersViewModel
    lateinit var userViewModel: UserViewModel

    private val batchSize = 7
    private val debouncer = Debouncer(500)

    private lateinit var scrollListener : EndlessRecyclerViewScrollListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SearchPageBinding.inflate(LayoutInflater.from(this@SearchActivity))

        scrollListener = object : EndlessRecyclerViewScrollListener(binding.usersRecyclerView.layoutManager as LinearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadBatch(page)
            }
        }

        val uid = getSharedPreferences("login", MODE_PRIVATE).getString("uid", "") ?: ""

        searchUsersViewModel = ViewModelProvider(this@SearchActivity)[SearchUsersViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        loadBatch(0)
        val adapter = SearchUsersViewAdapter(this,this)
        binding.usersRecyclerView.adapter = adapter
        searchUsersViewModel.getSearchedUsers().observe(this@SearchActivity) { usersData ->
            adapter.users = usersData
            this.runOnUiThread {
                adapter.notifyDataSetChanged()
                if(adapter.users.isEmpty()){
                    binding.noUsers.visibility = View.VISIBLE
                }else{
                    binding.noUsers.visibility = View.GONE
                }
            }
        }
        binding.usersRecyclerView.addOnScrollListener(scrollListener)

        binding.bottomHome.addButton.visibility = View.GONE
        binding.bottomHome.homeButton .visibility = View.GONE
        binding.bottomHome.settingsButton.visibility = View.GONE

        binding.searchBar.addTextChangedListener(
            object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    s?.let{ it ->
                        val str = it.toString()
                        if(str.isEmpty() || str.length >= 3){
                            debouncer.debounce { search(str) }
                        }
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            }
        )

        binding.backButton.setOnClickListener {
            finish()
        }

        setContentView(binding.root)
    }

    private fun search(searchText: String){
        searchUsersViewModel.reset()
        scrollListener.resetState()
        searchUsersViewModel.filterUsers(searchText)
        loadBatch(0)
    }

    private fun loadBatch(page: Int){
        searchUsersViewModel.loadNextUserBatch(batchSize, page) {
            Toast.makeText(this@SearchActivity, "Could not connect to database!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClickListener(udata: UserPublicData) {
        startActivity(prepareJump(udata))
    }

    private fun prepareJump(udata: UserPublicData): Intent {
        return Intent(this@SearchActivity, ChatActivity::class.java).putExtra(CACHE_NICKNAME, udata.nickname).putExtra(
            CACHE_ID, udata.id).
        putExtra(CACHE_JOB, udata.occupation)
    }


}

class Debouncer(private val debounceDelay: Long) {
    private var debounceJob: Job? = null

    fun debounce(doAction: () -> Unit) {
        debounceJob?.cancel()
        debounceJob = CoroutineScope(Dispatchers.Main).launch {
            delay(debounceDelay)
            doAction()
        }
    }
}