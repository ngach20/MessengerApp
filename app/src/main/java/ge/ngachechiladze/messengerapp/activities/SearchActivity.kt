package ge.ngachechiladze.messengerapp.activities

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
import ge.ngachechiladze.messengerapp.adapters.SearchUsersViewAdapter
import ge.ngachechiladze.messengerapp.databinding.MessagesBinding
import ge.ngachechiladze.messengerapp.EndlessRecyclerViewScrollListener
import ge.ngachechiladze.messengerapp.viewmodels.SearchUsersViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity(){

    /** TODO: To be changed later */
    private lateinit var binding: MessagesBinding

    private lateinit var searchUsersViewModel: SearchUsersViewModel

    private val batchSize = 7
    private val debouncer = Debouncer(500)

    private lateinit var scrollListener : EndlessRecyclerViewScrollListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MessagesBinding.inflate(LayoutInflater.from(this@SearchActivity))
        /** TODO: Change later */
        binding.noContacts.visibility = View.GONE

        scrollListener = object : EndlessRecyclerViewScrollListener(binding.usersRecyclerView.layoutManager as LinearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadBatch(page)
            }
        }

        val uid = getSharedPreferences("login", MODE_PRIVATE).getString("uid", "") ?: ""

        searchUsersViewModel = ViewModelProvider(this@SearchActivity)[SearchUsersViewModel::class.java]
        loadBatch(0)
        val adapter = SearchUsersViewAdapter()
        binding.usersRecyclerView.adapter = adapter
        searchUsersViewModel.getSearchedUsers().observe(this@SearchActivity) { usersData ->
            adapter.users = usersData
            this.runOnUiThread {
                adapter.notifyDataSetChanged()
            }
        }
        binding.usersRecyclerView.addOnScrollListener(scrollListener)

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