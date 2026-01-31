package com.jp.jumpeak.util.objects

import com.google.android.material.tabs.TabLayout

object TabLayoutUtil {
    fun setup(tabLayout: TabLayout,block:(position: Int) -> Unit){
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                block(tab?.position ?: 0)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
}