package com.jp.jumpeak.presentation.ui.customer.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import com.jp.jumpeak.databinding.FragmentCustomerBinding
import com.jp.jumpeak.presentation.ui.customer.CustomerAdapter
import com.jp.jumpeak.presentation.ui.customer.ManageCustomerActivity
import com.jp.jumpeak.presentation.viewmodel.CustomerNameShareViewModel
import com.jp.jumpeak.presentation.viewmodel.CustomerViewModel
import com.jp.jumpeak.util.classes.NavigationUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomerFragment : Fragment() {
    private var _binding: FragmentCustomerBinding ?= null
    private val binding get() = _binding!!
    private val customerViewModel: CustomerViewModel by viewModels()
    private val navigationUtil by lazy { NavigationUtil(requireContext()) }
    private val customerNameShareViewModel: CustomerNameShareViewModel by activityViewModels()
    private val customerAdapter by lazy {
        CustomerAdapter{ customerId,_ ->
            navigationUtil.navigateTo(
                ManageCustomerActivity::class.java,
                bundleOf(
                    FORM_ADD_KEY to false,
                    CUSTOMER_ID_KEY to customerId
                )
            )
        }
    }

    companion object{
        private const val FORM_ADD_KEY = "form_add"
        private const val CUSTOMER_ID_KEY = "customer_id"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeCustomerName()
        observeCustomersByName()
    }

    private fun initView(){
        binding.apply {
            rv.adapter = customerAdapter
            btAddCustomer.setOnClickListener {
                navigationUtil.navigateTo(ManageCustomerActivity::class.java)
            }
        }
    }

    private fun observeCustomerName(){
        customerNameShareViewModel.name.observe(viewLifecycleOwner) { name ->
            customerViewModel.setName(name)
        }
    }

    private fun observeCustomersByName(){
        customerViewModel.customersByName.observe(viewLifecycleOwner) { pagingData ->
            customerAdapter.submitData(lifecycle,pagingData)
        }
        customerAdapter.addLoadStateListener { loadState ->
            when(loadState.refresh){
                is LoadState.Loading -> binding.pb.isVisible = true
                is LoadState.NotLoading -> {
                    if(customerAdapter.itemCount == 0) showData(false)
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