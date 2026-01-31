package com.jp.jumpeak.presentation.ui.reminder.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.jp.jumpeak.R
import com.jp.jumpeak.data.entity.Reminder
import com.jp.jumpeak.databinding.ActivityManageReminderBinding
import com.jp.jumpeak.enums.Action
import com.jp.jumpeak.presentation.ui.BaseActivity
import com.jp.jumpeak.presentation.viewmodel.ReminderViewModel
import com.jp.jumpeak.util.classes.DateTimePickerDialogUtil
import com.jp.jumpeak.util.classes.DialogUtil
import com.jp.jumpeak.util.objects.DateTimeUtil
import com.jp.jumpeak.util.objects.NotificationPermissionUtil
import com.jp.jumpeak.util.objects.TextWatcherUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageReminderActivity : BaseActivity() {
    private var isFormAdd = true
    private val dialogUtil by lazy { DialogUtil(this) }
    private val reminderViewModel: ReminderViewModel by viewModels()
    private val binding by lazy { ActivityManageReminderBinding.inflate(layoutInflater) }
    private val dateTimePickerDialogUtil by lazy {
        DateTimePickerDialogUtil(supportFragmentManager) { datetime ->
            binding.tietDateTime.setText(DateTimeUtil.format(datetime))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        showAddOrEditReminderForm()
        observeReminderById()
        observeManageReminder()
    }

    private fun showAddOrEditReminderForm(){
        isFormAdd = intent.getBooleanExtra("is_form_add",true)
        if(isFormAdd){
            showAddForm()
        }
        else{
            val reminderId = intent.getLongExtra("reminder_id",0L)
            reminderViewModel.getById(reminderId)
        }
    }

    private fun initView(){
        handleValidation()
        binding.mtb.apply {
            setNavigationOnClickListener { finish() }
            title = if(isFormAdd) getString(R.string.add_reminder) else getString(R.string.edit)
        }
        binding.tietDateTime.setOnClickListener { dateTimePickerDialogUtil.show() }
    }

    private fun handleValidation(){
        val textWatcher = TextWatcherUtil.setup {
            val dateTime = binding.tietDateTime.text.toString()
            val message = binding.tietMessage.text.toString().trim()
            val isEnabled = dateTime.isNotEmpty() && message.isNotEmpty()
            binding.mbtSave.apply {
                this.isEnabled = isEnabled
                alpha = if(isEnabled) 1f else 0.5f
            }
        }
        binding.apply {
            tietMessage.addTextChangedListener(textWatcher)
            tietDateTime.addTextChangedListener(textWatcher)
        }
    }

    private fun showAddForm(){
        binding.apply {
            pb.isVisible = false
            mbtDelete.isVisible = false
            mbtSave.setOnClickListener { submitReminder() }
        }
    }

    private fun submitReminder(reminder: Reminder ?= null){
        val dateTime = binding.tietDateTime.text.toString()
        val message = binding.tietMessage.text.toString().trim()
        val dateTimeMills = DateTimeUtil.toDate(dateTime).time
        val currentTimeMills = System.currentTimeMillis()
        if(dateTimeMills <= currentTimeMills){
            dialogUtil.showError(
                getString(R.string.invalid_date_time_message,
                DateTimeUtil.format(dateTimeMills),
                DateTimeUtil.format(currentTimeMills))
            )
            return
        }
        val newReminder = Reminder(reminder?.id ?: 0L,message,dateTimeMills,reminder?.workRequestId ?: "")
        val action = if(isFormAdd) Action.ADD else Action.EDIT
        if(NotificationPermissionUtil.ensure(this)){
            reminderViewModel.manage(action,newReminder)
        }
    }

    private fun observeManageReminder(){
        reminderViewModel.manage.observe(this) { result ->
            showLoading(true)
            result.onSuccess { message ->
                showMessage(message)
            }.onFailure { e ->
                showLoading(false)
                dialogUtil.showError(e.message)
            }
        }
    }

    private fun observeReminderById(){
        if(isFormAdd) return
        reminderViewModel.reminderById.observe(this) { result ->
            showLoading(true)
            result.onSuccess { reminder ->
                showLoading(false)
                fillForm(reminder)
                showEditForm(reminder)
            }.onFailure { e ->
                showMessage(e.message)
            }
        }
    }

    private fun showLoading(isVisible: Boolean){
        binding.apply {
            pb.isVisible = isVisible
            mbtSave.isEnabled = !isVisible
            div.alpha = if(isVisible) 0.5f else 1f
        }
    }

    private fun fillForm(reminder: Reminder){
        binding.apply {
            tietMessage.setText(reminder.message)
            tietDateTime.setText(DateTimeUtil.format(reminder.datetime))
        }
    }

    private fun showEditForm(reminder: Reminder){
        binding.apply {
            mbtSave.setOnClickListener { submitReminder(reminder) }
            mbtDelete.setOnClickListener {
                dialogUtil.showDelete {
                    reminderViewModel.manage(Action.DELETE,reminder)
                }
            }
        }
    }

    private fun showMessage(message: String?){
        Toast.makeText(applicationContext,message, Toast.LENGTH_SHORT).show()
        finish()
    }
}