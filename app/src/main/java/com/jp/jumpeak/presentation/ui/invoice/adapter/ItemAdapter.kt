package com.jp.jumpeak.presentation.ui.invoice.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jp.jumpeak.R
import com.jp.jumpeak.data.entity.Item
import com.jp.jumpeak.databinding.ViewHolderItemBinding
import com.jp.jumpeak.enums.Currency
import com.jp.jumpeak.util.objects.AmountUtil
import com.jp.jumpeak.util.objects.PopupMenuUtil

class ItemAdapter(
    val context: Context,
    val onClick:(Item,isEdit: Boolean) -> Unit
) : ListAdapter<Item, ItemAdapter.ItemViewHolder>(DIFF_CALLBACK) {
    private var currencySymbol = Currency.KHR.symbol

    fun setCurrency(currencySymbol: String){
        this.currencySymbol = currencySymbol
        notifyDataSetChanged()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ViewHolderItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder,position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ItemViewHolder(val binding: ViewHolderItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: Item){
            val priceFormat = AmountUtil.format(item.price,currencySymbol)
            val amountFormat = AmountUtil.format(item.amount,currencySymbol)
            val totalFormat = "${item.qty} ${item.unit}  *  $priceFormat  =  $amountFormat"
            binding.apply {
                tvGoods.text = item.goods
                tvTotal.text = totalFormat
                invoiceItem.setOnClickListener { showPopupMenu(binding.icArrow,item) }
            }
        }
    }

    private fun showPopupMenu(view: View,item: Item){
        PopupMenuUtil.setup(context,view,R.menu.edit_delete_menu) { listener ->
            when(listener.itemId){
                R.id.edit -> onClick(item,true)
                R.id.delete -> onClick(item,false)
            }
        }.show()
    }
}