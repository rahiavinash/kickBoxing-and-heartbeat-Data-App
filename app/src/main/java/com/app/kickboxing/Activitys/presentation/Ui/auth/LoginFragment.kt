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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.app.kickboxing.Activitys.domain.model.LoginRequest
import com.app.kickboxing.Activitys.presentation.Ui.Activities.AuthActivity
import com.app.kickboxing.Activitys.presentation.Ui.HomesActivity
import com.app.kickboxing.Activitys.presentation.Ui.ViewModel.LoginViewModel
import com.app.kickboxing.Activitys.presentation.state.Results
import com.app.kickboxing.Activitys.utils.Prefs
import com.app.kickboxing.Activitys.utils.AsteriskPasswordTransformation
import com.app.kickboxing.Activitys.utils.MyUtils.isInternetAvailable
import com.app.kickboxing.R
import com.app.kickboxing.databinding.LoginFragmentBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    lateinit var mBinding: LoginFragmentBinding
    private var isPasswordVisible = false
    private var authActivity: AuthActivity? = null
    lateinit var firebaseInstance: FirebaseMessaging
    //   val newViewModel = hiltViewModel<LoginViewModel>()
    //private val loginViewModel: LoginViewModel by viewModels()
    private val loginViewModel by viewModels<LoginViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = LoginFragmentBinding.inflate(layoutInflater, container, false)
        loginObserver()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isInternetAvailable(requireContext())) {
            firebaseInstance = FirebaseMessaging.getInstance()
            getDeviceToken()
        }

        setupTouchListenerToHideKeyboard(view)

        val text = getString(R.string.sign_up) // Example: "Don't have an account? Sign up"
        val spannable = SpannableString(text)

        val keyword = "Sign Up"
        val start = text.indexOf(keyword)
        val end = start + keyword.length

// Bold
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

//  Text Color
        spannable.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    com.hb.dialog.R.color.dialog_white
                )
            ), // replace with your color
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

// Text Size (in SP, scaled properly)
        spannable.setSpan(
            AbsoluteSizeSpan(16, true), // 16sp, `true` = sp unit
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

//  Set to TextView
        mBinding.tvSignUp.text = spannable



        mBinding.etEmail.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)



        mBinding.etPassword.transformationMethod = AsteriskPasswordTransformation()
        mBinding.passwordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                mBinding.etPassword.transformationMethod = null // Show password as text
                mBinding.passwordToggle.setImageResource(R.drawable.ic_eye) // Change icon to 'eye off'
            } else {
                mBinding.etPassword.transformationMethod =
                    AsteriskPasswordTransformation() // Mask password with asterisks
                mBinding.passwordToggle.setImageResource(R.drawable.ic_eye_off) // Change icon to 'eye'
            }
            mBinding.etPassword.setSelection(
                mBinding.etPassword.text?.length ?: 0
            ) // Move the cursor to the end
        }

        mBinding.btLogin.setOnClickListener {
            validateAndLogin()
        }

        mBinding.tvForgotPassword.setOnClickListener {
            clearPasswordField()
            findNavController().navigate(R.id.action_loginFragment_to_forgotPassFragment)
        }
        mBinding.tvSignUp.setOnClickListener {
            clearPasswordField()
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }


    }



    private fun validateAndLogin() {
        val email = mBinding.etEmail?.text?.toString()?.trim()
        val password = mBinding.etPassword.text?.toString()?.trim()

        if (email.isNullOrEmpty()) {
            (activity as AuthActivity).showErrorAlert("Please enter email address.")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            (activity as AuthActivity).showErrorAlert("Please enter a valid email address")
            return
        }

        if (password.isNullOrEmpty()) {
            (activity as AuthActivity).showErrorAlert("Please enter your password")
            return
        }


        if (!isInternetAvailable(requireContext())) {
            (activity as AuthActivity).showErrorAlert("Please check your internet connection.")
            return
        }

        // Hit API
        val loginRequest = LoginRequest(
            email = email,
            password = password,
            deviceType = "Android",
            deviceToken = Prefs.init().deviceToken,
            fcmToken=Prefs.init().deviceToken
        )
        loginViewModel.userLogIn(loginRequest)





    }


    fun loginObserver() {
        lifecycleScope.launchWhenStarted {
            loginViewModel.loginResponseData.collect { event ->
                event?.getContentIfNotHandled()?.let { result ->
                    when (result) {
                        is Results.Loading -> {
                            Log.d("LoginObserver", "Loading...")
                        }

                        is Results.Success -> {
                            if (result.data.success == true) {
                                Prefs.init().accessToken = result.data.data?.accessToken.toString()
                                if (result.data.data?.completeProfile == true){
                                    val intent = Intent(requireActivity(), HomesActivity::class.java)
                                    startActivity(intent)
                                    requireActivity().finish()
                                    Prefs.init().isProfileCompleted = true
                                }else{
                                    Prefs.init().isProfileCompleted = false
                                    findNavController().navigate(R.id.createProfileFragment)
                                }

                            } else {
                                result.data.message?.let {
                                    (activity as AuthActivity).showErrorAlert(it)
                                }
                                Log.e("LoginObserver", "API Error: ${result.data.message}")
                            }
                        }

                        is Results.Failure -> {
                            Log.e("LoginObserver", "Failure: ${result.message} (Code: ${result.code})")
                            // Show error message in UI (e.g., Toast)
                            (activity as AuthActivity).showErrorAlert(result.message)
                        }

                        null -> {
                            Log.d("LoginObserver", "No result")
                        }
                    }

                }
            }
        }

    }



    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
    private fun clearPasswordField(){
        mBinding.etEmail.setText("")
        mBinding.etPassword.setText("")
    }

    private fun getDeviceToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener { task ->

                val token = task.result
                Prefs.init().deviceToken = token
                Log.e("35345435435", token)
            })

    }
}


