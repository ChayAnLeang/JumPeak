package com.jp.jumpeak.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.jp.jumpeak.R
import com.jp.jumpeak.databinding.FragmentMoreBottomSheetBinding
import com.jp.jumpeak.enums.PartiesType
import com.jp.jumpeak.presentation.viewmodel.PartiesViewModel
import com.jp.jumpeak.util.classes.DialogUtil
import com.jp.jumpeak.util.classes.NavigationUtil
import com.jp.jumpeak.util.objects.StoragePermissionUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoreBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentMoreBottomSheetBinding ?= null
    private val binding get() = _binding!!
    private val partiesViewModel: PartiesViewModel by viewModels()
    private val dialogUtil by lazy { DialogUtil(requireContext()) }
    private val navigationUtil by lazy { NavigationUtil(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMoreBottomSheetBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeExportPartiesToExcel()
    }

    private fun initView(){
        binding.apply {
            tvTheme.setOnClickListener { navigationUtil.navigateTo(ThemeActivity::class.java) }
            tvLanguage.setOnClickListener { navigationUtil.navigateTo(LanguageActivity::class.java) }
            tvExportOweMe.setOnClickListener { exportToExcel(PartiesType.OWE_ME) }
            tvExportIOwe.setOnClickListener { exportToExcel(PartiesType.I_OWE) }
        }
    }

    private fun exportToExcel(partiesType: PartiesType){
        if(StoragePermissionUtil.ensure(requireActivity())){
            partiesViewModel.exportToExcel(partiesType)
        }
    }

    private fun observeExportPartiesToExcel(){
        partiesViewModel.export.observe(viewLifecycleOwner) { result ->
            showLoading(true)
            result.onSuccess { message ->
                Toast.makeText(requireContext(),message, Toast.LENGTH_SHORT).show()
                dismiss()
            }.onFailure { e ->
                showFailure(e.message)
            }
        }
    }

    private fun showLoading(isVisible: Boolean){
        binding.apply {
            pb.isVisible = isVisible
            div.alpha = if(isVisible) 0.5f else 1f
        }
    }

    private fun showFailure(message: String?){
        showLoading(false)
        if(message?.contains("No data yet") == true){
            dialogUtil.showError(getString(R.string.no_data_available))
            return
        }
        dialogUtil.showError(message)
    }
}