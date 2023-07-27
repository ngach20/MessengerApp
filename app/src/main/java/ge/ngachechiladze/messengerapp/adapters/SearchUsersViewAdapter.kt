package ge.ngachechiladze.messengerapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import ge.ngachechiladze.messengerapp.databinding.UserView2Binding
import ge.ngachechiladze.messengerapp.databinding.UserViewBinding
import ge.ngachechiladze.messengerapp.models.Contact
import ge.ngachechiladze.messengerapp.models.UserPublicData
import java.text.SimpleDateFormat
import java.util.*

class SearchUsersViewAdapter: RecyclerView.Adapter<SearchUsersViewAdapter.UsersViewHolder>() {

    var users : List<UserPublicData> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val binding : UserView2Binding = UserView2Binding.inflate(LayoutInflater.from(parent.context))

        return UsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.fillView(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }

    class UsersViewHolder(private val itemBinding: UserView2Binding) : RecyclerView.ViewHolder(itemBinding.root){

        init {
            itemBinding.root.minWidth = 0
            itemBinding.root.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
        }

        fun fillView(user: UserPublicData){
            itemBinding.username.text = user.nickname
            itemBinding.occupation.text = user.occupation
        }

    }
}

