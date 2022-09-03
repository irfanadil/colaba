package com.rnsoft.colabademo

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import co.infinum.goldfinger.Goldfinger
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.rnsoft.colabademo.databinding.LoginLayoutBinding
import com.rnsoft.colabademo.utils.CustomMaterialFields
import com.rnsoft.colabademo.utils.NumberTextFormat
import kotlinx.android.synthetic.main.login_layout.*


@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    private lateinit var root: View
    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var userEmailField: TextInputEditText
    private lateinit var passwordField: TextInputEditText
    private lateinit var loading: ProgressBar
    private lateinit var biometricSwitch: SwitchCompat
    private lateinit var forgotPasswordLink: AppCompatTextView
    private lateinit var loginButton: AppCompatButton
    private lateinit var imageView5: ImageView
    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    lateinit var parentLayout: ConstraintLayout
    private lateinit var mTextWatcher : TextWatcher




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.login_layout, container, false)
        super.addListeners(root as ViewGroup)

        setupFragment()
        hideSoftKeyboard()
        passwordLayout.setEndIconOnClickListener(View.OnClickListener {
            if (editTextPassword.getTransformationMethod()
                    .equals(PasswordTransformationMethod.getInstance())
            ) { //  hide password
                editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
                passwordLayout.setEndIconDrawable(R.drawable.ic_eye_hide)
            } else {
                editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance())
                passwordLayout.setEndIconDrawable(R.drawable.ic_eye_icon_svg)
            }
        })


        mTextWatcher = object : TextWatcher {
            override fun afterTextChanged(et: Editable?) {
                when {
                    et === userEmailField.editableText -> {
                        if(userEmailField.text.toString().length > 0){
                            clearError(emailLayout)
                            userEmailField.removeTextChangedListener(mTextWatcher)
                        }
                    }
                    et === passwordField.editableText -> {
                        if(passwordField.text.toString().length > 0){
                            clearError(passwordLayout)
                            passwordField.removeTextChangedListener(mTextWatcher)
                        }
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        }

        return root
    }

    private val testCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
        }
    }

    private fun disableEditText(editText: EditText) {
        editText.isFocusable = false
        editText.isEnabled = false
        editText.isCursorVisible = false
        editText.keyListener = null
        //editText.setBackgroundColor()
    }

    private fun setupFragment() {
        userEmailField = root.findViewById(R.id.editTextEmail)
        passwordField = root.findViewById(R.id.editTextPassword)
        emailLayout = root.findViewById(R.id.layout_login_email)
        passwordLayout = root.findViewById(R.id.layout_login_password)
        parentLayout = root.findViewById(R.id.layout_login)
        biometricSwitch = root.findViewById<SwitchCompat>(R.id.switch1)
        forgotPasswordLink = root.findViewById<AppCompatTextView>(R.id.forgotPasswordLink)
        loginButton = root.findViewById<AppCompatButton>(R.id.loginBtn)
        loading = root.findViewById<ProgressBar>(R.id.loader_login_screen)
        imageView5 = root.findViewById<ImageView>(R.id.imageView5)

        setLableFocus()

        if (activity is SignUpFlowActivity) {
            //Log.e("resumeState= ","LoginFragment -userEmailField ="+(activity as SignUpFlowActivity).resumeState)
            if (AppSetting.initialScreenLoaded) {
                disableEditText(userEmailField)
                activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, testCallback)
            }
        }

        userEmailField.setText("sadiq@rainsoftfn.com")
        passwordField.setText("rainsoft")

        imageView5.setOnClickListener {
            navigateToDashBoard(null)
        }


        resetToInitialPosition()
        loginButton.setOnClickListener {
//            loading.visibility = View.VISIBLE
//            toggleButtonState(false)
//            resetToInitialPosition()
//            loginViewModel.login(userEmailField.text.toString(), passwordField.text.toString())

            val emailError = LoginUtil.isValidEmail(editTextEmail.text.toString().trim())
            val passworError =
                LoginUtil.checkPasswordLength(editTextPassword.text.toString().trim())

            if (emailError != null) {
                CustomMaterialFields.setError(emailLayout, getString(R.string.error_field_required), requireContext())
            }
            if (passworError != null) {
                CustomMaterialFields.setError(passwordLayout, getString(R.string.error_field_required), requireActivity())
            }

            if (emailError == null && passworError == null) {
                clearError(emailLayout)
                clearError(passwordLayout)

                loading.visibility = View.VISIBLE
                toggleButtonState(false)
                loginViewModel.login(
                    editTextEmail.text.toString().trim(),
                    editTextPassword.text.toString().trim()
                )
            }

        }

        forgotPasswordLink.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.forgot_password_id, null)
        }

        goldfinger = Goldfinger.Builder(requireActivity())
            .logEnabled(true)
            .build()

        biometricSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (goldfinger.canAuthenticate()) {

                } else {
                    biometricSwitch.isChecked = false
                    SandbarUtils.showRegular(
                        requireActivity(),
                        resources.getString((R.string.biometric_check_two))
                    )

                    /*
                    ToastUtils.init(requireActivity().application)
                    ToastUtils.setView(R.layout.toast_error_layout)
                    ToastUtils.setGravity(Gravity.BOTTOM, 0, 60)
                    ToastUtils.show(resources.getString((R.string.biometric_check)))
                    */
                }
            }
            AppSetting.biometricEnabled = biometricSwitch.isChecked
        }
        parentLayout.setOnClickListener {
            hideSoftKeyboard()
        }

    }

    private lateinit var goldfinger: Goldfinger

    private fun resetToInitialPosition() {
        clearError(emailLayout)
        clearError(passwordLayout)
    }

    private fun navigateToDashBoard(model: LoginResponse?) {
        val intent = Intent(requireActivity(), DashBoardActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun navigateToOtpScreen() =
        findNavController().navigate(R.id.otp_verification_id, null)

    private fun navigateToPhoneScreen() =
        findNavController().navigate(R.id.phone_number_id, null)


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }


    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginEventReceived(event: LoginEvent) {
        toggleButtonState(true)
        event.loginResponseResult.let {
            loading.visibility = View.INVISIBLE

            if (it.emailError != null) {
                setError(emailLayout, it.emailError)

            } else if (it.passwordError != null) {
                setError(passwordLayout, it.passwordError)

            } else if (it.responseError != null) {
                //showToast(it.responseError)
                if (it.responseError == AppConstant.INTERNET_ERR_MSG)
                    SandbarUtils.showError(requireActivity(), AppConstant.INTERNET_ERR_MSG)
                else
                    SandbarUtils.showError(requireActivity(), it.responseError)
            } else if (it.success != null) {
                clearError(emailLayout)
                clearError(passwordLayout)

                when (it.screenNumber){
                    1 -> {
                        if (biometricSwitch.isChecked)
                            sharedPreferences.edit()
                                .putBoolean(AppConstant.isbiometricEnabled, true).apply()

                        if (activity is SignUpFlowActivity) {
                            if ((activity as SignUpFlowActivity).resumeState) {
                                AppSetting.initialScreenLoaded = false
                                requireActivity().finish()
                            } else
                                navigateToDashBoard(it.success)
                        } else
                            navigateToDashBoard(it.success)


                    }
                    2 -> navigateToPhoneScreen()
                    3 -> navigateToOtpScreen()
                    else -> {
                        //showToast(R.string.we_have_send_you_email)
                        SandbarUtils.showRegular(requireActivity(), AppConstant.INTERNET_ERR_MSG)

                    }
                }
            }
        }
    }

    fun setError(textInputlayout: TextInputLayout, errorMsg: String) {
        textInputlayout.helperText = errorMsg
        textInputlayout.setBoxStrokeColorStateList(
            AppCompatResources.getColorStateList(
                requireContext(),
                R.color.primary_info_stroke_error_color
            )
        )
    }

    fun clearError(textInputlayout: TextInputLayout) {
        textInputlayout.helperText = ""
        textInputlayout.setBoxStrokeColorStateList(
            AppCompatResources.getColorStateList(
                requireContext(),
                R.color.primary_info_line_color
            )
        )
    }

    private fun toggleButtonState(bool: Boolean) {
        forgotPasswordLink.isEnabled = bool
        loginButton.isEnabled = bool
    }

    private fun setLableFocus() {

        userEmailField.setOnFocusChangeListener { view, hasFocus ->
            val emailError = LoginUtil.isValidEmail(userEmailField.text.toString().trim())
            if (hasFocus){
                CustomMaterialFields.setColor(emailLayout, R.color.grey_color_two, requireContext())
                userEmailField.addTextChangedListener(mTextWatcher)
                passwordField.removeTextChangedListener(mTextWatcher)
            } else {
                if(emailError != null) {
                    setError(emailLayout, emailError)
                } else {
                    clearError(emailLayout)
                }

                if(userEmailField.text.toString().trim().length > 0)
                    CustomMaterialFields.setColor(emailLayout, R.color.grey_color_two, requireContext())
                else
                    CustomMaterialFields.setColor(emailLayout, R.color.grey_color_three, requireContext())
            }
        }

        passwordField.setOnFocusChangeListener { view, hasFocus ->
            val passworError = LoginUtil.checkPasswordLength(passwordField.text.toString().trim())
            if (hasFocus){
                CustomMaterialFields.setColor(passwordLayout, R.color.grey_color_two, requireContext())
                passwordField.addTextChangedListener(mTextWatcher)
                userEmailField.removeTextChangedListener(mTextWatcher)

            } else {
                if (passworError != null)
                    setError(passwordLayout, passworError)
                else
                    clearError(passwordLayout)

                if(passwordField.text.toString().trim().length > 0)
                    CustomMaterialFields.setColor(passwordLayout, R.color.grey_color_two, requireContext())
                else
                    CustomMaterialFields.setColor(passwordLayout, R.color.grey_color_three, requireContext())

            }
        }
    }



        private fun hideSoftKeyboard() {
        val imm =
            view?.let { ContextCompat.getSystemService(it.context, InputMethodManager::class.java) }
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

}


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Getting phone lock broadcast ......

/*
private var mPowerKeyReceiver: BroadcastReceiver? = null

private fun registerBroadcastReceiver() {
    val theFilter = IntentFilter()
    /** System Defined Broadcast  */
    theFilter.addAction(Intent.ACTION_SCREEN_ON)
    theFilter.addAction(Intent.ACTION_SCREEN_OFF)
    mPowerKeyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val strAction = intent.action
            if (strAction == Intent.ACTION_SCREEN_OFF || strAction == Intent.ACTION_SCREEN_ON) {
                // > Your playground~!
                Log.e("Screen-","ON-OFF LOCK---")
            }
        }
    }
    requireActivity().applicationContext
        .registerReceiver(mPowerKeyReceiver, theFilter)
}

private fun unregisterReceiver() {
        try {
            requireActivity().applicationContext
                .unregisterReceiver(mPowerKeyReceiver)
        } catch (e: IllegalArgumentException) {
            mPowerKeyReceiver = null
        }
}


 */


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//private fun showToast(@StringRes errorString: Int) {

//SandbarUtils.showRegular(requireActivity(), "some error" )

//ToastUtils.init(requireActivity().application)

//ToastUtils.setView(R.layout.toast_error_layout)
//ToastUtils.setGravity(Gravity.BOTTOM, 0, 60)


//ToastUtils.show("Some Error coming from the case")


// }


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// adding observer to login...

/*
        loginViewModel.loginResponseResult.observe(viewLifecycleOwner, Observer {
            val loginResult = it ?: return@Observer
            loading.visibility = View.INVISIBLE

            if (loginResult.emailError != null) {
                emailError.visibility = View.VISIBLE
                emailError.text = it.emailError?.let { it1 -> resources.getString(it1) }
            } else if (loginResult.passwordError != null) {
                passwordError.visibility = View.VISIBLE
                passwordError.text = it.passwordError?.let { it1 -> resources.getString(it1) }
            } else if (loginResult.responseError != null) {
                showToast(loginResult.responseError)
            } else if (loginResult.success != null) {
                emailError.visibility = View.GONE
                passwordError.visibility = View.GONE
                when(it.screenNumber)
                {
                    1 -> navigateToDashBoard(loginResult.success)
                    2 -> navigateToPhoneScreen(loginResult.success)
                    3 -> navigateToOtpScreen(loginResult.success)
                    else -> navigateToDashBoard(loginResult.success)
                }
            }
        })


        lifecycleScope.launchWhenStarted  {
            // Trigger the flow and start listening for values.
            // Note that this happens when lifecycle is STARTED and stops
            // collecting when the lifecycle is STOPPED
            loginViewModel.loginResponseResult.collect { it ->
                // New value received
                loading.visibility = View.INVISIBLE
                if (it.emailError != null) {
                    emailError.visibility = View.VISIBLE
                    emailError.text = it.emailError.let { it1 -> resources.getString(it1) }
                } else if (it.passwordError != null) {
                    passwordError.visibility = View.VISIBLE
                    passwordError.text = it.passwordError.let { it1 -> resources.getString(it1) }
                } else if (it.responseError != null) {
                    showToast(it.responseError)
                } else if (it.success != null) {
                    emailError.visibility = View.GONE
                    passwordError.visibility = View.GONE
                    when(it.screenNumber)
                    {
                        0 -> resetToInitialPosition()
                        1 -> navigateToDashBoard(it.success)
                        2 -> navigateToPhoneScreen(it.success)
                        3 -> navigateToOtpScreen(it.success)
                        else -> navigateToDashBoard(it.success)
                    }
                }
            }
        }


        @ExperimentalCoroutinesApi
    private fun runColdFlow(email:String, password:String){
        lifecycleScope.launch  {
            // Trigger the flow and start listening for values.
            // Note that this happens when lifecycle is STARTED and stops
            // collecting when the lifecycle is STOPPED
            loginViewModel.newLoginFlow(email, password).collect { it ->
                // New value received
                loading.visibility = View.INVISIBLE
                if (it.emailError != null) {
                    emailError.visibility = View.VISIBLE
                    emailError.text = it.emailError.let { it1 -> resources.getString(it1) }
                } else if (it.passwordError != null) {
                    passwordError.visibility = View.VISIBLE
                    passwordError.text = it.passwordError.let { it1 -> resources.getString(it1) }
                } else if (it.responseError != null) {
                    showToast(it.responseError)
                } else if (it.success != null) {
                    emailError.visibility = View.GONE
                    passwordError.visibility = View.GONE
                    when(it.screenNumber)
                    {
                        0 -> resetToInitialPosition()
                        1 -> navigateToDashBoard(it.success)
                        2 -> navigateToPhoneScreen()
                        3 -> navigateToOtpScreen()
                        else -> navigateToDashBoard(it.success)
                    }
                }
            }
        }
    }
*/