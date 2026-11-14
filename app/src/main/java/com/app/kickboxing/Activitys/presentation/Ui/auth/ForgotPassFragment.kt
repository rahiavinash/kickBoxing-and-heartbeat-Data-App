package com.app.kickboxing.Activitys.presentation.Ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.kickboxing.Activitys.domain.model.RegisterRequest
import com.app.kickboxing.Activitys.domain.model.SendOTPRequest
import com.app.kickboxing.Activitys.presentation.Ui.Activities.AuthActivity
import com.app.kickboxing.Activitys.presentation.Ui.HomesActivity
import com.app.kickboxing.Activitys.presentation.Ui.ViewModel.LoginViewModel
import com.app.kickboxing.Activitys.presentation.state.Results
import com.app.kickboxing.Activitys.utils.MyUtils
import com.app.kickboxing.Activitys.utils.Prefs
import com.app.kickboxing.R
import com.app.kickboxing.databinding.FragmentForgotPassBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPassFragment : Fragment() {
    lateinit var mBinding: FragmentForgotPassBinding
    private val mViewModel by viewModels<LoginViewModel>()
    private var authActivity: AuthActivity? = null
    var email =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AuthActivity) {
            authActivity = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentForgotPassBinding.inflate(layoutInflater, container, false)
        observer()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTouchListenerToHideKeyboard(view)
        mBinding.apply {

            ivBack.setOnClickListener{
                findNavController().navigateUp()
            }

            btSendOtp.setOnClickListener {
                Prefs.init().accessToken=""
                validateAndForgetPassword()

            }
            mBinding.etEmail.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)


        }

    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus ?: View(requireContext())
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchListenerToHideKeyboard(view: View) {
        if (view !is EditText) {
            view.setOnTouchListener { _, _ ->
                hideKeyboard()
                view.clearFocus()
                false
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                setupTouchListenerToHideKeyboard(view.getChildAt(i))
            }
        }
    }

    private fun observer() {
        lifecycleScope.launchWhenStarted {
            mViewModel.sendOtTPResponseData.collect { event ->
                event?.getContentIfNotHandled()?.let { result ->
                    when (result) {
                        is Results.Loading -> {
                        }

                        is Results.Success -> {
                            if (result.data.success) {
                                authActivity?.showSuccessAlert(result.data.message)
                                //      Prefs.init().accessToken = result.data.data?.accessToken.toString()
                                val bundle = Bundle()
                                bundle.putString("email", email)
                                findNavController().navigate(R.id.otpFragment, bundle)
                            } else {
                                authActivity?.showErrorAlert(result.data.message ?: "Unknown error")
                            }
                        }

                        is Results.Failure -> {
                            authActivity?.showErrorAlert(result.message)
                        }
                    }
                }
            }
        }

    }

    private fun validateAndForgetPassword() {
         email = mBinding.etEmail?.text?.toString()?.trim().toString()

        if (email.isNullOrEmpty()) {
            authActivity?.showErrorAlert("Please enter your email address.")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            authActivity?.showErrorAlert("Please enter a valid email address.")
            return
        }


        if (!MyUtils.isInternetAvailable(requireContext())) {
            authActivity?.showErrorAlert("Please check your internet connection.")
            return
        }

        // Proceed with registration
        val request = SendOTPRequest(
            email = email,
        )

        mViewModel.sendOtTP(request)
    }
}