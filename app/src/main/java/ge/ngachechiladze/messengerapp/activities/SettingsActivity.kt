package ge.ngachechiladze.messengerapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ge.ngachechiladze.messengerapp.dao.OnCancel
import ge.ngachechiladze.messengerapp.databinding.SettingsBinding
import ge.ngachechiladze.messengerapp.viewmodels.UserViewModel

class SettingsActivity : AppCompatActivity()  {

    private lateinit var binding: SettingsBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        binding.bottomHome.homeButton.setOnClickListener {
            val intent = Intent(this@SettingsActivity, MessagesActivity::class.java)
            startActivity(intent)
            this.finish()
        }


        binding.usernameEditText.setText(userViewModel.getUserData().value?.nickname ?: "N/A")
        binding.jobEditText.setText(userViewModel.getUserData().value?.occupation ?: "N/A")

    }
}