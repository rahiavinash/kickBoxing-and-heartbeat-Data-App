package com.app.kickboxing.Activitys.presentation.Ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.kickboxing.Activitys.domain.model.LoginRequest
import com.app.kickboxing.Activitys.domain.model.RegisterRequest
import com.app.kickboxing.Activitys.presentation.Ui.Activities.AuthActivity
import com.app.kickboxing.Activitys.presentation.Ui.HomesActivity
import com.app.kickboxing.Activitys.presentation.Ui.ViewModel.LoginViewModel
import com.app.kickboxing.Activitys.presentation.state.Results
import com.app.kickboxing.Activitys.utils.AsteriskPasswordTransformation
import com.app.kickboxing.Activitys.utils.MyUtils.isInternetAvailable
import com.app.kickboxing.Activitys.utils.Prefs
import com.app.kickboxing.R
import com.app.kickboxing.databinding.FragmentSignupBinding
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupFragment : Fragment() {

    private lateinit var mBinding: FragmentSignupBinding
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false
    private val loginViewModel by viewModels<LoginViewModel>()
    private var authActivity: AuthActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AuthActivity) {
            authActivity = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSignupBinding.inflate(layoutInflater, container, false)
        loginObserver()
        mBinding.ivBack.setOnClickListener {
            clearPasswordField()
            findNavController().navigateUp()
        }

        mBinding.tvSignIn.setOnClickListener {
            clearPasswordField()
            findNavController().navigateUp()
        }
        mBinding.etEmail.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)

        mBinding.etPasswordSignUp.transformationMethod = AsteriskPasswordTransformation()
        mBinding.etPasswordConfirmPass.transformationMethod = AsteriskPasswordTransformation()

        mBinding.passwordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                mBinding.etPasswordSignUp.transformationMethod = null // Show password as text
                mBinding.passwordToggle.setImageResource(R.drawable.ic_eye) // Change icon to 'eye off'
            } else {
                mBinding.etPasswordSignUp.transformationMethod =
                    AsteriskPasswordTransformation() // Mask password with asterisks
                mBinding.passwordToggle.setImageResource(R.drawable.ic_eye_off) // Change icon to 'eye'
            }
            mBinding.etPasswordSignUp.setSelection(
                mBinding.etPasswordSignUp.text?.length ?: 0
            ) // Move the cursor to the end
        }

        mBinding.passwordToggleConfirmPass.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            if (isConfirmPasswordVisible) {
                mBinding.etPasswordConfirmPass.transformationMethod = null // Show password as text
                mBinding.passwordToggleConfirmPass.setImageResource(R.drawable.ic_eye) // Change icon to 'eye off'
            } else {
                mBinding.etPasswordConfirmPass.transformationMethod =
                    AsteriskPasswordTransformation() // Mask password with asterisks
                mBinding.passwordToggleConfirmPass.setImageResource(R.drawable.ic_eye_off) // Change icon to 'eye'
            }
            mBinding.etPasswordConfirmPass.setSelection(
                mBinding.etPasswordConfirmPass.text?.length ?: 0
            ) // Move the cursor to the end
        }


        mBinding.btSubmit.setOnClickListener {
            validateAndLogin()
        }


        setupStyledText()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTouchListenerToHideKeyboard(view)


    }
    private fun validateAndLogin() {
        val email = mBinding.etEmail?.text?.toString()?.trim()
        val password = mBinding.etPasswordSignUp.text?.toString()?.trim()
        val confirmPassword = mBinding.etPasswordConfirmPass.text?.toString()?.trim()

        if (email.isNullOrEmpty()) {
            authActivity?.showErrorAlert("Please enter your email address.")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            authActivity?.showErrorAlert("Please enter a valid email address.")
            return
        }

        if (password.isNullOrEmpty()) {
            authActivity?.showErrorAlert("Please enter your password.")
            return
        }

        if (!isValidPassword(password)){
            authActivity?.showErrorAlert("Password must be at least 8 characters long with uppercase, lowercase, number, special character, and no spaces.")
            return
        }

            if (confirmPassword.isNullOrEmpty()) {
            authActivity?.showErrorAlert("Please enter confirm password.")
            return
        }

        if (confirmPassword != password) {
            authActivity?.showErrorAlert("Password and confirm password must be same.")
            return
        }



        if (!isInternetAvailable(requireContext())) {
            authActivity?.showErrorAlert("Please check your internet connection.")
            return
        }

        // Proceed with registration
        val request = RegisterRequest(
            email = email,
            password = password
        )
        loginViewModel.userRegister(request)
    }

    private fun loginObserver() {
        lifecycleScope.launchWhenStarted {
            loginViewModel.userRegisterData.collect { event ->
                event?.getContentIfNotHandled()?.let { result ->
                    when (result) {
                        is Results.Loading -> {
                        }

                        is Results.Success -> {
                            if (result.data.success) {
                                Prefs.init().accessToken = result.data.data?.accessToken.toString()
                                findNavController().navigate(R.id.createProfileFragment)
                                clearPasswordField()

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

    private fun setupStyledText() {
        val fullText = getString(R.string.sign_in)
        val spannable = SpannableString(fullText)

        val keyword = "Sign In"
        val start = fullText.indexOf(keyword)

        if (start != -1) {
            val end = start + keyword.length

            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), com.hb.dialog.R.color.dialog_white)),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable.setSpan(
                AbsoluteSizeSpan(16, true),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            Log.e("SignupFragment", "\"$keyword\" not found in: \"$fullText\"")
        }

        mBinding.tvSignIn.text = spannable
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

    fun isValidPassword(password: String): Boolean {
        val passwordPattern =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=!])(?=\\S+\$).{8,}$"
        return Regex(passwordPattern).matches(password)
    }

    private fun clearPasswordField(){
        mBinding.etEmail.setText("")
        mBinding.etPasswordSignUp.setText("")
        mBinding.etPasswordConfirmPass.setText("")
    }
}
