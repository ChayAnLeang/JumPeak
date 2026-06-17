package com.jp.jumpeak.presentation.ui.invoice.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jp.jumpeak.R
import com.jp.jumpeak.data.projection.InvoiceWithCustomer
import com.jp.jumpeak.databinding.ViewHolderInvoiceItemBinding
import com.jp.jumpeak.util.objects.AmountUtil
import com.jp.jumpeak.util.objects.DateUtil

class InvoiceAdapter(
    val context: Context,
    val onClick:(Long, Double) -> Unit
) : PagingDataAdapter<InvoiceWithCustomer, InvoiceAdapter.InvoiceViewHolder>(DIFF_CALLBACK) {
    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<InvoiceWithCustomer>(){
            override fun areItemsTheSame(oldItem: InvoiceWithCustomer, newItem: InvoiceWithCustomer): Boolean {
                return oldItem.invoiceId == newItem.invoiceId
            }
            override fun areContentsTheSame(oldItem: InvoiceWithCustomer, newItem: InvoiceWithCustomer): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val binding = ViewHolderInvoiceItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return InvoiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder,position: Int) {
        val invoiceWithCustomer = getItem(position)
        invoiceWithCustomer?.let { holder.bind(invoiceWithCustomer) }
    }

    inner class InvoiceViewHolder(
        val binding: ViewHolderInvoiceItemBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(invoice: InvoiceWithCustomer){
            val currencySymbol = invoice.currency.symbol
            val totalDue = invoice.totalDue
            val totalPaid = invoice.totalPaid
            val balance = totalDue - totalPaid
            val invoiceId = invoice.invoiceId
            val totalDueFormat = AmountUtil.format(totalDue,currencySymbol)
            val totalPaidFormat = AmountUtil.format(totalPaid,currencySymbol)
            val balanceFormat = AmountUtil.format(balance,currencySymbol)
            val invoiceNoFormat = context.getString(R.string.invoice_no_format,invoiceId)
            binding.apply {
                tvInvoiceNo.text = invoiceNoFormat
                tvPaidType.text = getPaidType(balance)
                tvCustomerName.text = invoice.fullName
                tvPhoneNumber.text = invoice.phoneNumber
                binding.tvDate.text = DateUtil.format(invoice.date)
                tvTotalDue.text = context.getString(R.string.total_due_format,totalDueFormat)
                tvTotalPaid.text = context.getString(R.string.total_paid_format,totalPaidFormat)
                tvBalance.text = context.getString(R.string.balance_format,balanceFormat)
                invoiceItem.setOnClickListener { onClick(invoiceId,totalPaid) }
            }
        }
    }

    private fun getPaidType(balance: Double): String{
        val paidType =  when{
            balance > 0 -> context.getString(R.string.unpaid)
            balance < 0 -> context.getString(R.string.over_payment)
            else -> context.getString(R.string.paid)
        }
        return context.getString(R.string.paid_type_format,paidType)
    }
}