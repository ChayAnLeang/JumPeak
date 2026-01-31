package com.jp.jumpeak.presentation.ui.transaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jp.jumpeak.data.entity.Transaction
import com.jp.jumpeak.databinding.ViewHolderTransactionItemBinding
import com.jp.jumpeak.enums.TransactionType
import com.jp.jumpeak.util.objects.AmountUtil
import com.jp.jumpeak.util.objects.DateTimeUtil

class TransactionAdapter(
    val onClick:(Boolean,Long, Double) -> Unit
) : PagingDataAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(DIFF_CALLBACK){
    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Transaction>(){
            override fun areItemsTheSame(oldItem: Transaction,newItem: Transaction): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Transaction,newItem: Transaction): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ViewHolderTransactionItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder,position: Int) {
        val transaction = getItem(position)
        transaction?.let { holder.bind(transaction) }
    }


    inner class TransactionViewHolder(
        val binding: ViewHolderTransactionItemBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(transaction: Transaction){
            val note = transaction.note
            val amountFormat = AmountUtil.format(transaction.amount)
            val (debt,repayment) = when(transaction.type){
                TransactionType.DEBT -> Pair(amountFormat,"")
                TransactionType.REPAYMENT -> Pair("",amountFormat)
            }
            binding.apply {
                tvDebt.text = debt
                tvRepayment.text = repayment
                tvNote.text = transaction.note
                tvDateTime.text = DateTimeUtil.format(transaction.datetime)
                transactionItem.setOnClickListener {
                    onClick(transaction.isSettled,transaction.id,transaction.amount)
                }
            }
            binding.tvNote.apply {
                if(note.isEmpty()){
                    isVisible = false
                }
                else{
                    text = note
                    isVisible = true
                }
            }
        }
    }
}