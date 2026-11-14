package com.app.kickboxing.Activitys.presentation.Ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.kickboxing.R
import androidx.navigation.fragment.findNavController
import com.app.kickboxing.Activitys.domain.model.ResetPasswordRequest
import com.app.kickboxing.Activitys.presentation.Ui.Activities.AuthActivity
import com.app.kickboxing.Activitys.presentation.Ui.HomesActivity
import com.app.kickboxing.Activitys.presentation.Ui.ViewModel.LoginViewModel
import com.app.kickboxing.Activitys.presentation.state.Results
import com.app.kickboxing.Activitys.utils.AsteriskPasswordTransformation
import com.app.kickboxing.Activitys.utils.Prefs
import com.app.kickboxing.databinding.FragmentChangePasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {
    lateinit var mBinding: FragmentChangePasswordBinding
    private var authActivity: AuthActivity? = null
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false
    private var userId=""
    private val mViewModel by viewModels<LoginViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AuthActivity) {
            authActivity = context
        }else{
            authActivity = (activity as AuthActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentChangePasswordBinding.inflate(layoutInflater, container, false)
        userId= arguments?.getString("userId").toString()
        observer()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.apply {

            ivBackReset.setOnClickListener {
                findNavController().navigateUp()
            }
            setupTouchListenerToHideKeyboard(view)
            btChangePassword.setOnClickListener {

                 validateChangePassword()

            }

            mBinding.etPasswordChange.transformationMethod = AsteriskPasswordTransformation()
            mBinding.etPasswordConfirmPass.transformationMethod = AsteriskPasswordTransformation()

            mBinding.passwordToggle.setOnClickListener {
                isPasswordVisible = !isPasswordVisible
                if (isPasswordVisible) {
                    mBinding.etPasswordChange.transformationMethod = null // Show password as text
                    mBinding.passwordToggle.setImageResource(R.drawable.ic_eye) // Change icon to 'eye off'
                } else {
                    mBinding.etPasswordChange.transformationMethod = AsteriskPasswordTransformation() // Mask password with asterisks
                    mBinding.passwordToggle.setImageResource(R.drawable.ic_eye_off) // Change icon to 'eye'
                }
                mBinding.etPasswordChange.setSelection(mBinding.etPasswordChange.text?.length ?: 0) // Move the cursor to the end
            }

            mBinding.passwordToggleConfirmPass.setOnClickListener {
                isConfirmPasswordVisible = !isConfirmPasswordVisible
                if (isConfirmPasswordVisible) {
                    mBinding.etPasswordConfirmPass.transformationMethod = null // Show password as text
                    mBinding.passwordToggleConfirmPass.setImageResource(R.drawable.ic_eye) // Change icon to 'eye off'
                } else {
                    mBinding.etPasswordConfirmPass.transformationMethod = AsteriskPasswordTransformation() // Mask password with asterisks
                    mBinding.passwordToggleConfirmPass.setImageResource(R.drawable.ic_eye_off) // Change icon to 'eye'
                }
                mBinding.etPasswordConfirmPass.setSelection(mBinding.etPasswordConfirmPass.text?.length ?: 0) // Move the cursor to the end
            }

        }
    }

    private fun validateChangePassword() {
        val password = mBinding.etPasswordChange.text?.toString()?.trim()
        val confirmPassword = mBinding.etPasswordConfirmPass.text?.toString()?.trim()

        // Clear previous errors
        mBinding.etPasswordLayoutSignUp.error = null
        mBinding.etPasswordLayout.error = null

        if (password.isNullOrEmpty()) {
            authActivity?.showErrorAlert( "Please enter password.")
            return
        }

        if (!isValidPassword(password)) {
            authActivity?.showErrorAlert("Password must be at least 8 characters long with uppercase, lowercase, number, special character, and no spaces.")
            return
        }

        if (confirmPassword.isNullOrEmpty()) {
            authActivity?.showErrorAlert("Please enter confirm password.")
            return
        }

        if (password != confirmPassword) {
            authActivity?.showErrorAlert("Password and confirm password must be same.")
            return
        }

        mViewModel.userResetPassword(
            ResetPasswordRequest(
                user=userId,
                password=mBinding.etPasswordChange.text?.toString()?.trim()
            )
        )

    }
    private fun observer() {
        lifecycleScope.launchWhenStarted {
            mViewModel.userResetPasswordResponseData.collect { event ->
                event?.getContentIfNotHandled()?.let { result ->
                    when (result) {
                        is Results.Loading -> {
                        }

                        is Results.Success -> {
                            if (result.data.success) {
                                authActivity?.showSuccessAlert(result.data.message)
                                Handler().postDelayed(
                                    {
                                        val intent = Intent(requireActivity(), AuthActivity::class.java)
                                        startActivity(intent)
                                        requireActivity().finish()
                                    },
                                    1500
                                )

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

    fun isValidPassword(password: String): Boolean {
        val passwordPattern =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=!])(?=\\S+\$).{8,}$"
        return Regex(passwordPattern).matches(password)
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus ?: View(requireContext())
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}