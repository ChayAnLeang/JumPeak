package com.jp.jumpeak.presentation.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jp.jumpeak.presentation.ui.customer.fragment.CustomerFragment
import com.jp.jumpeak.presentation.ui.invoice.fragment.InvoiceFragment

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity){
    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> CustomerFragment()
            1 -> InvoiceFragment()
            else -> InvoiceFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}