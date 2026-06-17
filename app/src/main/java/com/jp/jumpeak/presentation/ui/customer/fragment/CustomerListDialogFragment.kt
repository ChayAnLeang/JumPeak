package com.jp.jumpeak.presentation.ui.customer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import com.jp.jumpeak.R
import com.jp.jumpeak.databinding.FragmentCustomerListDialogBinding
import com.jp.jumpeak.presentation.ui.customer.CustomerAdapter
import com.jp.jumpeak.presentation.ui.customer.ManageCustomerActivity
import com.jp.jumpeak.presentation.viewmodel.CustomerViewModel
import com.jp.jumpeak.util.classes.NavigationUtil
import com.jp.jumpeak.util.objects.TextWatcherUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CustomerListDialogFragment : DialogFragment() {
    private var searchJob: Job ?= null
    private var _binding: FragmentCustomerListDialogBinding?= null
    private val binding get() = _binding!!
    private val customerViewModel: CustomerViewModel by viewModels()
    private val navigationUtil by lazy { NavigationUtil(requireContext()) }
    private val customerAdapter by lazy {
        CustomerAdapter{ customerId,fullName ->
            parentFragmentManager.setFragmentResult(
                CUSTOMER_RESULT,
                bundleOf(
                    FULL_NAME_KEY to fullName,
                    CUSTOMER_ID_KEY to customerId
                )
            )
            dismiss()
        }
    }

    companion object{
        private const val FULL_NAME_KEY = "full_name"
        private const val CUSTOMER_ID_KEY = "customer_id"
        private const val CUSTOMER_RESULT = "customer_result"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerListDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeCustomersByName()
    }

    private fun initView(){
        setupToolbar()
        setupSearchCustomerByName()
        binding.apply {
            rv.adapter = customerAdapter
            btAddCustomer.setOnClickListener { navigationUtil.navigateTo(ManageCustomerActivity::class.java) }
        }
    }

    private fun setupToolbar(){
        binding.mtb.setOnMenuItemClickListener { items ->
            when(items.itemId){
                R.id.close -> dismiss()
            }
            true
        }
    }

    private fun setupSearchCustomerByName(){
        val textWatcher = TextWatcherUtil.setup {
            searchJob?.cancel()
            searchJob = MainScope().launch {
                delay(300)
                val name = binding.etSearch.text.toString().trim()
                customerViewModel.setName(name)
            }
        }
        binding.etSearch.addTextChangedListener(textWatcher)
    }

    private fun observeCustomersByName() {
        customerViewModel.customersByName.observe(viewLifecycleOwner) { pagingData ->
            customerAdapter.submitData(lifecycle, pagingData)
        }
        customerAdapter.addLoadStateListener { loadState ->
            when (loadState.refresh) {
                is LoadState.Loading -> binding.pb.isVisible = true
                is LoadState.NotLoading -> {
                    if (customerAdapter.itemCount == 0) showData(false)
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
            icGroups.isVisible = !isVisible
            tvNoData.isVisible = !isVisible
        }
    }
}