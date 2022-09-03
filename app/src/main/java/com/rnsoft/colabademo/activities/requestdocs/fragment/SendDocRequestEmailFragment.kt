package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import javax.inject.Inject
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import com.google.android.material.chip.ChipGroup
import java.util.regex.Pattern
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.rnsoft.colabademo.databinding.SendDocRequestLayoutBinding
import com.rnsoft.colabademo.utils.CustomMaterialFields
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * Created by Anita Kiran on 10/4/2021.
 */

@AndroidEntryPoint
class SendDocRequestEmailFragment : DocsTypesBaseFragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private val viewModel: RequestDocsViewModel by activityViewModels()
    private lateinit var binding : SendDocRequestLayoutBinding
    private var savedViewInstance: View? = null
    private var loanApplicationId: Int? = null
    var templateList: ArrayList<Template> = ArrayList()
    var htmlEmailBody : String? = null
    var renderEmailResponse: EmailTemplatesResponse? = null
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

    private fun addNewChip(email: String,editText: EditText, chipGroup : FlexboxLayout){
        val chip = LayoutInflater.from(context).inflate(R.layout.chip, chipGroup, false) as Chip
        //val chip = Chip(context)
        chip.text = email
       // chip.chipIcon = ContextCompat.getDrawable(requireContext(), R.mipmap.ic_launcher_round)
        chip.isCloseIconEnabled = true
        chip.isClickable = true
        chip.isCheckable = false
        chipGroup.addView(chip as View, chipGroup.childCount - 1)
        chip.setOnCloseIconClickListener{
            chipGroup.removeView(chip as View)
        }
        editText.setText("")
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

    private fun sendDocRequest() {
        var isDataEntered  = true
        var emailCount = binding.recipientGroupFL.childCount
        if (emailCount == 1) {
            isDataEntered = false
            binding.recipientEmailError.visibility = View.VISIBLE
        } else {
            binding.recipientEmailError.visibility = View.GONE
            isDataEntered = true
        }

        if (isDataEntered){
            try {
                // get to email
                var recipientEmail: String? = null
                var ccEmail: String? = null
                var fromEmail: String? = null
                for (i in 0 until binding.recipientGroupFL.getChildCount()) {
                    if (binding.recipientGroupFL.getChildAt(i) is Chip) {
                        val chip = binding.recipientGroupFL.getChildAt(i) as Chip
                        recipientEmail = chip.text.toString()
                    }
                }
                // cc
                for (i in 0 until binding.ccFL.getChildCount()) {
                    if (binding.ccFL.getChildAt(i) is Chip) {
                        val chip = binding.ccFL.getChildAt(i) as Chip
                        ccEmail = chip.text.toString()
                    }
                }
                // subject
                var subject = binding.etSubjectLine.text.toString().trim()

                // email item
                val matchedList = templateList.filter { p ->
                    p.templateName.equals(
                        binding.tvEmailType.getText().toString().trim(), true
                    )
                }
                val emailTemplateId =
                    if (matchedList.size > 0) matchedList.map { matchedList.get(0).templateId }
                        .single() else null

                renderEmailResponse?.fromAddress?.let {
                    fromEmail = it
                }

                val emailBody = Email(
                    toAddress = recipientEmail!!,
                    ccAddress = ccEmail,
                    emailTemplateId = emailTemplateId,
                    subject = subject,
                    emailBody = htmlEmailBody,
                    fromAddress = fromEmail)

                lifecycleScope.launchWhenStarted {
                    sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                        binding.loaderDocRequest.visibility = View.VISIBLE
                        if (loanApplicationId != null) {
                            var docList: ArrayList<RequestDocument> = ArrayList()
                            if (combineDocList.size > 0) {
                                for (i in 0 until combineDocList.size) {
                                   docList.add(
                                       RequestDocument(docTypeId = combineDocList.get(i).docTypeId, docType = combineDocList.get(i).docType, docMessage = combineDocList.get(i).docMessage))
                                }
                            }

                            val requestList: ArrayList<DocRequestDataList> = ArrayList()
                            requestList.add(DocRequestDataList(email = emailBody, documents = docList))
                            val sendRequestBody = SendDocRequestModel(loanApplicationId = loanApplicationId!!, requestList)
                            //Log.e("sendBody", "$sendRequestBody")

                            viewModel.sendDocRequest(authToken, sendRequestBody)
                        }
                    }
                }
            } catch (e:Exception){}
        } // data entered
    }

    private fun getDropDownTemplate(){
        lifecycleScope.launchWhenStarted {
            sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                val call = async {
                    binding.loaderDocRequest.visibility = View.VISIBLE
                    viewModel.getEmailTemplates(authToken)
                }
                call.await()
                setDropDownTemplate()
            }
        }
    }

    private fun setDropDownTemplate(){
        viewModel.emailTemplates.observe(viewLifecycleOwner, { data ->
            if (data != null && data.size > 0){

                for(item in data){
                    templateList.add(
                        Template( id= item.id,templateId = item.id, templateName = item.templateName, docDesc = item.templateDescription))
                }
                val spinnerAdapter = EmailTemplateSpinnerAdapter(requireContext(), R.layout.email_template_item, templateList)
                binding.tvEmailType.setAdapter(spinnerAdapter)
                binding.tvEmailType.setOnClickListener {
                    binding.tvEmailType.showDropDown()
                }
                //binding.tvEmailType.setBackground(getDrawable(requireContext(), R.drawable.content_bg_with_drop_shadow))
                //binding.tvEmailType.setDropDownBackgroundDrawable(getDrawable(requireContext(), R.drawable.content_bg_with_drop_shadow))

                //binding.tvEmailType.setDropDownBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.content_bg_with_drop_shadow))

                // set initial template
                getEmailBody(0)

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
        try {
            lifecycleScope.launchWhenStarted {
                sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                    binding.loaderDocRequest.visibility = View.VISIBLE
                    binding.tvEmailBody.setText("")
                    if(loanApplicationId != null) {
                        val call = async {
                            viewModel.getEmailTemplateBody(authToken, loanApplicationId!!, templateList.get(position).templateId)
                        }
                        call.await()
                        setEmailBody()
                    }
                }
            }
        } catch (e:java.lang.Exception){}
    }

    private fun setEmailBody(){
        viewModel.emailTemplateBody.observe(viewLifecycleOwner, { body ->
            binding.loaderDocRequest.visibility = View.GONE
            if(body != null){
               renderEmailResponse = body
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

                        htmlEmailBody = it.replace(AppConstant.relaceDocFormat, builder.toString())
                        binding.tvEmailBody.text = Html.fromHtml(htmlEmailBody)
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
                    for (item in 0 until templateList.size){
                        if (templateList.get(item).id == it) {
                            selectedItem = item
                            binding.tvEmailType.setText(templateList.get(item).templateName, false)
                            CustomMaterialFields.setColor(binding.layoutEmailTemplate, R.color.grey_color_two, requireActivity())
                            break
                        }
                    }
                }

                body.toAddress?.let{
                    if(it.isNotEmpty() && it != "null"){

                        for (i in 0 until binding.recipientGroupFL.getChildCount()) { // remove previous chips if any
                            if (binding.recipientGroupFL.getChildAt(i) is Chip) {
                                val chip = binding.recipientGroupFL.getChildAt(i) as Chip
                                binding.recipientGroupFL.removeView(chip as View)
                            }
                        }

                        addNewChip(it,binding.etRecipientEmail,binding.recipientGroupFL)
                        binding.recipientLabelTo.setTextColor(AppCompatResources.getColorStateList(requireActivity(), R.color.grey_color_two))
                    }
                }

                body.ccAddress?.let {
                    if(it.isNotEmpty() && it != "null"){

                        for (i in 0 until binding.ccFL.getChildCount()) {
                            if (binding.ccFL.getChildAt(i) is Chip) {
                                val chip = binding.ccFL.getChildAt(i) as Chip
                                binding.ccFL.removeView(chip as View)
                            }
                        }
                        addNewChip(it,binding.etccEmail,binding.ccFL)
                        binding.ccLable.setTextColor(AppCompatResources.getColorStateList(requireActivity(), R.color.grey_color_two))
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
                validateEmailAddress(binding.etRecipientEmail,binding.recipientGroupFL, binding.recipientEmailError)
                true
            }
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
                        validateEmailAddress(binding.etRecipientEmail,binding.recipientGroupFL,binding.recipientEmailError)
                        // Toast.makeText(context, "space bar pressed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })

        binding.etRecipientEmail.setOnKeyListener { _, keyCode, keyEvent ->
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if(binding.recipientGroupFL.childCount > 1) {

                        if(binding.etRecipientEmail.text.toString().trim().length > 0){
                            // do nothing
                        } else {
                            val childOne =
                                binding.recipientGroupFL.getChildAt(binding.recipientGroupFL.childCount - 1)
                            // val childTwo = binding.recipientGroupFL.getChildAt(binding.recipientGroupFL.childCount - 2)
                            var index = 0
                            index = if (childOne is Chip)
                                binding.recipientGroupFL.childCount - 1
                            else
                                binding.recipientGroupFL.childCount - 2

                            val lastChip = binding.recipientGroupFL.getChildAt(index) as Chip
                            binding.etRecipientEmail.setText(" "+ lastChip.text.toString())
                            binding.etRecipientEmail.setSelection(binding.etRecipientEmail.length())
                            binding.recipientGroupFL.removeView(lastChip as View)
                        }
                    }
                }
                return@setOnKeyListener false
           }

        binding.etRecipientEmail.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus){
                validateEmailAddress(binding.etRecipientEmail,binding.recipientGroupFL, binding.recipientEmailError)
            }
        }

        // cc email
        binding.etccEmail.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if(event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE || event.keyCode == KeyEvent.KEYCODE_SPACE
                || actionId == KeyEvent.KEYCODE_SPACE){
                validateEmailAddress(binding.etccEmail,binding.ccFL,binding.ccEmailError)
                true
            }
            else {
                false
            }
        })

        binding.etccEmail.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if(binding.etccEmail.text.toString().trim().length > 0) {
                    val lastChar: String = s.toString().substring(s.length - 1)
                    if (lastChar == " ") {
                        validateEmailAddress(binding.etccEmail ,binding.ccFL,binding.ccEmailError)
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })

        binding.etccEmail.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if(binding.ccFL.childCount > 1) {

                    if(binding.etccEmail.text.toString().trim().length > 0) {
                        // if there is text don't delete the chip, remove the text
                    } else {
                        val childOne =
                            binding.ccFL.getChildAt(binding.ccFL.childCount - 1)
                        // val childTwo = binding.recipientGroupFL.getChildAt(binding.recipientGroupFL.childCount - 2)
                        var index = 0
                        index = if (childOne is Chip)
                            binding.ccFL.childCount - 1
                        else
                            binding.ccFL.childCount - 2

                        val lastChip = binding.ccFL.getChildAt(index) as Chip
                        binding.etccEmail.setText(" "+ lastChip.text.toString())
                        binding.etccEmail.setSelection(binding.etccEmail.length())
                        binding.ccFL.removeView(lastChip as View)
                    }
                }
            }
            return@setOnKeyListener false
        }

        binding.etccEmail.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus){
                validateEmailAddress(binding.etccEmail,binding.ccFL,binding.ccEmailError)
            }
        }


    }

    private fun isValidEmailAddress(email: String?): Boolean {
        val ePattern =
            "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,3}))$"
        val p = Pattern.compile(ePattern)
        val m = p.matcher(email)
        return m.matches()
    }

    private fun validateEmailAddress(editText: EditText, layout: FlexboxLayout, errorTextView : TextView){
        var email = editText.text.toString().trim()
        if(email.length > 0) {
            if (isValidEmailAddress(email)) {
                addNewChip(email,editText,layout)
                errorTextView.visibility = View.GONE
            } else {
                errorTextView.visibility = View.VISIBLE
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEmailRequestSent(event: SendDocRequestEvent){
        binding.loaderDocRequest.visibility = View.GONE
        if(event.response.responseCode == 200){
            findNavController().navigate(R.id.navigation_request_sent)
        }
        else {
            SandbarUtils.showError(requireActivity(), AppConstant.WEB_SERVICE_ERR_MSG)
            findNavController().popBackStack()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveData(event: WebServiceErrorEvent){
        binding.loaderDocRequest.visibility = View.GONE

//         if(event.addUpdateDataResponse.code == AppConstant.INTERNET_ERR_CODE)
//            SandbarUtils.showError(requireActivity(), AppConstant.INTERNET_ERR_MSG)
//        else
//            if (event.addUpdateDataResponse.message != null)
//                SandbarUtils.showError(requireActivity(), AppConstant.WEB_SERVICE_ERR_MSG)


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
