package com.rnsoft.colabademo

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.rnsoft.colabademo.activities.signinflow.phone.events.OtpSentEvent
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


@AndroidEntryPoint
class OtpFragment: BaseFragment() {

    private lateinit var root:View
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    @Inject
    lateinit var spEditor: SharedPreferences.Editor
    private val signUpFlowActivity: SignUpFlowViewModel by activityViewModels() // Shared Repo.....
    private val otpViewModel: OtpViewModel by activityViewModels() // Shared Repo.....
    private lateinit var resendLink:TextView
    private lateinit var nearToResetTextView:TextView
    private lateinit var verifyButton:Button
    private lateinit var otpEditText:TextInputEditText
    private lateinit var textInputLayout : TextInputLayout
    private var cTimer:CountDownTimer? = null
    private lateinit var insideTimerLayout:ConstraintLayout
    private lateinit var otpLoader:ProgressBar
    private lateinit var minuteTextView:TextView
    private lateinit var secondTextView:TextView
    private lateinit var phoneMessageText:TextView
    private lateinit var otpMessageTextView:TextView
    private lateinit var notAskChekBox:CheckBox
    private lateinit var tickImage:ImageView
    private lateinit var crossImage:ImageView
    private var minutes:Int = 0
    private var seconds:Int = 0
    private var attemptLeft:Int = 0
    private var restoreBtnState:Boolean = false
    private var otpTextFieldState:Boolean = false
    lateinit var parentLayout : ConstraintLayout


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        root = inflater.inflate(R.layout.otp_screen_material, container, false)
        verifyButton = root.findViewById<Button>(R.id.verifyBtn)
        resendLink = root.findViewById(R.id.resendTextView)
        nearToResetTextView = root.findViewById(R.id.nearToResetTextView)
        otpEditText = root.findViewById(R.id.otpCodeEditText)
        insideTimerLayout = root.findViewById(R.id.inside_timer_constraintlayout)
        otpLoader = root.findViewById(R.id.loader_otp_screen)
        minuteTextView = root.findViewById(R.id.minuteTextView)
        secondTextView = root.findViewById(R.id.secondTextView)
        otpMessageTextView = root.findViewById(R.id.timerMessageTextView)
        notAskChekBox = root.findViewById(R.id.permission_checkbox)
        tickImage = root.findViewById<ImageView>(R.id.tick_image)
        crossImage= root.findViewById<ImageView>(R.id.cross_image)
        textInputLayout=root.findViewById(R.id.til_otp_verify)
        parentLayout=root.findViewById(R.id.layout_otp_verify)
        phoneMessageText=root.findViewById(R.id.phoneMessageTextView)
        //////////////////////////////////////////////////////////////////////////////////////////////

        sharedPreferences.getString(AppConstant.phoneNumber,"")?.let { phoneNumber ->
            if(phoneNumber.isNotEmpty() && phoneNumber.isNotBlank()) {
                val lastFour = phoneNumber.substring(phoneNumber.length - 4, phoneNumber.length)
                phoneMessageText.text = "Enter the code sent to (***) ***-" + lastFour
            }
        }

        otpEditText.setOnFocusChangeListener(CustomFocusListenerForEditText(otpEditText, textInputLayout, requireContext()))

        otpEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                val str: String = otpEditText.text.toString()
                //verifyButton.isEnabled = str.length == 6
                crossImage.visibility = View.INVISIBLE
                if(str.length == 6) {
                    otpEditText.isEnabled = false
                    disabledButtons()
                    val otpVal = otpEditText.text.toString()
                    otpViewModel.verifyOtp(otpVal.toInt())
                }

            }
        })

        resendLink.setOnClickListener {
            sharedPreferences.getString(AppConstant.phoneNumber,"")?.let { phoneNum->
                sharedPreferences.getString(AppConstant.token,"")?.let { intermediateToken ->
                    restoreBtnState = verifyButton.isEnabled
                    otpTextFieldState = otpEditText.isEnabled
                    disabledButtons()
                    signUpFlowActivity.sendOtpToPhone(intermediateToken, phoneNum)
                }
            }
        }

        verifyButton.setOnClickListener {
            if(notAskChekBox.isChecked) {
                sharedPreferences.getString(AppConstant.token,"")?.let {
                    disabledButtons()
                    otpViewModel.notAskForOtp(it)
                }
            }
            else
                navigateToDashBoardScreen()
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, testCallback )

        checkForTimer()
        updateResendCount()


        parentLayout.setOnClickListener { hideSoftKeyboard() }

        super.addListeners(root as ViewGroup)
        return root
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
        //cTimer?.cancel()

    }


    private val testCallback:OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            cTimer?.cancel()
            findNavController().popBackStack()
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOtpReceivedOnPhone(event: OtpSentEvent) {
        otpLoader.visibility = View.INVISIBLE

        otpEditText.isEnabled = otpTextFieldState
        verifyButton.isEnabled = restoreBtnState

        val otpSentResponse =event.otpSentResponse
        resendLink.isEnabled = true

        Log.e("otp-sent", otpSentResponse.toString())

        if(otpSentResponse.code == AppConstant.INTERNET_ERR_CODE)
            SandbarUtils.showError(requireActivity(), AppConstant.INTERNET_ERR_MSG)
        else if (otpSentResponse.code == "400" && otpSentResponse.otpData!=null) {
            checkForTimer()
            updateResendCount()
        }
        else
        if (otpSentResponse.code == "429" && otpSentResponse.otpData!=null) {
            if(otpSentResponse.otpData.twoFaMaxAttemptsCoolTimeInSeconds!=null && otpSentResponse.otpData.twoFaMaxAttemptsCoolTimeInSeconds!=0) {
                updateResendCount()
                checkForTimer()
                otpSentResponse.message?.let {
                    SandbarUtils.showSuccess(requireActivity(), it)
                }
            }
            else
                otpSentResponse.message?.let {
                    SandbarUtils.showRegular(requireActivity(), it)
                }
        }
        else {
            otpSentResponse.message?.let {  SandbarUtils.showSuccess(requireActivity(), it) }
            updateResendCount()
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOtpVerificationCompleteEvent(event: OtpVerificationEvent) {
        otpLoader.visibility = View.INVISIBLE
        resendLink.isEnabled = true

        val verificationResponse = event.otpVerificationResponse

        if(verificationResponse.code == AppConstant.INTERNET_ERR_CODE)
            SandbarUtils.showError(requireActivity(), AppConstant.INTERNET_ERR_MSG)
        else
        if(verificationResponse.code == "200" &&  verificationResponse.data != null) {
            verifyButton.isEnabled = true
            tickImage.visibility = View.VISIBLE
            resendLink.setOnClickListener(null)
            resendLink.isClickable = false
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {}
        }
        else if(verificationResponse.message!=null) {
                crossImage.visibility = View.VISIBLE
                SandbarUtils.showRegular(requireActivity(), verificationResponse.message)
                otpEditText.isEnabled = true
        }
        else {
                crossImage.visibility = View.VISIBLE
                SandbarUtils.showError(requireActivity(), "Response contains no message...")
                otpEditText.isEnabled = true
        }



    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun notAskForOtpEventReceived(event: NotAskForOtpEvent) {
        otpLoader.visibility = View.INVISIBLE


        val notAskForOtpResponse =event.notAskForOtpResponse
        Log.e("notAskForOtpResponse==", notAskForOtpResponse.toString())
        if (notAskForOtpResponse.code == "200" && notAskForOtpResponse.status=="OK") {
            notAskForOtpResponse.notAskForData?.dontAskTwoFaIdentifier?.let {
                spEditor.putString(AppConstant.dontAskTwoFaIdentifier, it).apply()
            }
            navigateToDashBoardScreen()
        }
        else {
            verifyButton.isEnabled = true
            resendLink.isEnabled = true
            notAskForOtpResponse.message?.let { SandbarUtils.showError(requireActivity(), it)  }
        }
    }


    private fun navigateToDashBoardScreen(){
        startActivity(Intent(requireActivity(), DashBoardActivity::class.java))
        requireActivity().finish()
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun checkForTimer(){
        sharedPreferences.getString(AppConstant.otpDataJson, "").let {
            val test = sharedPreferences.getString(AppConstant.otpDataJson, "")
            val obj = Gson().fromJson(test, OtpData::class.java)

            if(obj!=null) {
               if (obj.twoFaMaxAttemptsCoolTimeInSeconds != null && obj.twoFaMaxAttemptsCoolTimeInSeconds>3) {
                    setUpTimerInitial(obj.twoFaMaxAttemptsCoolTimeInSeconds)
                    toggleTimerView(true)
                }
                else {
                   toggleTimerView(false)
                   cTimer?.cancel()
               }
            }
            else {
                toggleTimerView(false)
                cTimer?.cancel()
            }
        }
    }

    private fun toggleTimerView(bool:Boolean) {
        if(bool) {
            insideTimerLayout.visibility = View.VISIBLE
            nearToResetTextView.visibility = View.GONE
            resendLink.visibility = View.GONE
        }
        else {
            insideTimerLayout.visibility = View.GONE
            nearToResetTextView.visibility = View.VISIBLE
            resendLink.visibility = View.VISIBLE
        }
        updateResendCount()
    }

    private fun updateResendCount(){
        sharedPreferences.getString(AppConstant.otpDataJson, "")?.let { otpDataReceived->
            val obj = Gson().fromJson(otpDataReceived, OtpData::class.java)
            if (obj != null) {
                sharedPreferences.getInt(AppConstant.maxOtpSendAllowed, 5).let { maxOtpSendAllowed->
                    if(obj.attemptsCount!=null){
                        if(obj.attemptsCount!=0)
                            attemptLeft = maxOtpSendAllowed - obj.attemptsCount
                    }
                    else
                        attemptLeft = maxOtpSendAllowed
                }
            }
        }
        val attemptsLeftString = resources.getString(R.string.resend_code) + " ($attemptLeft left)"
        resendLink.text = attemptsLeftString
    }

    private fun setUpTimerInitial(remainingSeconds:Int){

        if(remainingSeconds>60){
            minutes =  (remainingSeconds / 60)
            seconds = (remainingSeconds - (minutes * 60))
            if(seconds<0)
                seconds = 0
            val totalSeconds:Long = (remainingSeconds * 1000).toLong()
            Log.e("all - ", "$remainingSeconds becomes $minutes min $seconds seconds")
            setTimeMessageAsPerDesign(minutes)
            startTimer(totalSeconds)
        }
        else{
            minutes = 0
            seconds = remainingSeconds
            if(seconds<0)
                seconds = 0
            setTimeMessageAsPerDesign(minutes)
            startTimer((remainingSeconds * 1000).toLong())
        }
    }

    private  fun setTimeMessageAsPerDesign(minutes:Int){
        val designMsg = if(minutes == 0)
            "Max resend attempts reached. Please try again after less then a minute."
        else
            "Max resend attempts reached. Please try again after $minutes minutes"
        otpMessageTextView.text  =  designMsg
    }

    private fun startTimer(totalSeconds:Long){

        cTimer = object : CountDownTimer(totalSeconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                seconds--
                Log.e("millisUntilFinished-", millisUntilFinished.toString())
                Log.e("Minutes - ", "$minutes  seconds - $seconds")
                if(minutes > 0  && seconds == 0)  {
                    Log.e("MinutesNow--", "Decreased")
                    minutes -= 1
                    seconds = 60
                    setTimeMessageAsPerDesign(minutes)
                }
                else
                if(minutes <= 0 && seconds <= 0){
                    Log.e("TimerStop", "StopNow")
                    toggleTimerView(false)
                    cancelTimer()
                    updateResendCount()
                }
                minuteTextView.text = "0"+minutes
                if(seconds<10)
                    secondTextView.text = ": 0"+seconds.toString()
                else
                    secondTextView.text = ": "+seconds.toString()
            }
            override fun onFinish() {
                Log.e("Timer Finished-", "Completed...")
                toggleTimerView(false)
                updateResendCount()
                cancelTimer()
            }
        }
        cTimer?.start()
    }

    private fun cancelTimer() {
        cTimer?.cancel()
    }

    private fun hideSoftKeyboard(){
        val imm = view?.let { ContextCompat.getSystemService(it.context, InputMethodManager::class.java) }
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }




    private fun disabledButtons(){
        resendLink.isEnabled = false
        verifyButton.isEnabled = false
        otpLoader.visibility = View.VISIBLE
    }



}
