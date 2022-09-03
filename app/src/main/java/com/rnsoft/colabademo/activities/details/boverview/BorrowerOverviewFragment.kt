package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.databinding.DetailBorrowerLayoutTwoBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class BorrowerOverviewFragment : BaseFragment()  {
    private var _binding: DetailBorrowerLayoutTwoBinding? = null
    private val binding get() = _binding!!
    private val detailViewModel: DetailViewModel by activityViewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DetailBorrowerLayoutTwoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val detailActivity = (activity as? DetailActivity)
        detailActivity?.let {
            binding.loanPurposeParam.text = it.borrowerLoanPurpose
        }

        binding.box1.setOnClickListener{
            //findNavController().navigate(R.id.invitation_primary_borrower_fragment_id, null)
            findNavController().navigate(R.id.navigation_pre_approval_fragment, null)
        }

        binding.noAddressLayout.visibility = View.INVISIBLE
        binding.addressLayout.visibility = View.INVISIBLE

        detailViewModel.borrowerOverviewModel.observe(viewLifecycleOwner, {  overviewModel->
            if(overviewModel!=null) {

                binding.userLayout3.setOnClickListener {
                    findNavController().navigate(R.id.borrower_app_status_fragment)
                }

                binding.mainBorrowerName.text = ""
                val coBorrowers = overviewModel.coBorrowers
                var mainBorrowerName = ""

                if (coBorrowers != null) {
                    val coBorrowerNames:ArrayList<String> = ArrayList()
                    if(coBorrowers.size == 0)
                        binding.coBorrowerNames.visibility = View.GONE
                    else{
                        for (coBorrower in coBorrowers){
                            val coBorrowerName = coBorrower.firstName+" "+coBorrower.lastName
                            if(coBorrower.ownTypeId!=1)
                                coBorrowerNames.add(coBorrowerName)
                            else
                                mainBorrowerName = coBorrowerName
                        }
                        binding.coBorrowerNames.visibility = View.VISIBLE
                        binding.coBorrowerNames.text = coBorrowerNames.joinToString(separator = ", ")
                        if(binding.coBorrowerNames.text.toString().isBlank() || binding.coBorrowerNames.text.toString().isBlank())
                            binding.coBorrowerNames.visibility = View.GONE
                    }
                }
                else
                    binding.coBorrowerNames.visibility = View.GONE

                binding.mainBorrowerName.text = mainBorrowerName

                 if(overviewModel.loanNumber!=null && !overviewModel.loanNumber.equals("null", true)  && overviewModel.loanNumber.isNotEmpty()) {
                     binding.loanId.visibility = View.VISIBLE
                     binding.loanId.text = "Loan No. " + overviewModel.loanNumber
                 }
                else
                     binding.loanId.visibility = View.GONE

                //binding.loanPurpose.text = overviewModel.loanPurpose
                binding.borrowerPropertyType.text = overviewModel.propertyType
                //binding.loanGoalTextView.text = "- "+overviewModel.loanGoal
                binding.propertyUsageTextView.text = overviewModel.propertyUsage
                binding.borrowerAppStatus.text = overviewModel.milestone

                overviewModel.downPayment?.let {
                    val stringValue = AppSetting.returnAmountFormattedString(it)
                    binding.downPayment.text =  "$"+stringValue
                }

                overviewModel.loanAmount?.let {
                    val stringValue = AppSetting.returnAmountFormattedString(it)
                    binding.loanPayment.text = "$"+stringValue
                }

                overviewModel.loanPurpose?.let {
                    //val stringValue = AppSetting.returnAmountFormattedString(it)
                    binding.loanPurposeValue.text = "$"+it
                }


                var percentage = 0
                overviewModel.downPayment?.let{ downPayment ->
                    overviewModel.propertyValue?.let { propertyValue ->
                        if(propertyValue!=0.0 && downPayment!=0.0)
                            percentage  = ((downPayment / propertyValue) * 100).roundToInt()
                    }
                }

                if(percentage!=0)
                    binding.percentageTextView.text = "("+percentage.toString()+"%)"

                if(overviewModel.webBorrowerAddress==null)
                    binding.noAddressLayout.visibility = View.VISIBLE
                else {
                    binding.addressLayout.visibility = View.VISIBLE
                    overviewModel.webBorrowerAddress.let {
                        //binding.completeAddress.text = it.street+" "+it.unit+"\n"+it.city+" "+it.stateName+" "+it.zipCode+" "+it.countryName
                        val builder = StringBuilder()
                        it.street?.let {
                            if (it != "null")
                                builder.append(it).append(" ")
                        }
                        it.unit?.let {
                            if (it != "null")
                                builder.append(it).append(",")
                            else
                                builder.append(",")
                        } ?: run { builder.append(",") }
                        it.city?.let {
                            if (it != "null")
                                builder.append("\n").append(it).append(",").append(" ")
                        } ?: run { builder.append("\n") }
                        it.stateName?.let {
                            if (it != "null") builder.append(it).append(" ")
                        }
                        it.zipCode?.let {
                            if (it != "null")
                                builder.append(it)
                        }
                        binding.completeAddress.text = builder

                    }
                }

                if(overviewModel.postedOn!=null && !overviewModel.postedOn.equals("null", true)) {
                    binding.bytesPosted.text = "Posted to Byte on " + overviewModel.postedOn
                    binding.bytesPosted.visibility = View.VISIBLE
                    //binding.bytesTick.visibility = View.VISIBLE
                }
                else {
                    binding.bytesPosted.visibility = View.GONE
                   // binding.bytesTick.visibility = View.GONE
                }

                if(!binding.bytesPosted.isVisible && !binding.loanId.isVisible)
                    binding.view7.visibility = View.GONE
            }
            else
                Log.e("should-stop"," here....")

        })
        super.addListeners(binding.root)
        return root
    }

    override fun onResume() {
        super.onResume()
        (activity as DetailActivity).binding.requestDocFab.visibility = View.GONE
    }

}