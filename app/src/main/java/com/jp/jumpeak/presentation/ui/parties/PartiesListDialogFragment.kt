package com.jp.jumpeak.presentation.ui.parties

import androidx.fragment.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import com.jp.jumpeak.R
import com.jp.jumpeak.databinding.FragmentPartiesListDialogBinding
import com.jp.jumpeak.enums.PartiesType
import com.jp.jumpeak.presentation.ui.parties.activity.AddPartiesActivity
import com.jp.jumpeak.presentation.viewmodel.PartiesViewModel
import com.jp.jumpeak.util.classes.NavigationUtil
import com.jp.jumpeak.util.objects.TabLayoutUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PartiesListDialogFragment : DialogFragment() {
    private var _binding: FragmentPartiesListDialogBinding ?= null
    private val binding get() = _binding!!
    private val partiesViewModel: PartiesViewModel by viewModels()
    private val navigationUtil by lazy { NavigationUtil(requireContext()) }
    private val partiesAdapter by lazy {
        PartiesAdapter { partiesDTO,_ ->
            val parties = partiesDTO.parties
            parentFragmentManager.setFragmentResult(
                "parties",
                bundleOf(
                    "parties_id" to parties.id,
                    "parties_name" to parties.name,
                    "balance" to partiesDTO.balance
                )
            )
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPartiesListDialogBinding.inflate(inflater,container,false)
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
        observePartiesByPartiesType()
    }

    private fun initView(){
        handleTabLayout()
        binding.apply {
            rv.adapter = partiesAdapter
            mtb.setOnMenuItemClickListener { items ->
                if(items.itemId == R.id.close){
                    dismiss()
                }
                true
            }
            tvAddPersonWhoOweMe.setOnClickListener {
                navigateToAddPartiesActivity(getString(R.string.add_person_who_owe_me))
            }
            tvAddPersonIOwe.setOnClickListener {
                navigateToAddPartiesActivity(getString(R.string.add_person_i_owe))
            }
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

    private fun observePartiesByPartiesType(){
        partiesViewModel.partiesByPartiesType.observe(viewLifecycleOwner) { parties ->
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