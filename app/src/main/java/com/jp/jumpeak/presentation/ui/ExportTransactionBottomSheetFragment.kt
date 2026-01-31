package com.jp.jumpeak.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.jp.jumpeak.R
import com.jp.jumpeak.databinding.FragmentExportTransactionBottomSheetBinding
import com.jp.jumpeak.presentation.viewmodel.TransactionViewModel
import com.jp.jumpeak.util.classes.DialogUtil
import com.jp.jumpeak.util.objects.StoragePermissionUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExportTransactionBottomSheetFragment : BottomSheetDialogFragment() {
    private val dialogUtil by lazy { DialogUtil(requireContext()) }
    private val transactionViewModel: TransactionViewModel by viewModels()
    private var _binding: FragmentExportTransactionBottomSheetBinding ?= null
    private val binding get() = _binding!!

    companion object{
        private const val PARTIES_ID_KEY = "parties_id"
        private const val PARTIES_NAME_KEY = "parties_name"
        fun newInstance(partiesId: String,partiesName: String): ExportTransactionBottomSheetFragment{
            return ExportTransactionBottomSheetFragment().apply {
                arguments = bundleOf(
                    PARTIES_ID_KEY to partiesId,
                    PARTIES_NAME_KEY to partiesName
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExportTransactionBottomSheetBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeExportTransactionToExcel()
    }

    private fun initView(){
        binding.apply {
            tvExportSettled.setOnClickListener {
                if(StoragePermissionUtil.ensure(requireActivity())){
                    exportToExcel(true)
                }
            }
            tvExportUnsettle.setOnClickListener {
                if(StoragePermissionUtil.ensure(requireActivity())){
                    exportToExcel(false)
                }
            }
        }
    }

    private fun exportToExcel(isSettled: Boolean){
        val partiesId = arguments?.getString(PARTIES_ID_KEY) ?: ""
        val partiesName = arguments?.getString(PARTIES_NAME_KEY) ?: ""
        if(StoragePermissionUtil.ensure(requireActivity())){
            transactionViewModel.exportToExcel(partiesId,partiesName,isSettled)
        }
    }

    private fun observeExportTransactionToExcel(){
        transactionViewModel.export.observe(viewLifecycleOwner) { result ->
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