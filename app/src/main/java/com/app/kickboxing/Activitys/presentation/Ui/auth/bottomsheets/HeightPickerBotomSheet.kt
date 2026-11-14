package com.app.kickboxing.Activitys.presentation.Ui.auth.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.kickboxing.databinding.FragmentHeightPickerBotomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class HeightPickerBottomSheet(
    var clickListener: HeightPickerListener
) : BottomSheetDialogFragment() {
    private lateinit var mBinding: FragmentHeightPickerBotomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentHeightPickerBotomSheetBinding.inflate(inflater, container, false)

        setupNumberPickers()
        setupListeners()

        return mBinding.root
    }

    private fun setupNumberPickers() {
        mBinding.feetPicker.minValue = 1
        mBinding.feetPicker.maxValue = 8

        mBinding.inchPicker.minValue = 0
        mBinding.inchPicker.maxValue = 11
    }

    private fun setupListeners() {
        mBinding.btDone.setOnClickListener {
            val selectedFeet = mBinding.feetPicker.value
            val selectedInches = mBinding.inchPicker.value
            val result = "${selectedFeet}ft ${selectedInches}in"
            clickListener.onHeightSelected(feet = selectedFeet.toString(), inches = selectedInches.toString())
            dismiss()
        }
    }

    interface HeightPickerListener {
        fun onHeightSelected(feet: String, inches: String)
    }
}
