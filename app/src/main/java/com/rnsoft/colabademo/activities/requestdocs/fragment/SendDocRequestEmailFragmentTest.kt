package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ImageSpan
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.rnsoft.colabademo.databinding.SendDocRequestLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import javax.inject.Inject
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import com.google.android.material.chip.ChipGroup
import java.util.regex.Pattern
import android.widget.Toast
import com.rnsoft.colabademo.utils.CustomMaterialFields
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * Created by Anita Kiran on 10/4/2021.
 */

@AndroidEntryPoint
class SendDocRequestEmailFragmentTest : DocsTypesBaseFragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private val viewModel: RequestDocsViewModel by activityViewModels()
    private lateinit var binding : SendDocRequestLayoutBinding
    private var savedViewInstance: View? = null
    private var loanApplicationId: Int? = null
    var templateList: ArrayList<Template> = ArrayList()
    companion object {
        var selectedItem = -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (savedViewInstance != null) {
            savedViewInstance
        } else {
            binding = SendDocRequestLayoutBinding.inflate(inflater, container, false)
            savedViewInstance = binding.root
            super.addListeners(savedViewInstance as ViewGroup)

            val activity = (activity as? RequestDocsActivity)
            activity?.loanApplicationId?.let {
                loanApplicationId = it
            }

            setupUI()
            getDropDownTemplate()
            setClickEvents()
            savedViewInstance
        }
    }

    private fun addNewChip(email: String,chipGroup : FlexboxLayout){
        val chip = LayoutInflater.from(context).inflate(R.layout.chip, chipGroup, false) as Chip
        //val chip = Chip(context)
        chip.text = email
        chip.chipIcon = ContextCompat.getDrawable(requireContext(), R.mipmap.ic_launcher_round)
        chip.isCloseIconEnabled = true
        chip.isClickable = true
        chip.isCheckable = false
        chipGroup.addView(chip as View, chipGroup.childCount - 1)
        chip.setOnCloseIconClickListener { chipGroup.removeView(chip as View) }
        binding.etRecipientEmail.setText("")
    }

    private fun setupUI(){
        binding.btnSendRequest.setOnClickListener {
          //findNavController().navigate(R.id.action_request_sent)
            sendDocRequest()
        }

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_selected_doc_fragment)
        }
    }

    private fun sendDocRequest(){

        lifecycleScope.launchWhenStarted {
            sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                //binding.loaderDocRequest.visibility = View.VISIBLE
                if(loanApplicationId != null) {
                    //val emailBody= Email()

                    //val requestList: ArrayList<DocRequestDataList> = ArrayList()
                    //requestList.add(DocRequestDataList(email = emailBody,documents = ))
                    //val sendRequestBody = SendDocRequestModel(loanApplicationId = loanApplicationId!!,)

                    //viewModel.sendDocRequest(authToken, sendRequestBody)
                }
            }
        }
    }

    private fun getDropDownTemplate(){
        lifecycleScope.launchWhenStarted {
            sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                val call = async {
                    binding.loaderDocRequest.visibility = View.VISIBLE
                    viewModel.getEmailTemplates(authToken)
                }
                call.await()
                setEmailTemplate()
            }
        }
    }

    private fun setEmailTemplate(){
        viewModel.emailTemplates.observe(viewLifecycleOwner, { data ->
            if (data != null && data.size > 0){

                for(item in data){
                    templateList.add(
                        Template( id= item.id ,templateId = item.id, templateName = item.templateName, docDesc = item.templateDescription))
                }
                val spinnerAdapter = EmailTemplateSpinnerAdapter(requireContext(), R.layout.email_template_item, templateList)
                binding.tvEmailType.setAdapter(spinnerAdapter)
                binding.tvEmailType.setOnClickListener {
                    binding.tvEmailType.showDropDown()
                }

                // set initial template
                getEmailBody(0,)

                binding.tvEmailType.onItemClickListener =
                    AdapterView.OnItemClickListener { p0, p1, position, id ->
                        selectedItem = position
                        //Log.e("onClick-id",templateList.get(position).templateId + " " +  templateList.get(position).templateName)
                        binding.layoutEmailTemplate.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.grey_color_two))
                        binding.loaderDocRequest.visibility = View.VISIBLE
                        getEmailBody(position)
                    }
            }
        })

    }

    private fun getEmailBody(position : Int){
        lifecycleScope.launchWhenStarted {
            sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                // showloader here
                binding.tvEmailBody.setText("")
                val call = async {
                    viewModel.getEmailTemplateBody(authToken,47,templateList.get(position).templateId) }
                call.await()
                setEmailBody()
            }
        }
    }

    private fun setEmailBody(){
        viewModel.emailTemplateBody.observe(viewLifecycleOwner, { body ->
            binding.loaderDocRequest.visibility = View.GONE
            if(body != null){
                body.emailBody?.let {
                    val builder = StringBuilder()
                    if(it.contains(AppConstant.relaceDocFormat)){
                        //Log.e("combineDocList","$combineDocList")

                        for(item in combineDocList.indices){
                             // builder.append("<p><span style=\"color: rgb(78,78,78);background-color: rgb(255,255,255);font-size: 14px;font-family: Rubik, sans-serif;\">")
                               //   .append("\u25CF").append(" ").append(combineDocList.get(item).docType).append("</span></p>")


                            builder.append("<ul style=\"color: rgb(78,78,78);background-color: rgb(255,255,255);font-size: 14px;font-family: Rubik, sans-serif;\">")
                                .append("\u2022").append(" ").append(combineDocList.get(item).docType).append("</ul>")
                        }



                       // <ul style=\"text-align:left;\"><span style=\"color: rgb(78,78,78);font-size: 14px;font-family: Rubik, sans-serif;\">\(bulletList)</ul>"

                        val newText = it.replace(AppConstant.relaceDocFormat, builder.toString())
                        binding.tvEmailBody.text = Html.fromHtml(newText)
                        binding.layoutEmailBody.visibility = View.VISIBLE
                    }
                }

                body.subject?.let {
                    if(it.isNotEmpty() && it != "null"){
                        binding.etSubjectLine.setText(it)
                        CustomMaterialFields.setColor(binding.layoutSubLine, R.color.grey_color_two, requireContext())
                    }
                }

                body.id?.let {
                    for (item in templateList){
                        if (item.id == it) {
                            binding.tvEmailType.setText(item.templateName, false)
                            CustomMaterialFields.setColor(binding.layoutEmailTemplate, R.color.grey_color_two,
                                requireActivity())
                            break
                        }
                    }
                }

            }
        })



        //      emailBody = emailBody.replacingOccurrences(of: "###RequestDocumentList###", with: "<ul style=\"text-align:left;\"><span style=\"color: rgb(78,78,78);font-size: 14px;font-family: Rubik, sans-serif;\">\(bulletList)</ul>").replacingOccurrences(of: "background-color: rgb(255,255,255);", with: "")
    }

    private fun setClickEvents(){
        binding.etRecipientEmail.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if(event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE || event.keyCode == KeyEvent.KEYCODE_SPACE
                || actionId == KeyEvent.KEYCODE_SPACE){
                //Log.e("Key","Pressed")

                validateEmailAddress()

                true
            }
            /*if(event != null && event.getKeyCode() == KeyEvent.KEYCODE_DEL){
                Toast.makeText(
                    context, "delete pressed",
                    Toast.LENGTH_SHORT
                ).show()
                true
            } */

            else {
                false
            }
        })

        binding.etRecipientEmail.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val text = binding.etRecipientEmail.text.toString()
                if(text.length > 0 ) {
                    val lastChar: String = s.toString().substring(s.length - 1)
                    if (lastChar == " ") {
                        validateEmailAddress()
                        // Toast.makeText(context, "space bar pressed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })

//            binding.etRecipientEmail.setOnKeyListener { _, keyCode, keyEvent ->
//                if (keyCode == KeyEvent.KEYCODE_ENTER || keyEvent.action == EditorInfo.IME_ACTION_DONE) {
//                    Toast.makeText(context, "Enter pressed", Toast.LENGTH_SHORT).show()
//                    return@setOnKeyListener true
//                }
//
//                if(keyCode == KeyEvent.KEYCODE_DEL){
//                    Toast.makeText(context, "Delete pressed", Toast.LENGTH_SHORT).show()
//                    return@setOnKeyListener true
//                }
//
//                if(keyCode == KeyEvent.KEYCODE_SPACE){
//                    Toast.makeText(context, "Spce pressed", Toast.LENGTH_SHORT).show() //
//                    return@setOnKeyListener true
//                }
//                return@setOnKeyListener false
//            }
    }

    private fun isValidEmailAddress(email: String?): Boolean {
        val ePattern =
            "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,3}))$"
        val p = Pattern.compile(ePattern)
        val m = p.matcher(email)
        return m.matches()
    }

    private fun validateEmailAddress(){
        val email = binding.etRecipientEmail.text.toString().trim()
        if(email.length > 0) {
            if (isValidEmailAddress(email)) {
               // addNewChip(email, binding.recipientGroupFL)
                binding.recipientEmailError.visibility = View.GONE
            } else {
                binding.recipientEmailError.visibility = View.VISIBLE
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSentData(event: SendDataEvent){
        binding.loaderDocRequest.visibility = View.GONE
        if(event.addUpdateDataResponse.code == AppConstant.RESPONSE_CODE_SUCCESS){
            requireActivity().finish()
        }
        else if(event.addUpdateDataResponse.code == AppConstant.INTERNET_ERR_CODE)
            SandbarUtils.showError(requireActivity(), AppConstant.INTERNET_ERR_MSG)
        else
            if (event.addUpdateDataResponse.message != null)
                SandbarUtils.showError(requireActivity(), AppConstant.WEB_SERVICE_ERR_MSG)


        requireActivity().finish()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveData(event: WebServiceErrorEvent){
        binding.loaderDocRequest.visibility = View.GONE

        /*else if(event.addUpdateDataResponse.code == AppConstant.INTERNET_ERR_CODE)
            SandbarUtils.showError(requireActivity(), AppConstant.INTERNET_ERR_MSG)
        else
            if (event.addUpdateDataResponse.message != null)
                SandbarUtils.showError(requireActivity(), AppConstant.WEB_SERVICE_ERR_MSG) */


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


}
