package com.rnsoft.colabademo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.rnsoft.colabademo.activities.signinflow.phone.events.OtpSentEvent
import com.rnsoft.colabademo.activities.signinflow.phone.events.SkipEvent
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


@AndroidEntryPoint
class PhoneNumberFragment : BaseFragment() {

    private lateinit var root: View

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val phoneNumberViewModel: PhoneNumberViewModel by activityViewModels()
    private val signUpFlowViewModel: SignUpFlowViewModel by activityViewModels() // Shared Repo.....
    private lateinit var continueButton:Button
    private lateinit var phoneNumber : TextInputEditText
    lateinit var phoneNumLayout : TextInputLayout
    private var len = 0
    private lateinit var loading:ProgressBar
    private lateinit var skipLink:AppCompatTextView
    lateinit var parentLayout : ConstraintLayout


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        root = inflater.inflate(R.layout.phone_number_layout, container, false)

        parentLayout = root.findViewById(R.id.layout_phone_num_verification)
        continueButton = root.findViewById<Button>(R.id.continueBtn)
        skipLink = root.findViewById<AppCompatTextView>(R.id.skipTextLink)
        phoneNumber = root.findViewById(R.id.editTextPhoneNumber)
        phoneNumLayout = root.findViewById(R.id.layout_mobilenum)
        phoneNumber.hideKeyboard()


        //val locale = requireContext().resources.configuration.locale.country
        //Log.e("get-country", locale)

        phoneNumber.setOnFocusChangeListener(CustomFocusListenerForEditText(phoneNumber,phoneNumLayout, requireContext()))
        phoneNumber.addTextChangedListener(PhoneTextFormatter(phoneNumber, "(###) ###-####"))

        parentLayout.setOnClickListener {
            phoneNumber.clearFocus()
            parentLayout.hideKeyboard()
        }

        phoneNumber.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                val str: String = phoneNumber.text.toString()
                len = str.length
            }
            override fun afterTextChanged(s: Editable) {
                val str: String = phoneNumber.text.toString()
                /*
                if (str.length >= 3 && len < str.length) {
                    if(str.contains("("))
                        phoneNumber.setText(phoneNumber.text.toString() + ") ");
                    else
                        phoneNumber.setText("("+phoneNumber.text.toString() + ") ")
                    phoneNumber.setSelection(phoneNumber.text.length)
                }
                if (str.length == 9 && len < str.length) {
                    phoneNumber.append("-")
                }

                 */
                continueButton.isEnabled = str.length == 14
            }
        })



        phoneNumber.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                //Perform Code
                phoneNumber.hideKeyboard()
                val str: String = phoneNumber.text.toString()
                if(str.length == 14)
                    continueButton.performClick()
                return@OnKeyListener true
            }

            false
        })

        if (sharedPreferences.getInt(AppConstant.tenantTwoFaSetting, 0) == 3 &&
            AppConstant.userTwoFaSetting == null
        )
            skipLink.visibility = View.VISIBLE

        skipLink.setOnClickListener {
            //phoneNumberError.text =""
            loading.visibility = View.VISIBLE
            toggleButtonState(false)
            phoneNumberViewModel.skipTwoFactor()
        }

        continueButton.setOnClickListener {
            //phoneNumberError.text =""
            sharedPreferences.getString(AppConstant.token, "")?.let {
                loading.visibility = View.VISIBLE
                toggleButtonState(false)
                signUpFlowViewModel.sendOtpToPhone(intermediateToken = it, phoneNumber = phoneNumber.text.toString())
            }
        }

        super.addListeners(root as ViewGroup)
        return root
    }



    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun navigateToDashboardScreen() {
        val intent = Intent(requireActivity(), DashBoardActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOtpReceivedEvent(event: OtpSentEvent) {
        loading.visibility = View.INVISIBLE
        toggleButtonState(true)
        val otpSentResponse =event.otpSentResponse
        Log.e("otp-sent", otpSentResponse.toString())
        when (otpSentResponse.code) {
            "200" -> findNavController().navigate(R.id.otp_verification_id, null)
            "400" -> {
                if(otpSentResponse.otpData?.twoFaMaxAttemptsCoolTimeInSeconds != null)
                    findNavController().navigate(R.id.otp_verification_id, null)
                else
                    otpSentResponse.message?.let {
                        SandbarUtils.showRegular(requireActivity(), it)
                    }
            }
            "429" -> {
                if(otpSentResponse.otpData?.twoFaMaxAttemptsCoolTimeInSeconds != null)
                    findNavController().navigate(R.id.otp_verification_id, null)
                else
                    otpSentResponse.message?.let {
                        SandbarUtils.showRegular(requireActivity(), it)
                    }
            }
            AppConstant.INTERNET_ERR_CODE->{
                SandbarUtils.showRegular(requireActivity() , AppConstant.INTERNET_ERR_MSG)
            }
            else -> {
                if(otpSentResponse.message!=null)
                    SandbarUtils.showRegular(requireActivity() , otpSentResponse.message)
                else
                    SandbarUtils.showError(requireActivity(),"Number can not be verified...")
            }

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSkipEvent(event: SkipEvent) {
        loading.visibility = View.INVISIBLE
        toggleButtonState(true)
        val skipTwoFactorResponse =event.skipTwoFactorResponse
        when (skipTwoFactorResponse.code) {
            "200" -> navigateToDashboardScreen()
            else -> {
                skipTwoFactorResponse.message?.let { it1 -> SandbarUtils.showSuccess(requireActivity(), it1) }
            }
        }
    }

    private fun toggleButtonState(bool:Boolean){
        skipLink.isEnabled = bool
        continueButton.isEnabled = bool
    }

}
