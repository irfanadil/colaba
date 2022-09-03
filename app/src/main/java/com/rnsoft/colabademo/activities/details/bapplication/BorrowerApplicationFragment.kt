package com.rnsoft.colabademo

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.rnsoft.colabademo.activities.details.bapplication.RealEstateClickListener
import com.rnsoft.colabademo.databinding.DetailApplicationTabBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


@AndroidEntryPoint
class BorrowerApplicationFragment : BaseFragment(), AdapterClickListener, GovernmentQuestionClickListener,RealEstateClickListener {

    private var _binding: DetailApplicationTabBinding? = null
    private val binding get() = _binding!!
    private lateinit var horizontalRecyclerView: RecyclerView
    private lateinit var realStateRecyclerView: RecyclerView
    private lateinit var questionsRecyclerView: RecyclerView
    private lateinit var loanLayout : ConstraintLayout
    private lateinit var subjectPropertyLayout : ConstraintLayout
    private val detailViewModel: DetailViewModel by activityViewModels()
    private val borrowerApplicationViewModel: BorrowerApplicationViewModel by activityViewModels()
    private var borrowerInfoList: ArrayList<BorrowersInformation> = ArrayList()
    private var realStateList: ArrayList<RealStateOwn> = ArrayList()
    private var questionList: ArrayList<BorrowerQuestionsModel> = ArrayList()
    private var borrowerInfoAdapter = CustomBorrowerAdapter(borrowerInfoList , this)
    private var realStateAdapter  = RealStateAdapter(realStateList,this)
    private var questionAdapter  = QuestionAdapter(questionList, this, null)
    var saveBorrowerId:Int = 0
    var borrowerName : String? = null
    var streetName : String? = null
    var propertyType : String? = null
    var occupancyType : String? = null

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DetailApplicationTabBinding.inflate(inflater, container, false)
        val root: View = binding.root
        (activity as DetailActivity).binding.requestDocFab.visibility = View.GONE
        horizontalRecyclerView = root.findViewById(R.id.horizontalRecycleView)
        realStateRecyclerView = root.findViewById(R.id.realStateHorizontalRecyclerView)
        questionsRecyclerView = root.findViewById(R.id.govtQuestionHorizontalRecyclerView)
        loanLayout = root.findViewById(R.id.loanInfoLayout)
        subjectPropertyLayout = root.findViewById(R.id.sub_property_data_layout)


        //applicationTopContainer = root.findViewById(R.id.application_top_container)

        binding.assetsConstraintLayout.setOnClickListener{
            var borrowerIndex = 0
            borrowerApplicationViewModel.resetAssetModelClass()
            navigateToAssetActivity()
        }

        binding.incomeConstraintLayout.setOnClickListener{
            borrowerApplicationViewModel.resetIncomeModelClass()
            navigateToIncomeActivity()
        }

        loanLayout.setOnClickListener {

            val detailActivity = (activity as? DetailActivity)
            detailActivity?.let {
                val borrowerLoanActivity = Intent(requireActivity(), BorrowerLoanActivity::class.java)
                it.loanApplicationId?.let { loanId ->
                    borrowerLoanActivity.putExtra(AppConstant.loanApplicationId, loanId)
                    //Log.e("Loan Id", ""+it.loanApplicationId)
                }
                it.borrowerLoanPurpose?.let{ loanPurpose->
                    borrowerLoanActivity.putExtra(AppConstant.loanPurpose, loanPurpose)
                    //Log.e("PurposeId", ""+loanPurpose)
                }
                startActivity(borrowerLoanActivity)
            }
        }

        subjectPropertyLayout.setOnClickListener {
            val detailActivity = (activity as? DetailActivity)
            detailActivity?.let {
                val intent = Intent(requireActivity(), SubjectPropertyActivity::class.java)
                //Log.e("loanAppID", ""+ it.loanApplicationId)
                //Log.e("purpose", ""+ it.)
                intent.putExtra(AppConstant.loanApplicationId, it.loanApplicationId)
                intent.putExtra(AppConstant.borrowerPurpose, it.borrowerLoanPurpose)
                startActivityForResult(intent,200)

            }
        }

        binding.btnAddSubProperty.setOnClickListener {
            val detailActivity = (activity as? DetailActivity)
            detailActivity?.let {
                val intent = Intent(requireActivity(), SubjectPropertyActivity::class.java)
                intent.putExtra(AppConstant.loanApplicationId, it.loanApplicationId)
                intent.putExtra(AppConstant.borrowerPurpose, it.borrowerLoanPurpose)
                startActivityForResult(intent,200)

            }
        }

        val linearLayoutManager = LinearLayoutManager(activity , LinearLayoutManager.HORIZONTAL, false)

        horizontalRecyclerView.apply {
            this.layoutManager =linearLayoutManager
            //this.setHasFixedSize(true)
            this.adapter = borrowerInfoAdapter
        }

        val realStateLayoutManager = LinearLayoutManager(activity , LinearLayoutManager.HORIZONTAL, false)

        realStateRecyclerView.apply {
            this.layoutManager =realStateLayoutManager
            //this.setHasFixedSize(true)
            this.adapter = realStateAdapter
        }
        realStateAdapter.notifyDataSetChanged()

        val questionLayoutManger = LinearLayoutManager(activity , LinearLayoutManager.HORIZONTAL, false)

        questionsRecyclerView.apply {
            this.layoutManager =questionLayoutManger
            //this.setHasFixedSize(true)
            this.adapter = questionAdapter
        }
        questionAdapter.notifyDataSetChanged()

        binding.applicationTabLayout.visibility = View.INVISIBLE

        horizontalRecyclerView.addOnItemTouchListener(object : OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                when (e.action) {
                    MotionEvent.ACTION_MOVE -> rv.parent.requestDisallowInterceptTouchEvent(true)
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })

        realStateRecyclerView.addOnItemTouchListener(object : OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                when (e.action) {
                    MotionEvent.ACTION_MOVE -> rv.parent.requestDisallowInterceptTouchEvent(true)
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })

        questionsRecyclerView.addOnItemTouchListener(object : OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                when (e.action) {
                    MotionEvent.ACTION_MOVE -> rv.parent.requestDisallowInterceptTouchEvent(true)
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })

        val detailActivity = (activity as? DetailActivity)
        detailActivity?.let {
            it.borrowerLoanPurpose?.let { loanPurpose ->
                binding.textviewLoanPurpose.text = loanPurpose
            }
        }

        observeTabData()


        binding.btnAddRealEstateOwned.setOnClickListener {
            val detailActivity = (activity as? DetailActivity)
            detailActivity?.let {

                val intent = Intent(requireActivity(), RealEstateActivity::class.java)
                intent.putExtra(AppConstant.loanApplicationId, it.loanApplicationId)
                intent.putExtra(AppConstant.borrowerId,saveBorrowerId)
                intent.putExtra(AppConstant.borrowerName, borrowerName)
                startActivity(intent)
            }
        }

        super.addListeners(binding.root)
        return root

    }

    fun Fragment.getNavigationResult(key: String = "result") =
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(key)


    private fun navigateToAssetActivity(){
        val detailActivity = (activity as? DetailActivity)
        detailActivity?.let {
            val assetsActivity = Intent(requireActivity(), AssetsActivity::class.java)
            val bList:ArrayList<Int> = arrayListOf()
            for(item in borrowerInfoList) {
                //Timber.d("borrowerInfoList borrowerId  "+ item)
                if(item.borrowerId!=-1)
                    bList.add(item.borrowerId)
            }
            assetsActivity.putIntegerArrayListExtra( AppConstant.assetBorrowerList, bList)
            it.loanApplicationId?.let { loanId ->
                assetsActivity.putExtra(AppConstant.loanApplicationId, loanId)
            }
            it.borrowerLoanPurpose?.let{ loanPurpose->
                assetsActivity.putExtra(AppConstant.loanPurpose, loanPurpose)
            }
            startActivity(assetsActivity)
        }
    }

    private fun navigateToIncomeActivity(){
        val detailActivity = (activity as? DetailActivity)
        detailActivity?.let {
            val incomeActivity = Intent(requireActivity(), IncomeActivity::class.java)
            val bList:ArrayList<Int> = arrayListOf()
            for(item in borrowerInfoList) {
                //Timber.d("borrowerInfoList borrowerId  "+ item)
                if(item.borrowerId!=-1)
                    bList.add(item.borrowerId)
            }
            incomeActivity.putIntegerArrayListExtra( AppConstant.incomeBorrowerList, bList)
            it.loanApplicationId?.let { loanId ->
                incomeActivity.putExtra(AppConstant.loanApplicationId, loanId)
            }
            it.borrowerLoanPurpose?.let{ loanPurpose->
                incomeActivity.putExtra(AppConstant.loanPurpose, loanPurpose)
            }
            startActivity(incomeActivity)
        }
    }

    override fun getSingleItemIndex(position: Int){}

    override fun navigateTo(position: Int){
        val detailActivity = (activity as? DetailActivity)
        detailActivity?.let {
            val intent = Intent(requireActivity(), BorrowerAddressActivity::class.java)

            intent.putExtra(AppConstant.loanApplicationId, it.loanApplicationId)
            intent.putExtra(AppConstant.borrowerId,borrowerInfoList.get(position).borrowerId)
            intent.putExtra(AppConstant.borrowerName, borrowerName)
            intent.putExtra(AppConstant.addBorrower,borrowerInfoList.get(position).isFooter)
            intent.putParcelableArrayListExtra(AppConstant.coborrowers,borrowerInfoList)
            intent.putExtra(AppConstant.owntypeid, borrowerInfoList.get(position).owntypeId)
            intent.putExtra(AppConstant.firstName, borrowerInfoList.get(position).firstName)
            intent.putExtra(AppConstant.lastName, borrowerInfoList.get(position).lastName)
            intent.putExtra(AppConstant.middleName, borrowerInfoList.get(position).middleName)
            //Log.e("Application-Frag",""+borrowerInfoList.get(position).isFooter)
            startActivity(intent)
        }
    }

    override fun onRealEstateClick(position: Int){

        val detailActivity = (activity as? DetailActivity)
        detailActivity?.let {
            if(realStateList.get(position).isFooter){
                val intent = Intent(requireActivity(), RealEstateActivity::class.java)
                intent.putExtra(AppConstant.loanApplicationId, it.loanApplicationId)
                intent.putExtra(AppConstant.borrowerId, realStateList.get(position).borrowerId)
                intent.putExtra(AppConstant.borrowerName, borrowerName)
                startActivity(intent)
            } else {
                val intent = Intent(requireActivity(), RealEstateActivity::class.java)
                intent.putExtra(AppConstant.loanApplicationId, it.loanApplicationId)
                intent.putExtra(AppConstant.borrowerId, realStateList.get(position).borrowerId)
                intent.putExtra(AppConstant.propertyInfoId, realStateList.get(position).propertyInfoId)
                intent.putExtra(AppConstant.borrowerPropertyId, realStateList.get(position).borrowerPropertyId)
                intent.putExtra(AppConstant.borrowerName, borrowerName)
                startActivity(intent)
            }
        }
    }

    override fun navigateToGovernmentActivity(position: Int){
        val detailActivity = (activity as? DetailActivity)
        detailActivity?.let {
            val govtQuestionActivity = Intent(requireActivity(), GovtQuestionActivity::class.java)
            val bList:ArrayList<Int> = arrayListOf()
            val bListOwnTypeId:ArrayList<Int> = arrayListOf()
            for(item in borrowerInfoList) {
                //Timber.d("borrowerInfoList borrowerId  "+ item)
                if(item.borrowerId!=-1) {
                    //Timber.e("passing borrowerId -- "+item.borrowerId)
                   // Timber.e("passing owntypeId -- "+item.owntypeId)
                    bList.add(item.borrowerId)
                    bListOwnTypeId.add(item.owntypeId)
                }
            }
            govtQuestionActivity.putIntegerArrayListExtra( AppConstant.borrowerList, bList)
            govtQuestionActivity.putIntegerArrayListExtra( AppConstant.borrowerOwnTypeList, bListOwnTypeId)
            it.loanApplicationId?.let { loanId ->
                govtQuestionActivity.putExtra(AppConstant.loanApplicationId, loanId)
                //Timber.e("loanApplicationId -- "+loanId)
            }
            val questionSelection = questionList[position]
            govtQuestionActivity.putExtra( AppConstant.selectedQuestionHeader, questionSelection.questionDetail?.questionHeader)



            startActivity(govtQuestionActivity)
        }
    }

    private fun observeTabData(){
        detailViewModel.borrowerApplicationTabModel.observe(viewLifecycleOwner, { appTabModel ->
            if (appTabModel != null) {
                binding.applicationTopContainer.visibility = View.VISIBLE
                binding.applicationTabLayout.visibility = View.VISIBLE

                appTabModel.borrowerAppData?.subjectProperty?.subjectPropertyAddress?.let {
                    displaySubjectPropertyAddress(it)
                }

                appTabModel.borrowerAppData?.subjectProperty?.propertyTypeName?.let {
                    if(it !="null" && it.isNotEmpty()) {
                        binding.bAppPropertyType.text = it
                        propertyType = it
                    }
                }

                appTabModel.borrowerAppData?.subjectProperty?.propertyUsageDescription?.let {
                    if(it !="null" && it.isNotEmpty()){
                        binding.bAppPropertyUsage.text = it
                        occupancyType = it
                    }
                }

                if(streetName == null && occupancyType == null && propertyType == null){
                    binding.btnAddSubProperty.visibility = View.VISIBLE
                    subjectPropertyLayout.visibility = View.GONE
                } else {
                    binding.btnAddSubProperty.visibility = View.GONE
                    subjectPropertyLayout.visibility = View.VISIBLE
                }


                    appTabModel.borrowerAppData?.loanInformation?.loanAmount?.let {
                        binding.bAppLoanPayment.text =
                            "$".plus(AppSetting.returnAmountFormattedString(it))
                    }

                    appTabModel.borrowerAppData?.loanInformation?.downPayment?.let {
                        binding.bAppDownPayment.text =
                            "$".plus(AppSetting.returnAmountFormattedString(it))
                    }

                    appTabModel.borrowerAppData?.loanInformation?.downPaymentPercent?.let {
                        binding.bAppPercentage.text = "(" + it.roundToInt() + "%)"
                    }

                    appTabModel.borrowerAppData?.assetAndIncome?.totalAsset?.let {
                        binding.bAppTotalAssets.text =
                            "$".plus(AppSetting.returnAmountFormattedString(it))
                    }

                    appTabModel.borrowerAppData?.assetAndIncome?.totalMonthyIncome?.let {
                        binding.bAppMonthlylncome.text =
                            "$".plus(AppSetting.returnAmountFormattedString(it))
                    }

                    var races: ArrayList<Race>? = null
                    var ethnicities: ArrayList<Ethnicity>? = null

                    appTabModel.borrowerAppData?.let { bAppData ->
                        bAppData.borrowersInformation?.let { borrowersList ->
                            borrowerInfoList.clear()
                            borrowerInfoList = borrowersList
                            saveBorrowerId = borrowersList[0].borrowerId
                            borrowerName = borrowersList[0].firstName.plus(" ")
                                .plus(borrowersList[0].lastName)


                            for (borrower in borrowersList) {
                                Timber.e("BApp -- " + borrower.firstName,
                                    borrower.borrowerId,
                                    borrower.owntypeId,
                                    borrower.genderName
                                )
                                if (borrower.owntypeId == 1) {
                                    borrower.races?.let {
                                        races = it
                                    }
                                    borrower.ethnicities?.let {
                                        ethnicities = it
                                    }
                                }
                            }
                        }
                    }

                    appTabModel.borrowerAppData?.let { bAppData ->
                        bAppData.realStateOwns?.let {
                            for (item in it) {
                                //Timber.e(" Get -- " + item)
                                //Timber.e(" details -- " + item.borrowerId + "  " + item.propertyInfoId + "  " + item.propertyTypeId + "  " + item.propertyTypeName)

                            }
                            realStateList.clear()
                            realStateList = it
                            //Log.e("list1", "" + it)

                        }
                    }

                    appTabModel.borrowerAppData?.let { bAppData ->
                        bAppData.borrowerQuestionsModel?.let {
                            questionList.clear()
                            questionList = it
                        }
                    }


                    //////////////////////////////////////////////////////////////////////////////////////////////////
                    // add add-more last cell to the adapters
                    borrowerInfoList.add(
                        BorrowersInformation(-1, "", 0, "", "", "", "",0, null, null, true)
                    )
                    borrowerInfoAdapter =
                        CustomBorrowerAdapter(borrowerInfoList, this@BorrowerApplicationFragment)
                    horizontalRecyclerView.adapter = borrowerInfoAdapter
                    //Log.e("Application Frag",""+ borrowerInfoList)

                    borrowerInfoAdapter.notifyDataSetChanged()


                    if(realStateList.size > 0){
                        realStateList.add(RealStateOwn(null, saveBorrowerId, 0, 0, "", true, 0))
                        realStateAdapter =
                            RealStateAdapter(realStateList, this@BorrowerApplicationFragment)
                        realStateRecyclerView.adapter = realStateAdapter
                        realStateAdapter.notifyDataSetChanged()
                        binding.realStateHorizontalRecyclerView.visibility = View.VISIBLE
                        binding.btnAddRealEstateOwned.visibility = View.GONE
                    } else {
                        binding.realStateHorizontalRecyclerView.visibility = View.GONE
                        binding.btnAddRealEstateOwned.visibility = View.VISIBLE
                    }

                    var counter = 0
                    var noAnswerArrayList:ArrayList<Int> = arrayListOf()
                    for(item in questionList){
                        if(item.questionResponses?.size == 0)
                            noAnswerArrayList.add(questionList.indexOf(item))
                    }

                    for(item in noAnswerArrayList){
                        Collections.swap(questionList, item, counter++)
                    }

                    //Timber.e(" print arraylist = $questionList")

                    questionList.add(BorrowerQuestionsModel(QuestionDetail(AppConstant.demographicInformation, 500, ""), null, true, races, ethnicities, ))

                    appTabModel.borrowerAppData?.let { bAppData ->
                        bAppData.borrowersInformation?.let { eachBorrowerRaceEthnicity ->
                            questionAdapter =
                                QuestionAdapter(questionList, this@BorrowerApplicationFragment , eachBorrowerRaceEthnicity)
                                questionsRecyclerView.adapter = questionAdapter
                                questionAdapter.notifyDataSetChanged()
                        }
                    }

                } else
                    binding.applicationTabLayout.visibility = View.INVISIBLE
            })

    }

    private fun displaySubjectPropertyAddress(address : SubjectPropertyAddress){
        address.let {
            val builder = StringBuilder()
            it.street?.let {
                if (it != "null" && it.isNotEmpty()) {
                    streetName = it
                    builder.append(it).append(" ")
                }
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
            binding.bAppAddress.text = builder
            // binding.bAppAddress.text = it.street+" "+it.unit+"\n"+it.city+" "+it.stateName+" "+it.zipCode+" "+it.countryName

        }

    }

    override fun onResume(){
        super.onResume()
        (activity as DetailActivity).binding.requestDocFab.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == 200) {
            Log.d("TAG", "${data.toString()}")
        }
    }

}


