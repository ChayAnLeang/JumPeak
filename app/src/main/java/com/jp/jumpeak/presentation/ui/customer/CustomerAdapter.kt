package com.jp.jumpeak.presentation.ui.customer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jp.jumpeak.data.entity.Customer
import com.jp.jumpeak.databinding.ViewHolderCustomerItemBinding

class CustomerAdapter(
    val onClick:(Long, String) -> Unit
) : PagingDataAdapter<Customer, CustomerAdapter.CustomerViewHolder>(DIFF_CALLBACK) {
    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Customer>(){
            override fun areItemsTheSame(oldItem: Customer, newItem: Customer): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: Customer, newItem: Customer): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding = ViewHolderCustomerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder,position: Int) {
        val customer = getItem(position)
        customer?.let { holder.bind(it) }
    }

    inner class CustomerViewHolder(
        val binding: ViewHolderCustomerItemBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(customer: Customer){
            val fullName = customer.fullName
            binding.apply {
                tvFullName.text = fullName
                tvPhoneNumber.text = customer.phoneNumber
                tvAddress.text = customer.address.ifEmpty { "- - -" }
                customerItem.setOnClickListener{ onClick(customer.id,fullName) }
            }
        }
    }
}