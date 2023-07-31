package ge.ngachechiladze.messengerapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ge.ngachechiladze.messengerapp.R
import ge.ngachechiladze.messengerapp.activities.SearchActivity
import ge.ngachechiladze.messengerapp.databinding.UserView2Binding
import ge.ngachechiladze.messengerapp.models.UserPublicData

interface SearchUsersViewListener{
    fun onClickListener(udata: UserPublicData)
}

class SearchUsersViewAdapter(var listener: SearchUsersViewListener, var searchActivity: SearchActivity): RecyclerView.Adapter<SearchUsersViewAdapter.UsersViewHolder>() {

    var users : List<UserPublicData> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val binding : UserView2Binding = UserView2Binding.inflate(LayoutInflater.from(parent.context))

        return UsersViewHolder(binding, listener, searchActivity)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.fillView(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }

    class UsersViewHolder(private val itemBinding: UserView2Binding, var listener: SearchUsersViewListener, var searchActivity: SearchActivity) : RecyclerView.ViewHolder(itemBinding.root){

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
            itemBinding.root.setOnClickListener { listener.onClickListener(user) }
            searchActivity.userViewModel.getPfp(user.id){
                if(it!=null){
                    Glide.with(searchActivity)
                        .load(it)
                        .into(itemBinding.profilePicture)
                }else{
                    itemBinding.profilePicture.setImageResource(R.drawable.avatar_image_placeholder)
                }
            }
        }

    }
}
