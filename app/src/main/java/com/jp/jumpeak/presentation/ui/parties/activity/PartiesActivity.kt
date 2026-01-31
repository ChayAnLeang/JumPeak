package com.jp.jumpeak.presentation.ui.parties.activity

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.paging.LoadState
import com.jp.jumpeak.R
import com.jp.jumpeak.data.entity.Parties
import com.jp.jumpeak.databinding.ActivityPartiesBinding
import com.jp.jumpeak.enums.PartiesType
import com.jp.jumpeak.presentation.ui.BaseActivity
import com.jp.jumpeak.presentation.ui.MoreBottomSheetFragment
import com.jp.jumpeak.presentation.ui.parties.PartiesAdapter
import com.jp.jumpeak.presentation.ui.reminder.activity.ReminderActivity
import com.jp.jumpeak.presentation.ui.transaction.activity.AddTransactionActivity
import com.jp.jumpeak.presentation.ui.transaction.activity.TransactionActivity
import com.jp.jumpeak.presentation.viewmodel.PartiesViewModel
import com.jp.jumpeak.util.classes.NavigationUtil
import com.jp.jumpeak.util.objects.TabLayoutUtil
import com.jp.jumpeak.util.objects.WindowInsertsListenerUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PartiesActivity : BaseActivity() {
    private val navigationUtil by lazy { NavigationUtil(this) }
    private val partiesViewModel: PartiesViewModel by viewModels()
    private val binding by lazy { ActivityPartiesBinding.inflate(layoutInflater) }
    private val partiesAdapter by lazy {
        PartiesAdapter { partiesDTO, partiesItem -> showPopupMenu(partiesDTO.parties, partiesItem) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        observePartiesByPartiesType()
    }

    private fun showPopupMenu(parties: Parties, partiesItem: View){
        val popupMenu = PopupMenu(this, partiesItem)
        popupMenu.apply {
            gravity = Gravity.END
            menuInflater.inflate(R.menu.parties_option_menu, popupMenu.menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.edit -> {
                        navigationUtil.navigateTo(
                            EditPartiesActivity::class.java,
                            bundleOf("parties_id" to parties.id)
                        )
                    }
                    R.id.view_transaction -> {
                        navigationUtil.navigateTo(
                            TransactionActivity::class.java,
                            bundleOf(
                                "parties_id" to parties.id,
                                "parties_name" to parties.name,
                                "phone_number" to parties.phoneNumber
                            )
                        )
                    }
                }
                true
            }
            show()
        }
    }

    private fun initView(){
        setupToolbar()
        handleTabLayout()
        WindowInsertsListenerUtil.setup(binding.main)
        binding.apply {
            rv.adapter = partiesAdapter
            tvAddPeopleWhoOweMe.setOnClickListener {
                navigateToAddPartiesActivity(getString(R.string.add_person_who_owe_me))
            }
            tvAddPeopleIOwe.setOnClickListener {
                navigateToAddPartiesActivity(getString(R.string.add_person_i_owe))
            }
            tvAddDebt.setOnClickListener {
                navigateToAddTransactionActivity(getString(R.string.add_debt))
            }
            tvAddRepayment.setOnClickListener {
                navigateToAddTransactionActivity(getString(R.string.add_repayment))
            }
        }
    }

    private fun setupToolbar(){
        binding.mtb.setOnMenuItemClickListener { items ->
            when(items.itemId){
                R.id.more -> {
                    val moreBottomSheetFragment = MoreBottomSheetFragment()
                    moreBottomSheetFragment.show(supportFragmentManager,moreBottomSheetFragment.tag)
                }
                R.id.reminder -> {
                    navigationUtil.navigateTo(ReminderActivity::class.java)
                }
            }
            true
        }
    }

    private fun handleTabLayout(){
        TabLayoutUtil.setup(binding.tl) { position ->
            when(position){
                0 -> partiesViewModel.setPartiesType(PartiesType.OWE_ME)
                1 -> partiesViewModel.setPartiesType(PartiesType.I_OWE)
            }
        }
    }

    private fun navigateToAddPartiesActivity(title: String){
        navigationUtil.navigateTo(AddPartiesActivity::class.java, bundleOf("title" to title))
    }

    private fun navigateToAddTransactionActivity(title: String){
        navigationUtil.navigateTo(AddTransactionActivity::class.java, bundleOf("title" to title))
    }

    private fun observePartiesByPartiesType(){
        partiesViewModel.partiesByPartiesType.observe(this) { parties ->
            partiesAdapter.submitData(lifecycle,parties)
        }
        partiesAdapter.addLoadStateListener { loadState ->
            when(loadState.refresh){
                is LoadState.Loading -> binding.pb.isVisible = true
                is LoadState.NotLoading -> {
                    if(partiesAdapter.itemCount == 0) showData(false)
                    else showData(true)
                }
                is LoadState.Error -> showData(false)
            }
        }
    }

    private fun showData(isVisible: Boolean){
        binding.apply {
            pb.isVisible = false
            rv.isVisible = isVisible
            icPerson.isVisible = !isVisible
            tvNoData.isVisible = !isVisible
        }
    }
}