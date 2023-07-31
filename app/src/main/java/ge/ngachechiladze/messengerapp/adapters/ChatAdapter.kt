package ge.ngachechiladze.messengerapp.adapters

import ge.ngachechiladze.messengerapp.RECEIVING_ID
import ge.ngachechiladze.messengerapp.SENDING_ID
import ge.ngachechiladze.messengerapp.databinding.ReceivingMessageBinding
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ge.ngachechiladze.messengerapp.databinding.SendingMessageBinding
import ge.ngachechiladze.messengerapp.models.Message
import java.text.SimpleDateFormat
import java.util.*
import android.view.LayoutInflater


class SendingVH(binding: SendingMessageBinding) :
    RecyclerView.ViewHolder(binding.root) {
    val stamp = binding.timeOfSending
    val content = binding.messageContent
}

class ReceivingVH(binding: ReceivingMessageBinding) :
    RecyclerView.ViewHolder(binding.root) {
    val stamp = binding.timeOfSending
    val content = binding.messageContent
}

class ChatAdapter(val uid: String, var messages: MutableList<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return messages.size
    }

    private fun sendingFill(holder: SendingVH, message: Message){
        holder.content.text = message.message
        holder.stamp.text =formatTime(message.time)
    }

    private fun receivingFill(holder: ReceivingVH, message: Message){
        holder.content.text = message.message
        holder.stamp.text =formatTime(message.time)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (ReceivingVH::class.java == holder.javaClass) {
            receivingFill(holder as ReceivingVH, messages[position])
        } else {
            sendingFill(holder as SendingVH, messages[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (SENDING_ID == viewType) {
            return SendingVH(SendingMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
        return ReceivingVH(ReceivingMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        if (messages[position].receiverId == uid) {
            return RECEIVING_ID
        }
        return SENDING_ID
    }
}

fun formatTime(time: Long): String{
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val date = Date(time)
    return sdf.format(date)
}