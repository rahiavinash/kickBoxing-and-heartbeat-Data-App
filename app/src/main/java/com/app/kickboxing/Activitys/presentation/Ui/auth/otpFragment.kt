package com.app.kickboxing.Activitys.presentation.Ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.kickboxing.Activitys.domain.model.SendOTPRequest
import com.app.kickboxing.Activitys.domain.model.VerifyOtpRequest
import com.app.kickboxing.Activitys.presentation.Ui.Activities.AuthActivity
import com.app.kickboxing.Activitys.presentation.Ui.ViewModel.LoginViewModel
import com.app.kickboxing.Activitys.presentation.state.Results
import com.app.kickboxing.Activitys.utils.MyUtils
import com.app.kickboxing.R

import com.app.kickboxing.databinding.FragmentOtpBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class otpFragment : Fragment() {
    lateinit var mBinding: FragmentOtpBinding
    private val mViewModel by viewModels<LoginViewModel>()
    var otp =""
    var email =""
    private var resendTimer: CountDownTimer? = null
    private var timeRemaining: Long = 0
    private var timerRunning = false

    companion object {
        private const val TIMER_DURATION = 30000L
        private const val TIMER_INTERVAL = 1000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            timeRemaining = it.getLong("time_remaining", TIMER_DURATION)
            timerRunning = it.getBoolean("timer_running", false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentOtpBinding.inflate(layoutInflater, container, false)
        observer()
        email = arguments?.getString("email")?:""
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            startResendTimer(TIMER_DURATION)
        } else if (timerRunning && timeRemaining > 0) {
            startResendTimer(timeRemaining)
        } else {
            updateResendUI(true)
        }
        setUpEventListener()
        setupTouchListenerToHideKeyboard(view)
    }

    private fun setUpEventListener(){
        mBinding.ivBack.setOnClickListener{
            findNavController().navigateUp()
        }
        mBinding.btVerifyOtp.setOnClickListener {
            if ( validateOtp()){
                if (!MyUtils.isInternetAvailable(requireContext())) {
                    (activity as AuthActivity).showErrorAlert("Please check your internet connection.")
                }else{
                    mViewModel.verifyOtTP(VerifyOtpRequest(
                        email = arguments?.getString("email"),
                        otp=otp.toInt()
                    ))
                }
            }
        }
        setupResendClickListener()
    }

    private fun startResendTimer() {
        resendTimer?.cancel()

        resendTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mBinding.tvResendOtp.text = "Resend OTP in ${formatTime(millisUntilFinished)}"
                mBinding.tvResendOtp.isClickable = false
                mBinding.tvResendOtp.setTextColor(ContextCompat.getColor(requireContext(), com.hb.dialog.R.color.dialog_white)) // Disabled color
            }

            override fun onFinish() {
                mBinding.tvResendOtp.text = getString(R.string.resend_otp)
                mBinding.tvResendOtp.isClickable = true
                mBinding.tvResendOtp.setTextColor(ContextCompat.getColor(requireContext(), com.hb.dialog.R.color.dialog_white))
            }
        }.start()
    }

    private fun startResendTimer(duration: Long = TIMER_DURATION) {
        resendTimer?.cancel()
        timerRunning = true
        timeRemaining = duration

        resendTimer = object : CountDownTimer(duration, TIMER_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                mBinding.tvResendOtp.text = "Resend OTP in ${formatTime(millisUntilFinished)}"
                mBinding.tvResendOtp.isClickable = false
                mBinding.tvResendOtp.setTextColor(ContextCompat.getColor(requireContext(), com.hb.dialog.R.color.dialog_white))
            }

            override fun onFinish() {
                timerRunning = false
                updateResendUI(true)
            }
        }.start()
    }

    private fun updateResendUI(enabled: Boolean) {
        mBinding.tvResendOtp.text = getString(R.string.resend_otp)
        mBinding.tvResendOtp.isClickable = enabled
        val colorRes = if (enabled) com.hb.dialog.R.color.dialog_white else com.hb.dialog.R.color.dialog_white
        mBinding.tvResendOtp.setTextColor(ContextCompat.getColor(requireContext(), colorRes))
    }

    private fun setupResendClickListener() {
        mBinding.tvResendOtp.setOnClickListener {
            if (!timerRunning) {
                mBinding.tvResendOtp.isClickable = false
                mBinding.tvResendOtp.text = "Sending..."
                val request = SendOTPRequest(email = email)
                mViewModel.sendOtTP(request)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("time_remaining", timeRemaining)
        outState.putBoolean("timer_running", timerRunning)
    }

    private fun formatTime(millis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        return String.format("%02d:%02d", minutes, seconds)
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


    private fun validateOtp(): Boolean {
        otp = mBinding.pinview.text?.toString()?.trim() ?: ""

        return when {
            otp.isEmpty() -> {
                (activity as AuthActivity).showErrorAlert("Please enter the OTP")
                false
            }
            otp.length != 4 -> {
                (activity as AuthActivity).showErrorAlert("Please enter valid OTP.")
                false
            }
            !otp.all { it.isDigit() } -> {
                (activity as AuthActivity).showErrorAlert("OTP should contain only digits")
                false
            }

            else -> true
        }
    }

    private fun clearOTPField(){
        mBinding.pinview.setText("")
    }

    private fun observer() {
        lifecycleScope.launchWhenStarted {
            mViewModel.verifyOtTPResponseData.collect { event ->
                event?.getContentIfNotHandled()?.let { result ->
                    when (result) {
                        is Results.Loading -> {
                        }

                        is Results.Success -> {
                            if (result.data.success) {
                                val bundle =Bundle()
                                bundle.putString("userId" , result.data.data.user)
                                findNavController().navigate(R.id.changePasswordFragment , bundle)
                            } else {
                                (activity as AuthActivity).showErrorAlert(result.data.message)
                            }
                        }

                        is Results.Failure -> {
                            (activity as AuthActivity).showErrorAlert(result.message)
                        }
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            mViewModel.sendOtTPResponseData.collect { event ->
                event?.getContentIfNotHandled()?.let { result ->
                    when (result) {
                        is Results.Loading -> {
                        }

                        is Results.Success -> {
                            if (result.data.success) {
                                (activity as AuthActivity).showSuccessAlert(result.data.message)
                                startResendTimer()
                            } else {
                                mBinding.tvResendOtp.isEnabled = true
                                (activity as AuthActivity).showErrorAlert(result.data.message ?: "Unknown error")
                            }
                        }

                        is Results.Failure -> {
                            mBinding.tvResendOtp.isEnabled = true
                            (activity as AuthActivity).showErrorAlert(result.message)
                        }
                    }
                }
            }
        }


    }

    override fun onDestroyView() {
        resendTimer?.cancel()
        resendTimer = null
        super.onDestroyView()
    }
}
