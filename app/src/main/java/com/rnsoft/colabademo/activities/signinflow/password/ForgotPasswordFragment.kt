package com.rnsoft.colabademo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ForgotPasswordFragment : BaseFragment() {
    private lateinit var root: View

    private val forgotPasswordViewModel: ForgotPasswordViewModel by activityViewModels()
    private lateinit var loading:ProgressBar
    private lateinit var resetButton:Button
    lateinit var emailLayout : TextInputLayout
    lateinit var userEmailField : TextInputEditText
    private lateinit var parentLayout : ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        root = inflater.inflate(R.layout.password_forgot, container, false)

        resetButton  = root.findViewById<Button>(R.id.resetPasswordBtn)
        userEmailField = root.findViewById<TextInputEditText>(R.id.editTextEmail)
        emailLayout = root.findViewById<TextInputLayout>(R.id.til_pswrd_email)
        parentLayout= root.findViewById(R.id.layout_forgot_password)
        //errorTextView = root.findViewById(R.id.emailErrorTextView)
        loading = root.findViewById(R.id.loader_forgot_screen)

        resetButton.setOnClickListener {
            it.isEnabled = false
            //errorTextView.text =""
            clearError()
            loading.visibility = View.VISIBLE
            forgotPasswordViewModel.forgotPassword(userEmailField.text.toString())
        }

        userEmailField.setOnFocusChangeListener(CustomFocusListenerForEditText(userEmailField,emailLayout, requireContext()))

        parentLayout.setOnClickListener {
            hideSoftKeyboard()
        }

        super.addListeners(root as ViewGroup)
        return root
    }

    fun setError(errorMsg: String) {
        emailLayout.helperText = errorMsg
        emailLayout.setBoxStrokeColorStateList(
            AppCompatResources.getColorStateList(requireContext(), R.color.primary_info_stroke_error_color))
    }

    fun clearError() {
        emailLayout.helperText = ""
        emailLayout.setBoxStrokeColorStateList(
            AppCompatResources.getColorStateList(
                requireContext(),
                R.color.primary_info_line_color
            )
        )
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
    fun onPasswordEventReceived(event: ForgotPasswordEvent) {
        loading.visibility = View.INVISIBLE
        resetButton.isEnabled = true
        val forgotPasswordResponse = event.forgotPasswordResponse
        when(event.forgotPasswordResponse.code)
        {
            "400" -> forgotPasswordResponse.message?.let { setError(it) }
            "300" ->forgotPasswordResponse.message?.let { setError(it) }
            "600" ->forgotPasswordResponse.message?.let { setError(it) }
            "200" -> {
                //SandbarUtils.showSuccess(requireActivity(), "Email with password reset instructions has been sent")
                //-- the navigated screen is sufficient for the user...
                findNavController().navigate(R.id.back_to_login_id, null)
            }
            else -> {
                SandbarUtils.showError(requireActivity(),"Failure Exception...")
            }
        }
    }

    private fun hideSoftKeyboard(){
        val imm = view?.let { ContextCompat.getSystemService(it.context, InputMethodManager::class.java) }
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }



    /*
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            forgotPasswordViewModel.forgotPasswordResponse?.observe(viewLifecycleOwner, Observer {
                val forgotPasswordResponse = it ?: return@Observer
                loading.visibility = View.INVISIBLE
                when(forgotPasswordResponse.code) {
                    "400" ->errorTextView.text = forgotPasswordResponse.message.let { it1 -> it1 }
                    "300" ->errorTextView.text = forgotPasswordResponse.message.let { it1 -> it1 }
                    "600" ->errorTextView.text = forgotPasswordResponse.message.let { it1 -> it1 }
                    "200" -> {
                        forgotPasswordViewModel.forgotPasswordResponse?.removeObservers(viewLifecycleOwner)
                        showToast("success")
                        findNavController().navigate(R.id.back_to_login_id, null)
                    }
                    else -> {
                        showToast("Failure Exception...")
                    }
                }
            })
    }

     */

}