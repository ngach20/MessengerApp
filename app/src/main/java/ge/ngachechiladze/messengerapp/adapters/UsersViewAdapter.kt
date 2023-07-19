package ge.ngachechiladze.messengerapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import ge.ngachechiladze.messengerapp.databinding.UserViewBinding
import ge.ngachechiladze.messengerapp.models.Contact
import java.text.SimpleDateFormat
import java.util.*

class UsersViewAdapter: RecyclerView.Adapter<UsersViewAdapter.UsersViewHolder>() {

    var users : List<Contact> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val binding : UserViewBinding = UserViewBinding.inflate(LayoutInflater.from(parent.context))

        return UsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.fillView(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }

    class UsersViewHolder(private val itemBinding: UserViewBinding) : RecyclerView.ViewHolder(itemBinding.root){

        init {
            itemBinding.root.minWidth = 0
            itemBinding.root.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
        }

        fun fillView(contact: Contact){
            itemBinding.message.text = contact.mostRecentMessage
            itemBinding.username.text = contact.nickname

            val sentTime = contact.time
            val curTime = System.currentTimeMillis()

            //Less than one hour
            if((curTime - sentTime)/(1000 * 3600) < 1){
                itemBinding.date.text = "${(curTime - sentTime)/(1000 * 60)} min"
            }else if((curTime - sentTime)/(1000 * 3600 * 24) < 1){
                itemBinding.date.text = "${(curTime - sentTime)/(1000 * 3600)} hour"
            }else{
                val date = Date()
                date.time = sentTime

                val simpleDateFormat = SimpleDateFormat("MMM dd")

                itemBinding.date.text = simpleDateFormat.format(date).uppercase(Locale.getDefault())
            }
        }

    }
}

