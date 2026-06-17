package com.jp.jumpeak.presentation.ui.payment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jp.jumpeak.R
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jp.jumpeak.data.entity.Payment
import com.jp.jumpeak.databinding.ViewHolderPaymentItemBinding
import com.jp.jumpeak.enums.Currency
import com.jp.jumpeak.util.objects.AmountUtil
import com.jp.jumpeak.util.objects.DateUtil

class PaymentAdapter(
    val context: Context,
    val onClick:(Long) -> Unit
) : ListAdapter<Payment, PaymentAdapter.PaymentViewHolder>(DIFF_CALLBACK) {
    private var currencySymbol = Currency.KHR.symbol

    fun setCurrencySymbol(currencySymbol: String){
        this.currencySymbol = currencySymbol
    }

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Payment>(){
            override fun areItemsTheSame(oldItem: Payment, newItem: Payment): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: Payment, newItem: Payment): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val binding = ViewHolderPaymentItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PaymentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder,position: Int) {
        val payment = getItem(position)
        holder.bind(payment)
    }

    inner class PaymentViewHolder(
        val binding: ViewHolderPaymentItemBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(payment: Payment){
            val amount = payment.amount
            val amountFormat = AmountUtil.format(amount,currencySymbol)
            binding.apply {
                tvDate.text = DateUtil.format(payment.date)
                tvPaid.text = context.getString(R.string.paid_format,amountFormat)
                tvPaymentMethod.text = context.getString(R.string.payment_method_format,payment.paymentMethod)
                paymentItem.setOnClickListener{ onClick(payment.id) }
            }
        }
    }
}