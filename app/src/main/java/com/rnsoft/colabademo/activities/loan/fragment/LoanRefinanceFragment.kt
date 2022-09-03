package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.addCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import com.rnsoft.colabademo.databinding.AppHeaderWithBackNavBinding
import com.rnsoft.colabademo.databinding.LoanRefinanceInfoBinding
import com.rnsoft.colabademo.utils.CustomMaterialFields
import com.rnsoft.colabademo.utils.NumberTextFormat
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject

/**
 * Created by Anita Kiran on 9/6/2021.
 */
@AndroidEntryPoint
class LoanRefinanceFragment : BaseFragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private var loanApplicationId: Int? = null
    private val loanViewModel : LoanInfoViewModel by activityViewModels()
    private lateinit var binding: LoanRefinanceInfoBinding
    private lateinit var bindingToolbar: AppHeaderWithBackNavBinding
    private var goalFullList: ArrayList<LoanGoalModel> = arrayListOf()
    var downPayment : Double?= null
    var propertyValue : Double ?= null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = LoanRefinanceInfoBinding.inflate(inflater, container, false)
        bindingToolbar = binding.headerLoanPurchase
        // set Header title
        bindingToolbar.headerTitle.setText(getString(R.string.loan_info_refinance))


        val activity = activity as BorrowerLoanActivity
        activity.let{
            loanApplicationId = it.loanApplicationId
        }

        initViews()
        clicks()
        setLoanStages()

        super.addListeners(binding.root)
        return binding.root

    }

    private fun initViews(){

        // add prefix
        CustomMaterialFields.setDollarPrefix(binding.layoutLoanAmount,requireContext())
        CustomMaterialFields.setDollarPrefix(binding.layoutCashout,requireContext())

        // number formats
        binding.edCashoutAmount.addTextChangedListener(NumberTextFormat(binding.edCashoutAmount))
        binding.edLoanAmount.addTextChangedListener(NumberTextFormat(binding.edLoanAmount))

        // lable focus
        binding.edCashoutAmount.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                CustomMaterialFields.setColor(binding.layoutCashout, R.color.grey_color_two, requireContext())
            } else {
                if (binding.edCashoutAmount.text?.length == 0) {
                    CustomMaterialFields.setColor(binding.layoutCashout, R.color.grey_color_three, requireContext())
                } else {
                    clearError(binding.layoutCashout)
                    CustomMaterialFields.setColor(binding.layoutCashout, R.color.grey_color_two, requireContext())
                }
            }
        }

        binding.edLoanAmount.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                CustomMaterialFields.setColor(binding.layoutLoanAmount, R.color.grey_color_two, requireContext())
            } else {
                if (binding.edLoanAmount.text?.length == 0) {
                    CustomMaterialFields.setColor(binding.layoutLoanAmount, R.color.grey_color_three, requireContext())
                } else {
                    clearError(binding.layoutLoanAmount)
                    CustomMaterialFields.setColor(binding.layoutLoanAmount, R.color.grey_color_two, requireContext())
                }
            }
        }
    }

    private fun setLoanInfoDetail() {
        loanViewModel.loanInfoPurchase.observe(viewLifecycleOwner, { loanInfo ->

            if (loanInfo != null) {
                loanInfo.data?.downPayment?.let {
                    downPayment = it
                }
                loanInfo.data?.propertyValue?.let {
                    propertyValue = it
                }

                loanInfo.data?.loanGoalName?.let {
                    //binding.tvLoanStage.setText(it,false)
                    CustomMaterialFields.setColor(binding.layoutLoanStage,R.color.grey_color_two,requireActivity())
                }

                loanInfo.data?.cashOutAmount?.let {
                    binding.edCashoutAmount.setText(Math.round(it).toString())
                    CustomMaterialFields.setColor(binding.layoutCashout,R.color.grey_color_two,requireActivity())
                }
                    loanInfo.data?.loanPayment?.let {
                        binding.edLoanAmount.setText(Math.round(it).toString())
                        CustomMaterialFields.setColor(binding.layoutLoanAmount,R.color.grey_color_two,requireActivity())
                    }

                loanInfo.data?.loanGoalId?.let { goalId->
                    for(item in goalFullList) {
                        if (item.id == goalId) {
                            binding.tvLoanStage.setText(item.description, false)
                            CustomMaterialFields.setColor(binding.layoutLoanStage, R.color.grey_color_two, requireActivity())
                            break
                        }
                    }
                }
               hideLoader()
            }

            })
    }

    private fun setLoanStages() {
        lifecycleScope.launchWhenStarted {
             loanViewModel.loanGoals.observe(viewLifecycleOwner, { goals ->
                 if (goals != null && goals.size > 0) {
                     val itemList: ArrayList<String> = arrayListOf()
                     goalFullList = arrayListOf()
                     for (item in goals) {
                         itemList.add(item.description)
                            goalFullList.add(item)
                     }

                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, itemList)
                        binding.tvLoanStage.setAdapter(adapter)
                        //adapter.setNotifyOnChange(true)

                    binding.tvLoanStage.setOnFocusChangeListener { _, _ ->
                        binding.tvLoanStage.showDropDown()
                    }
                        binding.tvLoanStage.setOnClickListener {
                            binding.tvLoanStage.showDropDown()
                        }

                        binding.tvLoanStage.onItemClickListener = object :
                            AdapterView.OnItemClickListener {
                            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                                CustomMaterialFields.setColor(binding.layoutLoanStage, R.color.grey_color_two, requireActivity())
                            }
                        }
                    }
                 hideLoader()
                 setLoanInfoDetail()
                })

         }

    }

    private fun processData(){

        val loanStage: String = binding.tvLoanStage.text.toString().trim()
        val cashOutAmount: String = binding.edCashoutAmount.text.toString().trim()
        val loanAmount: String = binding.edLoanAmount.text.toString().trim()

        if (loanStage.isEmpty() || loanStage.length == 0) {
            setError(binding.layoutLoanStage, getString(R.string.error_field_required))
        }
        if (cashOutAmount.isEmpty() || cashOutAmount.length == 0) {
            setError(binding.layoutCashout, getString(R.string.error_field_required))
        }
        if (loanAmount.isEmpty() || loanAmount.length == 0) {
            setError(binding.layoutLoanAmount, getString(R.string.error_field_required))
        }
        // clear error
        if (loanStage.isNotEmpty() || loanStage.length > 0) {
            clearError(binding.layoutLoanStage)
        }
        if (cashOutAmount.isNotEmpty() || cashOutAmount.length > 0) {
            clearError(binding.layoutCashout)
        }
        if (loanAmount.isNotEmpty() || loanAmount.length > 0) {
            clearError(binding.layoutLoanAmount)
        }
            if(loanStage.length > 0 && loanAmount.length >0 && cashOutAmount.length > 0){
                var newCashoutAmount = cashOutAmount.replace(",".toRegex(), "")
                var newLoanAmount = loanAmount.replace(",".toRegex(), "")

                val loanGoal : String = binding.tvLoanStage.getText().toString().trim()
                val matchedList =  goalFullList.filter { g -> g.description.equals(loanGoal,true)}
                val loanGoalId = if(matchedList.size > 0) matchedList.map { matchedList.get(0).id }.single() else null


                val activity = (activity as? BorrowerLoanActivity)
                activity?.loanApplicationId?.let { loanId ->
                    if (downPayment != null && propertyValue != null) {
                        val info = UpdateLoanRefinanceModel(
                            loanApplicationId = loanId,
                            loanPurposeId = AppConstant.PURPOSE_ID_REFINANCE,
                            loanGoalId = loanGoalId,
                            cashOutAmount = newCashoutAmount.toDouble(),
                            downPayment = 0.0,
                            propertyValue = propertyValue!!,
                            loanPayment = newLoanAmount.toDouble()
                        )

                        lifecycleScope.launchWhenStarted {
                           // sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                                binding.loaderLoanRefinance.visibility = View.VISIBLE
                                  //Log.e("LoanInfoApi",""+info)
                                  loanViewModel.addLoanRefinanceInfo(info)
                            //}
                        }
                    }
                }
                binding.loaderLoanRefinance.visibility = View.GONE
            }


    }

    private fun clicks(){
        binding.btnSaveChanges.setOnClickListener {
            processData()
        }

        bindingToolbar.backButton.setOnClickListener {
            requireActivity().finish()
            requireActivity().overridePendingTransition(R.anim.hold,R.anim.slide_out_left)
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            requireActivity().finish()
            requireActivity().overridePendingTransition(R.anim.hold, R.anim.slide_out_left)
        }
    }


    fun setError(textInputlayout: TextInputLayout, errorMsg: String) {
        textInputlayout.helperText = errorMsg
        textInputlayout.setBoxStrokeColorStateList(
            AppCompatResources.getColorStateList(requireContext(),R.color.primary_info_stroke_error_color))
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

    private fun hideLoader(){
        val  activity = (activity as? BorrowerLoanActivity)
        activity?.binding?.loaderLoanInfo?.visibility = View.GONE
    }


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
        EventBus.getDefault().unregister(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSentData(event: SendDataEvent) {
        binding.loaderLoanRefinance.visibility = View.GONE
        if(event.addUpdateDataResponse.code == AppConstant.RESPONSE_CODE_SUCCESS){
            EventBus.getDefault().postSticky(BorrowerApplicationUpdatedEvent(objectUpdated = true))
            requireActivity().finish()
        }
        else if(event.addUpdateDataResponse.code == AppConstant.INTERNET_ERR_CODE){
            SandbarUtils.showError(requireActivity(), AppConstant.INTERNET_ERR_MSG)
        } else {
            if (event.addUpdateDataResponse.message != null)
                SandbarUtils.showError(requireActivity(), AppConstant.WEB_SERVICE_ERR_MSG)
        }
    }


}