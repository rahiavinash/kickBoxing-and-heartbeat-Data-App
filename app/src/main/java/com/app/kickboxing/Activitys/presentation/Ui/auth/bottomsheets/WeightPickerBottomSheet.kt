package com.app.kickboxing.Activitys.presentation.Ui.auth.bottomsheets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.kickboxing.R
import com.app.kickboxing.databinding.FragmentWeightPickerBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class WeightPickerBottomSheet(
   var clickListener: WeightPickerListener
) : BottomSheetDialogFragment() {

    private lateinit var mBinding: FragmentWeightPickerBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentWeightPickerBottomSheetBinding.inflate(inflater, container, false)

        setupWeightPicker()
        setupListeners()

        return mBinding.root
    }

    private fun setupWeightPicker() {
        mBinding.weightPicker.minValue = 30
        mBinding.weightPicker.maxValue = 400
        mBinding.weightPicker.wrapSelectorWheel = false
    }

    private fun setupListeners() {
        mBinding.btDone.setOnClickListener {
            val selectedWeight = mBinding.weightPicker.value
            val result = "$selectedWeight"
            clickListener.onWeightSelected(result)
            dismiss()
        }
    }

    interface WeightPickerListener {
        fun onWeightSelected(weight: String)
    }
}