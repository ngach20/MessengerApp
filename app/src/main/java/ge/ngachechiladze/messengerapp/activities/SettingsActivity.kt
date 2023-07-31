package ge.ngachechiladze.messengerapp.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import ge.ngachechiladze.messengerapp.R
import ge.ngachechiladze.messengerapp.dao.OnCancel
import ge.ngachechiladze.messengerapp.databinding.SettingsBinding
import ge.ngachechiladze.messengerapp.models.User
import ge.ngachechiladze.messengerapp.viewmodels.UserViewModel

class SettingsActivity : AppCompatActivity()  {

    private lateinit var resultLauncherForImage: ActivityResultLauncher<Intent>
    private lateinit var binding: SettingsBinding
    private lateinit var userViewModel: UserViewModel
    private var pfpUrl: Uri? = null

    private var nickname: String = ""
    private var job: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsBinding.inflate(LayoutInflater.from(this))

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        resultLauncherForImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { final ->
                if(Activity.RESULT_OK != final.resultCode){
                    return@registerForActivityResult
                }
                if(null==final.data){
                    return@registerForActivityResult
                }
                val data: Intent? = final.data
                if (data != null) {
                    pfpUrl = data.data
                    binding.profilePicture.setImageURI(data.data)
                }
            }

//        binding.bottomHome.homeButton.setOnClickListener {
//            val intent = Intent(this@SettingsActivity, MessagesActivity::class.java)
//            startActivity(intent)
//            this.finish()
//        }

        val uid = getSharedPreferences("login", MODE_PRIVATE).getString("uid", "") ?: ""

        userViewModel.getUserData(uid, object : OnCancel {
            override fun onCancel() {
                Toast.makeText(this@SettingsActivity, "Failed to retrieve data from database", Toast.LENGTH_SHORT).show()
            }
        }).observe(this@SettingsActivity) { user ->
            binding.usernameEditText.setText(user.nickname)
            binding.jobEditText.setText(user.occupation)
            nickname = user.nickname
            job = user.occupation
        }

        updateDataDisplay(uid)

        binding.signOut.setOnClickListener {
            getSharedPreferences("login", MODE_PRIVATE).edit().putString("uid", "").apply()
            val intent = Intent(this@SettingsActivity, MainActivity::class.java)
            startActivity(intent)
            this.finish()
        }

        binding.profilePicture.setOnClickListener {
            resultLauncherForImage.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI))
        }

        binding.updateButton.setOnClickListener{
            val nicknamme = binding.usernameEditText.text.toString().trim()
            val occupation = binding.jobEditText.text.toString().trim()

            if(nicknamme.isEmpty() || occupation.isEmpty()){
                Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            userViewModel.updateUserData(User(uid, nickname = binding.usernameEditText.text.toString(),"", occupation = binding.jobEditText.text.toString())){it ->
                if(it){
                    if(pfpUrl!=null){
                        userViewModel.updatePfp(uid, pfpUrl!!){ itPfp->
                            if(itPfp){
                                Toast.makeText(this, "updated", Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(this, "user pfp uploading failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else {
                        Toast.makeText(this, "updated", Toast.LENGTH_SHORT).show()
                    }
                    updateDataDisplay(uid)
                }else{
                    Toast.makeText(this, "user data updating failed", Toast.LENGTH_SHORT).show()
                    binding.usernameEditText.setText(nickname)
                    binding.jobEditText.setText(job)
                    updateDataDisplay(uid)
                }
            }
        }

        menuInflater.inflate(R.menu.bottom_bar_menu_left, binding.home.menu)

        binding.home.setOnMenuItemClickListener { item ->
            when(item.itemId){
                R.id.action_home -> {
                    val messagesIntent = Intent(this@SettingsActivity, MessagesActivity::class.java)
                    startActivity(messagesIntent)
                    finish()
                    true
                }

                else -> {
                    true
                }
            }
        }

        binding.searchFab.setOnClickListener {
            val searchIntent = Intent(this@SettingsActivity, SearchActivity::class.java)
            startActivity(searchIntent)
        }

        setContentView(binding.root)
    }

    private fun updateDataDisplay(uid: String){
        userViewModel.getPfp(uid){
            if(it!=null){
                Glide.with(this)
                    .load(it)
                    .into(binding.profilePicture)
            }else{
                binding.profilePicture.setImageResource(R.drawable.avatar_image_placeholder)
            }
        }
    }
}
