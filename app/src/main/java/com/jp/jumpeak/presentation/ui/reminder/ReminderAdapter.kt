package com.jp.jumpeak.presentation.ui.reminder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jp.jumpeak.data.entity.Reminder
import com.jp.jumpeak.databinding.ViewHolderReminderItemBinding
import com.jp.jumpeak.util.objects.DateTimeUtil

class ReminderAdapter(
    val onClick:(Long) -> Unit
) : PagingDataAdapter<Reminder, ReminderAdapter.ReminderViewHolder>(DIFF_CALLBACK) {
    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Reminder>(){
            override fun areItemsTheSame(oldItem: Reminder,newItem: Reminder): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Reminder,newItem: Reminder): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): ReminderViewHolder {
        val binding = ViewHolderReminderItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder,position: Int) {
        val reminder = getItem(position)
        reminder?.let { holder.bind(reminder) }
    }

    inner class ReminderViewHolder(
        val binding: ViewHolderReminderItemBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(reminder: Reminder){
            binding.apply {
                tvMessage.text = reminder.message
                tvDateTime.text = DateTimeUtil.format(reminder.datetime)
                reminderItem.setOnClickListener { onClick(reminder.id) }
            }
        }
    }
}