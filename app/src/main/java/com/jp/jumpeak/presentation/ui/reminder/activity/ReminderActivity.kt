package com.jp.jumpeak.presentation.ui.reminder.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.paging.LoadState
import com.jp.jumpeak.databinding.ActivityReminderBinding
import com.jp.jumpeak.presentation.ui.BaseActivity
import com.jp.jumpeak.presentation.ui.reminder.ReminderAdapter
import com.jp.jumpeak.presentation.viewmodel.ReminderViewModel
import com.jp.jumpeak.util.classes.NavigationUtil
import com.jp.jumpeak.util.objects.WindowInsertsListenerUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReminderActivity : BaseActivity() {
    private val navigationUtil by lazy { NavigationUtil(this) }
    private val reminderViewModel: ReminderViewModel by viewModels()
    private val binding by lazy { ActivityReminderBinding.inflate(layoutInflater) }
    private val reminderAdapter by lazy {
        ReminderAdapter { reminderId ->
            navigationUtil.navigateTo(
                ManageReminderActivity::class.java,
                bundleOf("is_form_add" to false,"reminder_id" to reminderId)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        observeReminderByPartiesId()
    }

    private fun initView(){
        WindowInsertsListenerUtil.setup(binding.main)
        binding.apply {
            rv.adapter = reminderAdapter
            mtb.setNavigationOnClickListener { finish() }
            tvAddReminder.setOnClickListener {
                navigationUtil.navigateTo(ManageReminderActivity::class.java)
            }
        }
    }

    private fun observeReminderByPartiesId(){
        reminderViewModel.allReminders.observe(this) { reminders ->
            reminderAdapter.submitData(lifecycle,reminders)
        }
        reminderAdapter.addLoadStateListener { loadState ->
            when(loadState.refresh){
                is LoadState.Loading -> binding.pb.isVisible = true
                is LoadState.NotLoading -> {
                    if(reminderAdapter.itemCount == 0) showData(false)
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
            tvNoData.isVisible = !isVisible
            icReminder.isVisible = !isVisible
        }
    }
}