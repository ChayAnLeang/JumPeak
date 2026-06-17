package com.jp.jumpeak.presentation.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.jp.jumpeak.R
import com.jp.jumpeak.databinding.ActivityMainBinding
import com.jp.jumpeak.presentation.ui.BusinessProfileActivity
import com.jp.jumpeak.presentation.ui.ThemeBottomSheetFragment
import com.jp.jumpeak.presentation.viewmodel.CustomerNameShareViewModel
import com.jp.jumpeak.util.classes.NavigationUtil
import com.jp.jumpeak.util.objects.TextWatcherUtil
import com.jp.jumpeak.util.objects.WindowInsertsListenerUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var searchJob: Job ?= null
    private val navigationUtil by lazy { NavigationUtil(this) }
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val customerNameShareViewModel: CustomerNameShareViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        WindowInsertsListenerUtil.setup(binding.main)
    }

    private fun initView(){
        setupToolbar()
        setupSearchCustomerByName()
        setupViewPager()
        setupTabLayout()
    }

    private fun setupSearchCustomerByName(){
        val textWatcher = TextWatcherUtil.setup {
            searchJob?.cancel()
            searchJob = MainScope().launch {
                delay(300)
                val name = binding.etSearch.text.toString().trim()
                customerNameShareViewModel.setName(name)
            }
        }
        binding.etSearch.addTextChangedListener(textWatcher)
    }

    private fun setupViewPager(){
        binding.vp2.apply {
            adapter = ViewPagerAdapter(this@MainActivity)
            setCurrentItem(1,false)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.etSearch.setText(null)
                    customerNameShareViewModel.setName("")
                }
            })
        }
    }

    private fun setupTabLayout(){
        TabLayoutMediator(binding.tl, binding.vp2) { tab, position ->
            when (position) {
                0 -> tab.setText(getString(R.string.customer))
                1 -> tab.setText(getString(R.string.invoice))
            }
        }.attach()
    }

    private fun setupToolbar(){
        binding.mtb.setOnMenuItemClickListener{ items ->
            when(items.itemId){
                R.id.theme -> showThemeBottomSheet(ThemeBottomSheetFragment())
                R.id.business -> navigationUtil.navigateTo(BusinessProfileActivity::class.java)
            }
            true
        }
    }

    private fun showThemeBottomSheet(bottomSheet: BottomSheetDialogFragment){
        bottomSheet.show(supportFragmentManager,bottomSheet.tag)
    }
}