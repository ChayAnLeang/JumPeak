package com.jp.jumpeak.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jp.jumpeak.databinding.FragmentThemeBottomSheetBinding
import com.jp.jumpeak.presentation.ui.main.MainActivity
import com.jp.jumpeak.util.classes.ThemeUtil
import com.jp.jumpeak.util.objects.RestartAppUtil

class ThemeBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentThemeBottomSheetBinding?= null
    private val binding get() = _binding!!
    private val themeUtil by lazy { ThemeUtil(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThemeBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDefaultTheme()
        initView()
    }

    private fun initDefaultTheme(){
        val mode = themeUtil.getMode()
        binding.apply {
            icDarkCheck.isVisible = mode == AppCompatDelegate.MODE_NIGHT_YES
            icLightCheck.isVisible = mode == AppCompatDelegate.MODE_NIGHT_NO
            icFollowSystemCheck.isVisible = mode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    }
    private fun initView(){
        binding.apply {
            llDark.setOnClickListener{ switchTheme(AppCompatDelegate.MODE_NIGHT_YES) }
            llLight.setOnClickListener { switchTheme(AppCompatDelegate.MODE_NIGHT_NO) }
            llFollowSystem.setOnClickListener { switchTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) }
        }
    }

    private fun switchTheme(mode:Int){
        themeUtil.setTheme(mode)
        RestartAppUtil.restart(requireContext(), MainActivity::class.java)
    }
}