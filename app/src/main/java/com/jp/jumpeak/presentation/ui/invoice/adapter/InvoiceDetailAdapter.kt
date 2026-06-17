package com.jp.jumpeak.presentation.ui.invoice.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jp.jumpeak.data.entity.Item
import com.jp.jumpeak.databinding.ViewHolderInvoiceDetailItemBinding
import com.jp.jumpeak.enums.Currency
import com.jp.jumpeak.util.objects.AmountUtil

class InvoiceDetailAdapter : ListAdapter<Item, InvoiceDetailAdapter.InvoicePreviewViewHolder>(DIFF_CALLBACK) {
    private var currencySymbol = Currency.KHR.symbol

    fun setCurrencySymbol(currencySymbol: String){
        this.currencySymbol = currencySymbol
    }

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Item>(){
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoicePreviewViewHolder {
        val binding = ViewHolderInvoiceDetailItemBinding.inflate(LayoutInflater.from(parent.context),parent,false
        )
        return InvoicePreviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InvoicePreviewViewHolder,position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class InvoicePreviewViewHolder(
        val binding: ViewHolderInvoiceDetailItemBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: Item){
            val no = layoutPosition + 1
            val qtyFormat = "${item.qty} ${item.unit}"
            binding.apply {
                tvQty.text = qtyFormat
                tvNo.text = no.toString()
                tvGoods.text = item.goods
                tvPrice.text = AmountUtil.format(item.price,currencySymbol)
                tvAmount.text = AmountUtil.format(item.amount,currencySymbol)
            }
        }
    }
}