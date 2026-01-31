package com.jp.jumpeak.presentation.ui.parties

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jp.jumpeak.data.dto.PartiesDTO
import com.jp.jumpeak.databinding.ViewHolderPartiesItemBinding
import com.jp.jumpeak.util.objects.AmountUtil

class PartiesAdapter(
    val onClick:(PartiesDTO, View) -> Unit
) : PagingDataAdapter<PartiesDTO, PartiesAdapter.PartiesViewHolder>(DIFF_CALLBACK){
    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PartiesDTO>(){
            override fun areItemsTheSame(oldItem: PartiesDTO,newItem: PartiesDTO): Boolean {
                return oldItem.parties.id == newItem.parties.id
            }

            override fun areContentsTheSame(oldItem: PartiesDTO,newItem: PartiesDTO): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartiesViewHolder {
        val binding = ViewHolderPartiesItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PartiesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PartiesViewHolder,position: Int) {
        val partiesDTO = getItem(position)
        partiesDTO?.let { holder.bind(partiesDTO) }
    }


    inner class PartiesViewHolder(
        val binding: ViewHolderPartiesItemBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(partiesDTO: PartiesDTO){
            val parties = partiesDTO.parties
            binding.apply {
                tvName.text = parties.name
                tvPhoneNumber.text = parties.phoneNumber
                tvOutStandingDebt.text = AmountUtil.format(partiesDTO.balance)
                partiesItem.setOnClickListener { onClick(partiesDTO,partiesItem) }
            }
        }
    }
}