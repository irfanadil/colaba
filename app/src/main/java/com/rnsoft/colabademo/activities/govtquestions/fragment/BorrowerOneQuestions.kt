package com.rnsoft.colabademo

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.databinding.*
import timber.log.Timber
import android.widget.LinearLayout
import android.util.DisplayMetrics
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.children_separate_layout.view.*
import kotlinx.android.synthetic.main.common_govt_content_layout.view.ans_no
import kotlinx.android.synthetic.main.common_govt_content_layout.view.ans_yes
import kotlinx.android.synthetic.main.common_govt_content_layout.view.detail_text
import kotlinx.android.synthetic.main.common_govt_content_layout.view.detail_title
import kotlinx.android.synthetic.main.common_govt_content_layout.view.govt_detail_box
import kotlinx.android.synthetic.main.common_govt_content_layout.view.govt_question
import com.google.gson.Gson
import kotlin.collections.HashMap
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import com.rnsoft.colabademo.utils.Common
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.children_separate_layout.view.detail_text2
import kotlinx.android.synthetic.main.children_separate_layout.view.detail_title2
import kotlinx.android.synthetic.main.children_separate_layout.view.govt_detail_box2
import kotlinx.android.synthetic.main.new_demo_graphic_show_layout.view.*
import kotlinx.android.synthetic.main.new_demo_graphic_show_layout.view.american_or_indian_check_box
import kotlinx.android.synthetic.main.new_demo_graphic_show_layout.view.asian_check_box
import kotlinx.android.synthetic.main.new_demo_graphic_show_layout.view.black_or_african_check_box
import kotlinx.android.synthetic.main.new_demo_graphic_show_layout.view.do_not_wish_check_box
import kotlinx.android.synthetic.main.new_demo_graphic_show_layout.view.hispanic_or_latino
import kotlinx.android.synthetic.main.new_demo_graphic_show_layout.view.native_hawaian_or_other_check_box
import kotlinx.android.synthetic.main.new_demo_graphic_show_layout.view.not_hispanic
import kotlinx.android.synthetic.main.new_demo_graphic_show_layout.view.not_telling_ethnicity
import kotlinx.android.synthetic.main.new_demo_graphic_show_layout.view.white_check_box
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject
import java.util.ArrayList


@AndroidEntryPoint
class BorrowerOneQuestions : GovtQuestionBaseFragment() {

    private lateinit var binding: BorrowerOneQuestionsLayoutBinding
    private var idToContentMapping = HashMap<Int, ConstraintLayout>(0)
    private var innerLayoutHashMap = HashMap<AppCompatTextView, ConstraintLayout>(0)
    private val borrowerAppViewModel: BorrowerApplicationViewModel by activityViewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private var ethnicityChildNames = ""
    private var otherEthnicity = ""

    private var nativeHawaiiChildNames = ""
    private var nativeHawaiiOtherRace = ""

    private var asianChildNames = ""
    private var otherAsianRace = ""

    private var demoGraphicScreenDisplaying: Boolean = false
    private var ethnicityChildList: ArrayList<EthnicityDetailDemoGraphic> = arrayListOf()
    private var asianChildList: ArrayList<DemoGraphicRaceDetail> = arrayListOf()
    private var nativeHawaiianChildList: ArrayList<DemoGraphicRaceDetail> = arrayListOf()
    private lateinit var variableDemoGraphicData: DemoGraphicData
    private lateinit var variableRaceList: ArrayList<DemoGraphicRace>
    private lateinit var variableEthnicityList: ArrayList<EthnicityDemoGraphic>
    private var variableGender: Int? = null

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////s
    private var tabBorrowerId: Int? = null
    private var childSupportAnswerDataList: ArrayList<ChildAnswerData> = arrayListOf()
    private var bankruptcyAnswerData: BankruptcyAnswerData = BankruptcyAnswerData()
    private var ownerShipInnerScreenParams: ArrayList<String> = arrayListOf()

    private var governmentParams = GovernmentParams()
    private var saveGovtQuestionForDetailAnswer: ArrayList<QuestionData>? = null
    private lateinit var lastQData: QuestionData


    private var ownerShipConstraintLayout: ConstraintLayout?=null
    private lateinit var childConstraintLayout: ConstraintLayout
    private lateinit var undisclosedLayout: ConstraintLayout
    private lateinit var bankruptcyConstraintLayout: ConstraintLayout
    private lateinit var demoGraphLayout: ConstraintLayout
    private val childGovtBoxes: ArrayList<ConstraintLayout> = arrayListOf()
    //private lateinit var variableQuestionData: QuestionData


    private lateinit var clickedContentCell: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BorrowerOneQuestionsLayoutBinding.inflate(inflater, container, false)
        arguments?.let {
            tabBorrowerId = it.getInt(AppConstant.tabBorrowerId)
        }
        binding.saveBtn.setOnClickListener {
            if (demoGraphicScreenDisplaying)
                updateDemoGraphicApiCall()
            else
                updateGovernmentQuestionApiCall()
            EventBus.getDefault().postSticky(BorrowerApplicationUpdatedEvent(true))
            requireActivity().finish()
        }

        setUpDynamicTabs()
        super.addListeners(binding.root)
        return binding.root
    }

    private fun updateDemoGraphicApiCall() {
        lifecycleScope.launchWhenStarted {
            sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                variableDemoGraphicData.genderId = variableGender
                variableDemoGraphicData.race = variableRaceList
                variableDemoGraphicData.ethnicity = variableEthnicityList
                borrowerAppViewModel.addOrUpdateDemoGraphic(authToken, variableDemoGraphicData)
            }
        }
    }

    private fun showHideChildGovtBoxes(makeVisible: Int) {
        for (item in childGovtBoxes) {
            item.visibility = View.INVISIBLE
        }
        for (i in 0 until childSupportAnswerDataList.size) {
            val item = childGovtBoxes[i]
            item.visibility = makeVisible
        }
    }

    private fun updateGovernmentQuestionApiCall() {

        if(governmentParams.Questions.size>0)
        {
            //testGovernmentParams.BorrowerId = governmentParams.BorrowerId
            //testGovernmentParams.LoanApplicationId = governmentParams.LoanApplicationId
            for (question in governmentParams.Questions) {
                if(question.id == 21){
                    Timber.e("what is  "+question.answerData)
                    if(ownershipInterestAnswerData1==null)
                        question.answerData = null
                    if(ownershipInterestAnswerData1?.selectionOptionId == null)
                        question.answerData = null

                     continue
                }

                if (question.parentQuestionId == 130) {
                    if(bankruptcyMap.size==0)
                        question.answerData = null
                    else {
                        val test = hashMapOf<String, String>()
                        val newTestList = arrayListOf(HashMap<String, String>())
                        for(item in bankruptcyMap){
                            Timber.e("item kia ha ?"+item.value+"  and "+item.key)
                            test.put(item.key, item.value)
                            val json = Gson().toJson(test)
                            val mapCopy: HashMap<String, String> = Gson().fromJson(json, object : TypeToken<HashMap<String?, String>>() {}.type)
                            newTestList.add(mapCopy)
                            test.clear()
                        }
                        newTestList.removeAt(0)
                        question.answerData = newTestList
                    }
                    continue
                }

                if(question.id == 22){
                    Timber.e("what is  "+question.answerData)
                    if(ownershipInterestAnswerData2==null)
                        question.answerData = null
                    if(ownershipInterestAnswerData2?.selectionOptionId == null)
                        question.answerData = null
                    continue
                }
                question.answerData = null

                if (question.id == 140) {
                    //newChild.answerData = childSupportAnswerDataList
                    question.answerData = childSupportAnswerDataList
                }
                else
                if(question.id == 45){   //family
                    //question.answerData = FamilyAnswerData()
                }

                // ownership interest, it is handled when sent back....
                //if(question.parentQuestionId == 20){}

                // Bankruptcy


            }
            //governmentParams.Questions.add(childQuestionData)

            lifecycleScope.launchWhenStarted {
                sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                    borrowerAppViewModel.addOrUpdateGovernmentQuestions(authToken, governmentParams)
                }
            }
            findNavController().popBackStack()
        }


    }

    /*
    private fun guessTheType(any: Any) = when (any){
        is Int -> Timber.e("It's an Integer !")
        is String -> Timber.e("It's a String !")
        is Boolean -> Timber.e("It's a Boolean !")
        is Array<*> -> Timber.e("It's an Array !")
        is ArrayList<*> -> Timber.e("It's an ArrayList !")
        is Gson-> Timber.e("It's an Gson !")
        is List<*> -> Timber.e("It's a List !")
        is Set<*> -> Timber.e("It's a Set !")
        is OwnershipInterestAnswerData -> Timber.e("It's a TestAnswerData !")
        is ChildAnswerData -> Timber.e("It's an ChildAnswerData !")
        is BankruptcyAnswerData -> Timber.e("It's an BankruptcyAnswerData !")
        else -> Timber.e("Error ! Type not recognized...")
    }
     */

    private var currentBorrowerId:Int = 0

    private fun setUpDynamicTabs(){
        val governmentQuestionActivity = (activity as? GovtQuestionActivity)

        borrowerAppViewModel.governmentQuestionsModelClassList.observe(viewLifecycleOwner,  { governmentQuestionsModelClassList->
            var zeroIndexAppCompat:AppCompatTextView?= null
            if(governmentQuestionsModelClassList.size>0) {
                var selectedGovernmentQuestionModel: GovernmentQuestionsModelClass? = null
                for (item in governmentQuestionsModelClassList) {
                    if (item.passedBorrowerId == tabBorrowerId) {
                        selectedGovernmentQuestionModel = item
                        currentBorrowerId = tabBorrowerId!!
                        governmentQuestionActivity?.let { governmentQuestionActivity ->
                            governmentQuestionActivity.loanApplicationId?.let { nonNullLoanApplicationId ->
                                item.questionData?.let { questionDataList ->
                                    item.passedBorrowerId?.let { passedBorrowerId ->
                                        governmentParams =
                                            GovernmentParams(
                                                passedBorrowerId, nonNullLoanApplicationId,
                                                questionDataList
                                            )
                                        Timber.e(
                                            "TingoPingo = ",
                                            governmentParams.BorrowerId,
                                            governmentParams.toString()
                                        )
                                        //udateGovernmentQuestionsList.add(test)
                                    }
                                }
                            }
                        }

                        break
                    }
                }


                selectedGovernmentQuestionModel?.let{ selectedGovernmentQuestionModel->

                    val govtQuestionActivity = (activity as GovtQuestionActivity)
                    govtQuestionActivity.binding.govtDataLoader.visibility = View.INVISIBLE

                    childSupportAnswerDataList= arrayListOf()
                    bankruptcyAnswerData = BankruptcyAnswerData()
                    ownerShipInnerScreenParams = arrayListOf()



                    selectedGovernmentQuestionModel.questionData?.let { questionData ->
                        saveGovtQuestionForDetailAnswer = questionData

                        for (qData in questionData) {
                            lastQData = qData
                            qData.question?.let {
                                var questionReplaced = it
                                qData.firstName?.let {
                                    if (questionReplaced.contains("[Co-Applicant First Name]"))
                                        questionReplaced = questionReplaced.replace("[Co-Applicant First Name]", it)
                                }
                                qData.lastName?.let {
                                    if (questionReplaced.contains("[Co-Applicant Last Name]"))
                                        questionReplaced =
                                            questionReplaced.replace("[Co-Applicant Last Name]", it)
                                }
                                qData.question = questionReplaced
                            }

                            qData.headerText?.let { tabTitle ->

                                if (qData.parentQuestionId == null) {
                                    binding.parentContainer.visibility = View.INVISIBLE
                                    val appCompactTextView = createAppCompactTextView(tabTitle, currentBorrowerId)
                                    if (zeroIndexAppCompat == null)
                                        zeroIndexAppCompat = appCompactTextView

                                    if(tabTitle.equals(govtQuestionActivity.selectedQuestionHeader, true))
                                        zeroIndexAppCompat = appCompactTextView
                                    //tabArrayList.add(appCompactTextView)
                                    binding.horizontalTabs.addView(appCompactTextView)
                                    val contentView = createContentLayoutForTab(qData)

                                    innerLayoutHashMap.put(appCompactTextView, contentView)
                                    qData.id?.let { it1 -> idToContentMapping.put(it1, contentView) }

                                    binding.parentContainer.addView(contentView)
                                    appCompactTextView.setOnClickListener(openTabMenuScreen)
                                    horizontalTabArrayList.add(appCompactTextView)
                                }
                            }
                        }

                        // adding demo graphic tab here....
                        if (zeroIndexAppCompat != null) {
                            val appCompactTextView = createAppCompactTextView(AppConstant.demographicInformation, currentBorrowerId)
                            binding.horizontalTabs.addView(appCompactTextView)
                            val contentView = createContentLayoutForTab(
                                QuestionData(
                                    id = 5000, parentQuestionId = null, headerText = AppConstant.demographicInformation,
                                    questionSectionId = 1, ownTypeId = 1, firstName = lastQData.firstName, lastName = lastQData.lastName,
                                    question = "", answer = null, answerDetail = null, selectionOptionId = null, answerData = null))

                            innerLayoutHashMap.put(appCompactTextView, contentView)
                            idToContentMapping.put(5000, contentView)

                            if(AppConstant.demographicInformation.equals(govtQuestionActivity.selectedQuestionHeader, true))
                                zeroIndexAppCompat = appCompactTextView

                            binding.parentContainer.addView(contentView)
                            appCompactTextView.setOnClickListener(openTabMenuScreen)
                            horizontalTabArrayList.add(appCompactTextView)
                        }

                        var ownerShipBoxOneEnabled = true

                        for (qData in questionData) {
                            qData.parentQuestionId?.let { parentQuestionId ->
                                Timber.e("parentQuestionId...$parentQuestionId")
                                if (parentQuestionId == bankruptcyConstraintLayout.id) {
                                        Timber.e("bankruptcy = "+qData.answerDetail.toString())
                                        var extractedAnswer = ""
                                        qData.answerData?.let {
                                            val bankruptAnswerData: ArrayList<LinkedTreeMap<Any, Any>> = it as ArrayList<LinkedTreeMap<Any, Any>>
                                            if (bankruptAnswerData.size > 0) {
                                                for (i in 0 until bankruptAnswerData.size){
                                                    val test = bankruptAnswerData[i]
                                                    if(test.containsKey("1")){
                                                        bankruptcyAnswerData.`1` = true
                                                        extractedAnswer += "Chapter 7, "
                                                        bankruptcyMap.put("1", "Chapter 7")
                                                    }
                                                    else if(test.containsKey("2")){
                                                        bankruptcyAnswerData.`2` = true
                                                        extractedAnswer += "Chapter 11, "
                                                        bankruptcyMap.put("2", "Chapter 11")
                                                    }
                                                    else if(test.containsKey("3")){
                                                        bankruptcyAnswerData.`3` = true
                                                        extractedAnswer += "Chapter 12, "
                                                        bankruptcyMap.put("3", "Chapter 12")
                                                    }
                                                    else if(test.containsKey("4")){
                                                        bankruptcyAnswerData.`4` = true
                                                        extractedAnswer += "Chapter 13, "
                                                        bankruptcyMap.put("4", "Chapter 13")
                                                    }
                                                }

                                                if(extractedAnswer.isNotBlank() && extractedAnswer.isNotEmpty()){
                                                    extractedAnswer = extractedAnswer.removeRange(extractedAnswer.length-2, extractedAnswer.length)
                                                }

                                                bankruptcyConstraintLayout.detail_title.setTypeface(null, Typeface.NORMAL)
                                                bankruptcyConstraintLayout.detail_title.text = "Which Type?"
                                                bankruptcyConstraintLayout.detail_text.text = extractedAnswer
                                                bankruptcyConstraintLayout.detail_text.setTypeface(null, Typeface.BOLD)
                                                bankruptcyConstraintLayout.govt_detail_box.visibility = View.VISIBLE
                                            }
                                            /*
                                            if (bankruptAnswerData.size > 0) {
                                                if (bankruptAnswerData[0] != null) {
                                                    val getrow: Any = bankruptAnswerData[0]
                                                    val t: LinkedTreeMap<Any, Any> =
                                                        getrow as LinkedTreeMap<Any, Any>
                                                    t.
                                                    val chapter1 = t["`1`"].toString()
                                                    Timber.e("what i am getting in here = "+chapter1)

                                                    extractedAnswer = "Chapter 7"
                                                    bankruptcyAnswerData.`1` = true
                                                    Timber.e("1 = " + chapter1)
                                                    bankruptcyMap.put("1", "Chapter 7")
                                                }
                                                if (bankruptAnswerData.size > 1 && bankruptAnswerData[1] != null) {
                                                    val getrow: Any = bankruptAnswerData[1]
                                                    val t: LinkedTreeMap<Any, Any> =
                                                        getrow as LinkedTreeMap<Any, Any>
                                                    val chapter2 = t["`2`"].toString()
                                                    extractedAnswer = "$extractedAnswer, Chapter 11,"
                                                    bankruptcyAnswerData.`2` = true
                                                    Timber.e("2 = " + chapter2)
                                                    bankruptcyMap.put("2", "Chapter 11")

                                                }

                                                if (bankruptAnswerData.size > 2 && bankruptAnswerData[2] != null) {
                                                    val getrow: Any = bankruptAnswerData[2]
                                                    val t: LinkedTreeMap<Any, Any> =
                                                        getrow as LinkedTreeMap<Any, Any>
                                                    val chapter3 = t["`3`"].toString()
                                                    extractedAnswer = "$extractedAnswer, Chapter 12,"
                                                    bankruptcyAnswerData.`3` = true
                                                    Timber.e("3 = " + chapter3)
                                                    bankruptcyMap.put("3", "Chapter 12")
                                                }

                                                if (bankruptAnswerData.size > 3 && bankruptAnswerData[3] != null) {
                                                    val getrow: Any = bankruptAnswerData[3]
                                                    val t: LinkedTreeMap<Any, Any> =
                                                        getrow as LinkedTreeMap<Any, Any>
                                                    val chapter4 = t["`4`"].toString()
                                                    bankruptcyAnswerData.`4` = true
                                                    extractedAnswer = "$extractedAnswer, Chapter 13"
                                                    Timber.e("4 = " + chapter4)
                                                    bankruptcyMap.put("4", "Chapter 13")
                                                }

                                                Timber.e(" extracted answer = $extractedAnswer")

                                                bankruptcyConstraintLayout.detail_title.setTypeface(null, Typeface.NORMAL)
                                                bankruptcyConstraintLayout.detail_title.text = "Which Type?"

                                                bankruptcyConstraintLayout.detail_text.setText(extractedAnswer)
                                                bankruptcyConstraintLayout.detail_text.setTypeface(null, Typeface.BOLD)
                                                bankruptcyConstraintLayout.govt_detail_box.visibility = View.VISIBLE
                                            }

                                             */
                                        }
                                        qData.answerDetail?.let {
                                            if(it.isNotEmpty() && it.isNotBlank())
                                                bankruptcyAnswerData.extraDetail = it
                                        }
                                    }
                                else
                                if (parentQuestionId == childConstraintLayout.id) {
                                    Timber.e("childConstraintLayout " + qData.question)
                                    Timber.e(qData.answerDetail.toString())
                                }
                                else

                                    if (parentQuestionId == ownerShipConstraintLayout?.id) {
                                        ownerShipConstraintLayout?.let { ownerShipConstraintLayout->
                                            qData.answer?.let { answer ->
                                                if (!answer.equals("No", true)) {
                                                    if (ownerShipBoxOneEnabled) {
                                                        ownerShipBoxOneEnabled = false
                                                        Timber.e("ownerShipConstraintLayout " + qData.question)
                                                        Timber.e(qData.answerDetail.toString())


                                                        val t: LinkedTreeMap<Any, Any> =
                                                            qData.answerData as LinkedTreeMap<Any, Any>
                                                        //var selectionOptionText: String?
                                                        //var selectionOptionId: String? //t["selectionOptionId"].toString()

                                                        if (t["selectionOptionId"] != null && t["selectionOptionText"] != null) {
                                                            val selectionOptionText =
                                                                t["selectionOptionText"].toString()
                                                            val selectionOptionId =
                                                                t["selectionOptionId"].toString()
                                                                    .toDouble().toInt()
                                                            ownershipInterestAnswerData1 =
                                                                OwnershipInterestAnswerData(
                                                                    selectionOptionId,
                                                                    selectionOptionText
                                                                )
                                                            ownerShipConstraintLayout.detail_text.text =
                                                                selectionOptionText
                                                            qData.answerData =
                                                                ownershipInterestAnswerData1
                                                            ownerShipInnerScreenParams.add(
                                                                selectionOptionText
                                                            )
                                                        }

                                                        ownerShipConstraintLayout.detail_title.text =
                                                            qData.question

                                                        ownerShipConstraintLayout.detail_title.setTypeface(
                                                            null,
                                                            Typeface.NORMAL
                                                        )
                                                        ownerShipConstraintLayout.detail_text.setTypeface(
                                                            null,
                                                            Typeface.BOLD
                                                        )

                                                        ownerShipConstraintLayout.govt_detail_box.visibility =
                                                            View.VISIBLE
                                                    } else {

                                                        val t: LinkedTreeMap<Any, Any> =
                                                            qData.answerData as LinkedTreeMap<Any, Any>
                                                        if (t["selectionOptionId"] != null && t["selectionOptionText"] != null) {
                                                            val selectionOptionText =
                                                                t["selectionOptionText"].toString()
                                                            val selectionOptionId =
                                                                t["selectionOptionId"].toString()
                                                                    .toDouble().toInt()
                                                            ownershipInterestAnswerData2 =
                                                                OwnershipInterestAnswerData(
                                                                    selectionOptionId,
                                                                    selectionOptionText
                                                                )
                                                            ownerShipConstraintLayout.detail_text2.text =
                                                                selectionOptionText
                                                            qData.answerData =
                                                                ownershipInterestAnswerData2
                                                            ownerShipInnerScreenParams.add(
                                                                selectionOptionText
                                                            )
                                                        }

                                                        ownerShipConstraintLayout.detail_title2.text =
                                                            qData.question

                                                        ownerShipConstraintLayout.detail_title2.setTypeface(
                                                            null,
                                                            Typeface.NORMAL
                                                        )
                                                        ownerShipConstraintLayout.detail_text2.setTypeface(
                                                            null,
                                                            Typeface.BOLD
                                                        )

                                                        ownerShipConstraintLayout.govt_detail_box2.visibility =
                                                            View.VISIBLE
                                                    }
                                                }
                                            }
                                        }
                                    }

                                else
                                if (parentQuestionId == undisclosedLayout.id) {
                                    qData.answer?.let { answer->
                                        if (answer.isNotEmpty() && answer.isNotBlank()) {
                                            Timber.e("undisclosedLayout " + qData.question)
                                            Timber.e(qData.answerDetail.toString())
                                            undisclosedLayout.detail_title.text = qData.question
                                            undisclosedLayout.detail_text.text = "$".plus(answer)

                                            //ownerShipGlobalData.add(qData.answer!!)
                                            undisclosedLayout.govt_detail_box.visibility = View.VISIBLE
                                        }
                                    }
                                    undisclosedLayout.detail_title.setTypeface(null, Typeface.NORMAL)
                                    undisclosedLayout.detail_text.setTypeface(null, Typeface.BOLD)
                                }
                                else
                                    Timber.e("nothing")

                                }
                            }
                    }  // close of let block...

                    zeroIndexAppCompat?.let { zeroAppCompat->
                        zeroAppCompat.performClick()
                        //binding.horizontalScrollView.scrollTo(zeroAppCompat.translationX.toInt(), 0 )

                    }

                }
            }
            binding.parentContainer.postDelayed({
                binding.parentContainer.visibility = View.VISIBLE
                binding.horizontalScrollView.smoothScrollTo(zeroIndexAppCompat!!.left, 0 )
            },100)

        })
    }

    private fun createAppCompactTextView(tabTitle:String, currentBorrowerId:Int):AppCompatTextView{
        //val appCompactTextView = AppCompatTextView(requireContext())

        val appCompactTextView: AppCompatTextView =
            layoutInflater.inflate(R.layout.govt_text_view, null) as AppCompatTextView

        //appCompactTextView.setBackgroundColor(R.drawable.blue_white_style_filter)
        //appCompactTextView.setPadding(12,0,12,0)
        //appCompactTextView.height = 40

        val textParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            convertDpToPixel(30.0f,requireContext()),
            1.0f
        )

        textParam.setMargins( convertDpToPixel(4.0f,requireContext()), 0, convertDpToPixel(4.0f,requireContext()), 0)

        appCompactTextView.setLayoutParams(textParam)

       // appCompactTextView.setTextColor(resources.getColor(R.color.doc_filter_text_color_selector, activity?.theme ))
        //appCompactTextView.gravity = Gravity.CENTER
        //appCompactTextView.setTextSize(13, 13F)
        //appCompactTextView.isAllCaps = false
        //appCompactTextView.id = tabIndex
        appCompactTextView.tag = tabTitle
        appCompactTextView.text = tabTitle
        return appCompactTextView
    }

    private fun createContentLayoutForTab(questionData:QuestionData):ConstraintLayout{
        val variableQuestionData: QuestionData = questionData
        var childSupport = false
        var headerTitle = ""
        val contentCell: ConstraintLayout

        var questionId:Int = -100
        questionData.id?.let {
            questionId = it
        }

        if(questionData.headerText == AppConstant.ownershipConstantValue) {
            ownerShipConstraintLayout = layoutInflater.inflate(R.layout.ownership_interest_layout, null) as ConstraintLayout
            contentCell = ownerShipConstraintLayout as ConstraintLayout
            ownerShipConstraintLayout?.let { ownerShipConstraintLayout->
                    questionData.id?.let {
                    ownerShipConstraintLayout.id = it
                    contentCell.id = it
                }

                ownerShipConstraintLayout.govt_detail_box.setOnClickListener {
                    clickedContentCell = contentCell
                    navigateToInnerScreen(headerTitle, questionId)
                }

                ownerShipConstraintLayout.govt_detail_box2.setOnClickListener {
                    clickedContentCell = contentCell
                    navigateToInnerScreen(headerTitle, questionId)
                }
            }
        }
        else
        if(questionData.headerText == AppConstant.UndisclosedBorrowerFunds) {
            contentCell = layoutInflater.inflate(R.layout.common_govt_content_layout, null) as ConstraintLayout
            contentCell.detail_title.text = UndisclosedBorrowerFundFragment.UndisclosedBorrowerQuestionConstant
            contentCell.govt_detail_box.detail_title.setTypeface(null, Typeface.NORMAL)

            undisclosedLayout = contentCell
            questionData.id?.let {
                undisclosedLayout.id = it
                contentCell.id = it
            }
        }
        else
        if(questionData.headerText == AppConstant.childConstantValue) {
            contentCell = layoutInflater.inflate(R.layout.children_separate_layout, null) as ConstraintLayout
            childSupport = true
            childConstraintLayout = contentCell
            questionData.id?.let {
                childConstraintLayout.id = it
                contentCell.id = it
            }
        }
        else
        if(questionData.headerText?.trim() == AppConstant.Bankruptcy) {
            contentCell = layoutInflater.inflate(R.layout.common_govt_content_layout, null) as ConstraintLayout
            bankruptcyConstraintLayout = contentCell
            questionData.id?.let {
                bankruptcyConstraintLayout.id = it
                contentCell.id = it
            }
        }
        else
        if(questionData.headerText?.trim() == AppConstant.demographicInformation) {
            contentCell = layoutInflater.inflate(R.layout.new_demo_graphic_show_layout, null) as ConstraintLayout
            demoGraphLayout = contentCell
            questionData.id?.let {
                demoGraphLayout.id = it
                contentCell.id = it
            }
            contentCell.visibility = View.INVISIBLE
            demoGraphLayout.visibility = View.INVISIBLE
            observeDemoGraphicData(contentCell)
            return contentCell
        }
        else {
            contentCell = layoutInflater.inflate(R.layout.common_govt_content_layout, null) as ConstraintLayout
            questionData.id?.let {
              contentCell.id = it
            }
        }


        questionData.headerText?.let {
            contentCell.govt_detail_box.tag = it
            contentCell.ans_yes.tag = it
            headerTitle = it
        }
        contentCell.govt_question.text =  questionData.question
        questionData.answerDetail?.let {
            contentCell.detail_text.text = it
        }




        if(childSupport){
            contentCell.govt_detail_box.setOnClickListener {
                clickedContentCell = contentCell
                navigateToInnerScreen(headerTitle ,  questionId)
            }
            contentCell.govt_detail_box2.setOnClickListener {
                clickedContentCell = contentCell
                navigateToInnerScreen(headerTitle , questionId)
            }
            contentCell.govt_detail_box3.setOnClickListener {
                clickedContentCell = contentCell
                navigateToInnerScreen(headerTitle , questionId)
            }

            childGovtBoxes.add(contentCell.govt_detail_box)
            childGovtBoxes.add(contentCell.govt_detail_box2)
            childGovtBoxes.add(contentCell.govt_detail_box3)


            questionData.answerData?.let { notNullChildAnswerData->
                val childAnswerData = notNullChildAnswerData as ArrayList<*>
                if (childAnswerData.size > 0 && childAnswerData[0] != null) {
                    val getrow: Any = childAnswerData[0]
                    val t: LinkedTreeMap<Any, Any> = getrow as LinkedTreeMap<Any, Any>
                    val liabilityName = t["liabilityName"].toString()
                    val monthlyPayment = t["monthlyPayment"].toString().toDouble().toInt()
                    val liabilityTypeId = t["liabilityTypeId"].toString().toDouble().toInt()
                    val name = t["name"].toString()
                    val remainingMonth = t["remainingMonth"].toString().toDouble().toInt()

                    Timber.e("liabilityName = " + liabilityName + "  " + t["name"] + "  " + t["monthlyPayment"])
                    contentCell.detail_title.text = liabilityName
                    contentCell.detail_text.text = "$".plus(monthlyPayment.toString())

                    contentCell.govt_detail_box.visibility = View.VISIBLE

                    childSupportAnswerDataList.add(ChildAnswerData(liabilityName, liabilityTypeId, monthlyPayment, name, remainingMonth))


                }

                if (childAnswerData.size > 1 && childAnswerData[1] != null) {
                    val getrow: Any = childAnswerData[1]
                    val t: LinkedTreeMap<Any, Any> = getrow as LinkedTreeMap<Any, Any>
                    val liabilityName = t["liabilityName"].toString()
                    val monthlyPayment = t["monthlyPayment"].toString().toDouble().toInt()
                    val liabilityTypeId = t["liabilityTypeId"].toString().toDouble().toInt()
                    val name = t["name"].toString()
                    val remainingMonth = t["remainingMonth"].toString().toDouble().toInt()

                    childSupportAnswerDataList.add(ChildAnswerData(liabilityName, liabilityTypeId, monthlyPayment, name, remainingMonth))


                    Timber.e("liabilityName = " + liabilityName + "  " + t["name"] + "  " + t["monthlyPayment"])
                    contentCell.detail_title2.text = liabilityName
                    contentCell.detail_text2.text = "$".plus(monthlyPayment.toString())


                    contentCell.govt_detail_box2.visibility = View.VISIBLE


                }

                if (childAnswerData.size > 2 && childAnswerData[2] != null) {
                    val getrow: Any = childAnswerData[2]
                    val t: LinkedTreeMap<Any, Any> = getrow as LinkedTreeMap<Any, Any>
                    val liabilityName = t["liabilityName"].toString()
                    val monthlyPayment = t["monthlyPayment"].toString().toDouble().toInt()
                    val liabilityTypeId = t["liabilityTypeId"].toString().toDouble().toInt()
                    val name = t["name"].toString()
                    val remainingMonth = t["remainingMonth"].toString().toDouble().toInt()
                    childSupportAnswerDataList.add(ChildAnswerData(liabilityName, liabilityTypeId, monthlyPayment, name, remainingMonth))

                    Timber.e("liabilityName = " + liabilityName + "  " + t["name"] + "  " + t["monthlyPayment"])

                    contentCell.detail_title3.text = liabilityName
                    contentCell.detail_text3.text = "$".plus(monthlyPayment.toString())
                    contentCell.govt_detail_box3.visibility = View.VISIBLE

                }

                if (questionData.answer.equals("no", true)) {
                    contentCell.ans_no.isChecked = true
                    showHideChildGovtBoxes(View.INVISIBLE)
                } else {
                    contentCell.ans_yes.isChecked = true
                    showHideChildGovtBoxes(View.VISIBLE)
                }
            }
            contentCell.ans_no.setOnClickListener {
                showHideChildGovtBoxes(View.INVISIBLE)
            }
            contentCell.ans_yes.setOnClickListener {
                showHideChildGovtBoxes(View.VISIBLE)
                clickedContentCell = contentCell
                navigateToInnerScreen(headerTitle, questionId)
            }
        }
        else
        {
            if(questionData.answer.equals("no",true)) {
                contentCell.ans_no.isChecked = true
                contentCell.govt_detail_box.visibility = View.INVISIBLE
                contentCell.govt_detail_box2?.visibility = View.INVISIBLE

                contentCell.govt_detail_box3?.visibility = View.INVISIBLE
            }

            else if(questionData.answer.equals("yes",true)) {
                contentCell.ans_yes.isChecked = true
                if(questionData.answerDetail!=null && questionData.answer.equals("Yes", true) &&  questionData.answerDetail!!.isNotBlank() && questionData.answerDetail!!.isNotEmpty()) {
                    contentCell.govt_detail_box.visibility = View.VISIBLE
                    questionData.answerDetail?.let {
                        contentCell.detail_text.text = it
                    }
                }
            }

            contentCell.ans_no.setOnClickListener {
                contentCell.govt_detail_box.visibility = View.INVISIBLE
                contentCell.govt_detail_box2?.visibility = View.INVISIBLE
                contentCell.govt_detail_box3?.visibility = View.INVISIBLE
                variableQuestionData.answer = "No"
                updateGovernmentData(variableQuestionData)
            }
            contentCell.ans_yes.setOnClickListener {
                if(contentCell.detail_text.text.toString().isNotBlank() && contentCell.detail_text.text.toString().isNotEmpty()) {
                    contentCell.govt_detail_box.visibility = View.VISIBLE
                    questionData.answerDetail?.let {
                        contentCell.detail_text.text = it
                    }
                }

                contentCell.govt_detail_box2?.let{ govt_detail_box2->
                    if(contentCell.detail_text2.text.toString().isNotBlank() && contentCell.detail_text2.text.toString().isNotEmpty())
                        govt_detail_box2.visibility = View.VISIBLE

                    govt_detail_box2.setOnClickListener {
                        variableQuestionData.answer = "Yes"
                        updateGovernmentData(variableQuestionData)
                        clickedContentCell = contentCell
                        navigateToInnerScreen(headerTitle, questionId )
                    }
                }

                contentCell.govt_detail_box3?.let{ govt_detail_box3->
                    if(contentCell.detail_text3.text.toString().isNotBlank() && contentCell.detail_text3.text.toString().isNotEmpty())
                        govt_detail_box3.visibility = View.VISIBLE

                    govt_detail_box3.setOnClickListener {
                        variableQuestionData.answer = "Yes"
                        updateGovernmentData(variableQuestionData)
                        clickedContentCell = contentCell
                        navigateToInnerScreen(headerTitle, questionId )
                    }
                }

                variableQuestionData.answer = "Yes"
                updateGovernmentData(variableQuestionData)
                clickedContentCell = contentCell
                navigateToInnerScreen(headerTitle, questionId )
            }
            contentCell.govt_detail_box.setOnClickListener {
                variableQuestionData.answer = "Yes"
                updateGovernmentData(variableQuestionData)
                clickedContentCell = contentCell
                navigateToInnerScreen(headerTitle, questionId )
            }
        }

        return contentCell
    }

    private fun updateGovernmentData(testData:QuestionData){
        for (item in governmentParams.Questions) {
            if(item.id == testData.id){
                item.answer = testData.answer
            }
        }
    }

    private fun navigateToInnerScreen(stringForSpecificFragment:String, questionId: Int){
        val bundle = Bundle()
        bundle.putInt(AppConstant.questionId, questionId)
        bundle.putInt(AppConstant.whichBorrowerId, currentBorrowerId)
        bundle.putParcelable(AppConstant.addUpdateQuestionsParams , governmentParams)
        bundle.putString(AppConstant.govtUserName , (lastQData.firstName+" "+lastQData.lastName))

        when(stringForSpecificFragment) {
               "Undisclosed Borrowered Funds" ->{

                   findNavController().navigate(R.id.action_undisclosed_borrowerfund, bundle )
               }
               "Family or Business affiliation" ->{  findNavController().navigate(R.id.action_family_affiliation , bundle ) }
               "Ownership Interest in Property" ->{

                   bundle.putStringArrayList(AppConstant.ownerShipGlobalData, ownerShipInnerScreenParams)
                   findNavController().navigate(R.id.action_ownership_interest , bundle)
               }
               "Own Property Type" ->{}
               "Debt Co-Signer or Guarantor" ->{  findNavController().navigate(R.id.action_debt_co , bundle )}
               "Outstanding Judgements" ->{  findNavController().navigate(R.id.action_outstanding , bundle)}
               "Federal Debt Deliquency" ->{ findNavController().navigate(R.id.action_federal_debt , bundle)}
               "Party to Lawsuit" ->{

                   findNavController().navigate(R.id.action_party_to , bundle)
               }
               "Bankruptcy " ->{
                   val bankruptcyAnswerDataCopy = bankruptcyAnswerData.copy() //ArrayList(bankruptcyAnswerData.map { it.copy() })
                   bundle.putParcelable(AppConstant.bankruptcyAnswerData, bankruptcyAnswerDataCopy)
                   findNavController().navigate(R.id.navigation_bankruptcy , bundle)
               }
               "Child Support, Alimony, etc." ->{
                   bundle.putParcelableArrayList(AppConstant.childGlobalList, childSupportAnswerDataList)
                   findNavController().navigate(R.id.action_child_support, bundle)
               }
               "Foreclosured Property" ->{ findNavController().navigate(R.id.action_fore_closure_property , bundle) }
               "Pre-Foreclosureor Short Sale" ->{ findNavController().navigate(R.id.action_pre_for_closure , bundle) }
               "Title Conveyance" ->{ findNavController().navigate(R.id.action_title_conveyance, bundle) }
               else->{
                   Timber.e(" not matching with header title...")
               }
           }
    }

    private val openTabMenuScreen = View.OnClickListener { p0 ->
        demoGraphicScreenDisplaying = false
        if(p0 is AppCompatTextView){
            for(item in horizontalTabArrayList){
                item.isActivated = false
                item.setTextColor(resources.getColor(R.color.doc_filter_txt_color, requireActivity().theme))
            }
            p0.isActivated = true
            p0.setTextColor(resources.getColor(R.color.colaba_primary_color, requireActivity().theme))
            for ((key, value) in innerLayoutHashMap) {
                if(key == p0) {
                    if(key.text == AppConstant.demographicInformation)
                        demoGraphicScreenDisplaying = true
                    value.visibility = View.VISIBLE
                }
                else
                    value.visibility = View.INVISIBLE
            }
        }
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun showEthnicityInnerBox(){
        if(otherEthnicity.isNotEmpty() && otherEthnicity.isNotBlank()) {
            otherEthnicity = "Other Hispanic or Latino: $otherEthnicity"
            demoGraphLayout.other_ethnicity.text = otherEthnicity
            demoGraphLayout.other_ethnicity.visibility = View.VISIBLE
            demoGraphLayout.hispanic_or_latino_child_box_layout.visibility = View.VISIBLE
        }
        else
            demoGraphLayout.other_ethnicity.visibility = View.GONE

        if(ethnicityChildNames.isNotEmpty() && ethnicityChildNames.isNotBlank()) {
            //ethnicityChildNames = ethnicityChildNames.substring(0, ethnicityChildNames.length-2)
            demoGraphLayout.ethnicity_children.text = ethnicityChildNames
            demoGraphLayout.ethnicity_children.visibility = View.VISIBLE
            demoGraphLayout.hispanic_or_latino_child_box_layout.visibility = View.VISIBLE
        }
        else
            demoGraphLayout.ethnicity_children.visibility = View.GONE
    }

    private fun showNativeHawaiiInnerBox(){
        if(nativeHawaiiOtherRace.isNotEmpty() && nativeHawaiiOtherRace.isNotBlank()) {
            nativeHawaiiOtherRace = "Other Pacific Islander: $nativeHawaiiOtherRace"

            demoGraphLayout.other_typed_native_hawaiian.text = nativeHawaiiOtherRace
            demoGraphLayout.other_typed_native_hawaiian.visibility = View.VISIBLE
            demoGraphLayout.native_hawaian_child_box_layout.visibility = View.VISIBLE
        }
        else
            demoGraphLayout.other_typed_native_hawaiian.visibility = View.GONE

        if(nativeHawaiiChildNames.isNotEmpty() && nativeHawaiiChildNames.isNotBlank()) {
            demoGraphLayout.child_native_hawaiian.text = nativeHawaiiChildNames
            demoGraphLayout.child_native_hawaiian.visibility = View.VISIBLE
            demoGraphLayout.native_hawaian_child_box_layout.visibility = View.VISIBLE
        }
        else
            demoGraphLayout.child_native_hawaiian.visibility = View.GONE
    }

    private fun showAsianInnerBox(){
        if(otherAsianRace.isNotEmpty() && otherAsianRace.isNotBlank()) {
            otherAsianRace = "Other Asian: $otherAsianRace"
            demoGraphLayout.other_asian_race.text = otherAsianRace
            demoGraphLayout.other_asian_race.visibility = View.VISIBLE
            demoGraphLayout.asian_child_box_layout.visibility = View.VISIBLE
        }
        else
            demoGraphLayout.other_asian_race.visibility = View.GONE

        if(asianChildNames.isNotEmpty() && asianChildNames.isNotBlank()) {
            //asianChildNames = asianChildNames.substring(0, asianChildNames.length-2)
            demoGraphLayout.asian_child_names.text = asianChildNames
            demoGraphLayout.asian_child_names.visibility = View.VISIBLE
            demoGraphLayout.asian_child_box_layout.visibility = View.VISIBLE
        }
        else
            demoGraphLayout.asian_child_names.visibility = View.GONE
    }

    private fun addDemoGraphicEvents(){

        demoGraphLayout.do_not_wish_check_box.setOnClickListener{
            demoGraphLayout.white_check_box.isChecked = false
            demoGraphLayout.black_or_african_check_box.isChecked = false
            demoGraphLayout.american_or_indian_check_box.isChecked = false
            demoGraphLayout.native_hawaian_or_other_check_box.isChecked = false
            demoGraphLayout.asian_check_box.isChecked = false

            if(demoGraphLayout.do_not_wish_check_box.isChecked) {
                demoGraphLayout.asian_child_box_layout.visibility = View.GONE
                demoGraphLayout.native_hawaian_child_box_layout.visibility = View.GONE
            }

        }

        demoGraphLayout.white_check_box.setOnClickListener{ demoGraphLayout.do_not_wish_check_box.isChecked = false }
        demoGraphLayout.native_hawaian_or_other_check_box.setOnClickListener{ demoGraphLayout.do_not_wish_check_box.isChecked = false }
        demoGraphLayout.black_or_african_check_box.setOnClickListener{ demoGraphLayout.do_not_wish_check_box.isChecked = false }
        demoGraphLayout.asian_check_box.setOnClickListener{ demoGraphLayout.do_not_wish_check_box.isChecked = false }
        demoGraphLayout.american_or_indian_check_box.setOnClickListener{ demoGraphLayout.do_not_wish_check_box.isChecked = false }

    }

    private fun observeDemoGraphicData( contentCell:ConstraintLayout){
        borrowerAppViewModel.demoGraphicInfoList.observe(viewLifecycleOwner,{ demoGraphicInfoList->

            demoGraphLayout.asian_child_box_layout.visibility = View.GONE
            demoGraphLayout.native_hawaian_child_box_layout.visibility = View.GONE
            demoGraphLayout.hispanic_or_latino_child_box_layout.visibility = View.GONE

            if(demoGraphicInfoList.size>0){
                var selectedDemoGraphicInfoList: DemoGraphicResponseModel? =null
                for(item in demoGraphicInfoList){
                    if(item.passedBorrowerId == tabBorrowerId ) {
                        selectedDemoGraphicInfoList = item
                        break
                    }
                }

                selectedDemoGraphicInfoList?.let {
                    it.demoGraphicData?.let { demoGraphicData ->

                        variableDemoGraphicData = demoGraphicData
                        ethnicityChildNames = ""
                        otherEthnicity = ""
                        nativeHawaiiChildNames = ""
                        nativeHawaiiOtherRace = ""
                        asianChildNames = ""
                        otherAsianRace = ""

                        demoGraphicData.genderId?.let { genderId ->
                            variableGender = genderId
                            if (genderId == 1)
                                demoGraphLayout.demo_male.isChecked = true
                            else
                            if (genderId == 2)
                                demoGraphLayout.demo_female.isChecked = true
                            else
                            if (genderId == 3)
                                demoGraphLayout.demo_do_not_wish_to_provide.isChecked = true
                        }

                        demoGraphicData.ethnicity?.let { ethnicityList ->
                            variableEthnicityList = ethnicityList
                            if (ethnicityList.isNotEmpty()) {
                                val selectedEthnicity = ethnicityList[0]
                                if (selectedEthnicity.ethnicityId == 1) {
                                    demoGraphLayout.hispanic_or_latino.isChecked = true
                                    selectedEthnicity.ethnicityDetails?.let { theList ->
                                        for (item in theList) {
                                            ethnicityChildList.add(item)
                                            item.isOther?.let { isOther ->
                                                if (isOther)
                                                    item.otherEthnicity?.let {
                                                        otherEthnicity = it
                                                    }
                                                else
                                                    ethnicityChildNames =
                                                        ethnicityChildNames + item.name + ", "
                                            }
                                        }
                                    }

                                    showEthnicityInnerBox()
                                }
                                else
                                if (selectedEthnicity.ethnicityId == 2)
                                    demoGraphLayout.not_hispanic.isChecked = true
                                else
                                if (selectedEthnicity.ethnicityId == 3)
                                    demoGraphLayout.not_telling_ethnicity.isChecked = true
                            }
                        }

                        demoGraphicData.race?.let { raceList ->
                            variableRaceList = raceList
                            for (race in raceList) {
                                if (race.raceId == 1) {
                                    demoGraphLayout.american_or_indian_check_box.isChecked = true
                                }
                                if (race.raceId == 2) {
                                    demoGraphLayout.asian_check_box.isChecked = true
                                    race.raceDetails?.let { asianChildList ->

                                        for (item in asianChildList) {
                                            this.asianChildList.add(item)
                                            item.isOther?.let { isOther ->
                                                if (isOther)
                                                    item.otherRace?.let {
                                                        otherAsianRace = it
                                                    }
                                                else
                                                    asianChildNames =
                                                        asianChildNames + item.name + ", "
                                            }
                                        }
                                    }
                                    showAsianInnerBox()
                                }
                                if (race.raceId == 3) {
                                    demoGraphLayout.black_or_african_check_box.isChecked =
                                        true
                                }
                                if (race.raceId == 4) {
                                    demoGraphLayout.native_hawaian_or_other_check_box.isChecked =
                                        true

                                    race.raceDetails?.let { nativeHawaianChildList ->
                                        for (item in nativeHawaianChildList) {
                                            nativeHawaiianChildList.add(item)
                                            item.isOther?.let { isOther ->
                                                if (isOther)
                                                    item.otherRace?.let {
                                                        nativeHawaiiOtherRace = it
                                                    }
                                                else
                                                    nativeHawaiiChildNames =
                                                        nativeHawaiiChildNames + item.name + ", "
                                            }
                                        }
                                    }


                                    showNativeHawaiiInnerBox()

                                }
                                if (race.raceId == 5) {
                                    demoGraphLayout.white_check_box.isChecked = true
                                }
                                if (race.raceId == 6) {
                                    demoGraphLayout.do_not_wish_check_box.performClick()
                                }
                            }


                        }


                        // Now add static events....
                        setUpDemoGraphicScreen()
                        addDemoGraphicEvents()
                    }
                }
            }
        })
    }

    private fun setUpDemoGraphicScreen() {

        demoGraphLayout.american_or_indian_check_box.setOnCheckedChangeListener{ buttonView, isChecked ->
            updateDemoGraphicRace(1, isChecked)
        }

        demoGraphLayout.asian_check_box.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if((demoGraphLayout.asian_child_names.text.isNotEmpty() &&
                        demoGraphLayout.asian_check_box.text.isNotBlank()) ||
                    (demoGraphLayout.other_asian_race.text.isNotEmpty() &&
                    demoGraphLayout.other_asian_race.text.isNotBlank())
                        )
                demoGraphLayout.asian_child_box_layout.visibility = View.VISIBLE

                val copyAsianChildList =  ArrayList(asianChildList.map { it.copy() })
                val bundle = bundleOf(AppConstant.asianChildList to copyAsianChildList)
                findNavController().navigate(R.id.action_asian , bundle)
                Timber.e("not accessible...")
            }
            else
                demoGraphLayout.asian_child_box_layout.visibility = View.GONE

            updateDemoGraphicRace(2, isChecked)

        }

        demoGraphLayout.black_or_african_check_box.setOnCheckedChangeListener{ buttonView, isChecked ->
            updateDemoGraphicRace(3, isChecked)
        }

        demoGraphLayout.native_hawaian_or_other_check_box.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if((demoGraphLayout.child_native_hawaiian.text.isNotEmpty() &&
                            demoGraphLayout.child_native_hawaiian.text.isNotBlank()) ||
                    (demoGraphLayout.other_typed_native_hawaiian.text.isNotEmpty() &&
                            demoGraphLayout.other_typed_native_hawaiian.text.isNotBlank())
                )
                    demoGraphLayout.native_hawaian_child_box_layout.visibility = View.VISIBLE
                val copyNativeHawaiianChildList =  ArrayList(nativeHawaiianChildList.map { it.copy() })
                val bundle = bundleOf(AppConstant.nativeHawaianChildList to copyNativeHawaiianChildList)
                findNavController().navigate(R.id.action_native_hawai, bundle)
            }
            else
                demoGraphLayout.native_hawaian_child_box_layout.visibility = View.GONE
            updateDemoGraphicRace(4, isChecked)
        }

        demoGraphLayout.white_check_box.setOnCheckedChangeListener { buttonView, isChecked ->
            updateDemoGraphicRace(5, isChecked)
        }


        demoGraphLayout.do_not_wish_check_box.setOnCheckedChangeListener { buttonView, isChecked ->
            variableRaceList.clear()
            updateDemoGraphicRace(6, isChecked)
        }

        demoGraphLayout.native_hawaian_child_box_layout.setOnClickListener{
            if (demoGraphLayout.native_hawaian_or_other_check_box.isChecked) {
                demoGraphLayout.native_hawaian_child_box_layout.visibility = View.VISIBLE
                val copyNativeHawaiianChildList =  ArrayList(nativeHawaiianChildList.map { it.copy() })
                val bundle = bundleOf(AppConstant.nativeHawaianChildList to copyNativeHawaiianChildList)
                findNavController().navigate(R.id.action_native_hawai, bundle)
            }

        }

        demoGraphLayout.asian_child_box_layout.setOnClickListener{
            if ( demoGraphLayout.asian_check_box.isChecked) {
                demoGraphLayout.asian_child_box_layout.visibility = View.VISIBLE
                val copyAsianChildList =  ArrayList(asianChildList.map { it.copy() })
                val bundle = bundleOf(AppConstant.asianChildList to copyAsianChildList)
                findNavController().navigate(R.id.action_asian , bundle)
                Timber.e("not accessible...")
            }
        }

        demoGraphLayout.hispanic_or_latino.setOnClickListener {
            val copyEthnicityChildList =  ArrayList(ethnicityChildList.map { it.copy() })
            val bundle = bundleOf(AppConstant.ethnicityChildList to copyEthnicityChildList)
            findNavController().navigate(R.id.action_hispanic , bundle)
            demoGraphLayout.not_hispanic.isChecked = false
            demoGraphLayout.not_telling_ethnicity.isChecked = false
            showEthnicityInnerBox()
            updateDemoGraphicEthnicity(1)
        }

        demoGraphLayout.hispanic_or_latino_child_box_layout.setOnClickListener {
            val copyEthnicityChildList =  ArrayList(ethnicityChildList.map { it.copy() })
            val bundle = bundleOf(AppConstant.ethnicityChildList to copyEthnicityChildList)
            findNavController().navigate(R.id.action_hispanic, bundle)
            demoGraphLayout.not_hispanic.isChecked = false
            demoGraphLayout.not_telling_ethnicity.isChecked = false
        }

        demoGraphLayout.not_hispanic.setOnClickListener{
            demoGraphLayout.hispanic_or_latino.isChecked = false
            demoGraphLayout.not_telling_ethnicity.isChecked = false
            demoGraphLayout.hispanic_or_latino_child_box_layout.visibility = View.GONE
            updateDemoGraphicEthnicity(2)
        }

        demoGraphLayout.not_telling_ethnicity.setOnClickListener{
            demoGraphLayout.hispanic_or_latino.isChecked = false
            demoGraphLayout.not_hispanic.isChecked = false
            demoGraphLayout.hispanic_or_latino_child_box_layout.visibility = View.GONE
            updateDemoGraphicEthnicity(3)
        }


        demoGraphLayout.demo_male.setOnClickListener { updateDemoGraphicGender(1)}
        demoGraphLayout.demo_female.setOnClickListener { updateDemoGraphicGender(2)}
        demoGraphLayout.demo_do_not_wish_to_provide.setOnClickListener { updateDemoGraphicGender(3)}

    }

    private fun updateDemoGraphicRace(raceId:Int, removeFromList:Boolean){
       if(!removeFromList) {
           for (race in variableRaceList) {
               if (race.raceId == raceId) {
                   variableRaceList.remove(race)
                   break
               }
           }
       }
       else
           variableRaceList.add(DemoGraphicRace(arrayListOf(), raceId))
   }

    private fun updateDemoGraphicEthnicity(ethnicityId:Int){
        variableEthnicityList.clear()
        variableEthnicityList.add(EthnicityDemoGraphic(ethnicityId = ethnicityId , ethnicityDetails = arrayListOf()) )
    }

    private fun updateDemoGraphicGender(genderId:Int){
        variableGender = genderId
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController = findNavController()
        // We use a String here, but any type that can be put in a Bundle is supported
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<ArrayList<DemoGraphicRaceDetail>>(AppConstant.selectedAsianChildList)?.observe(
            viewLifecycleOwner) { resultAsianChildList ->
            // Do something with the result.
            asianChildList = resultAsianChildList
            if(resultAsianChildList.size>0) {
                for(item in variableRaceList){
                    if(item.raceId == 2) {
                        item.raceDetails = resultAsianChildList
                        break
                    }
                }


                asianChildNames = ""
                otherAsianRace = ""

                for(item in resultAsianChildList) {
                    item.isOther?.let { isOther ->
                        if (isOther)
                            item.otherRace?.let {
                                otherAsianRace = it
                            }
                        else
                            asianChildNames =
                                asianChildNames + item.name + ", "
                    }
                }
                showAsianInnerBox()
                //updateDemoGraphicService()
            }
            else
            {
                demoGraphLayout.asian_child_names.text =""
                demoGraphLayout.other_asian_race.text=""
                demoGraphLayout.asian_child_box_layout.visibility = View.GONE
                demoGraphLayout.asian_check_box.isChecked = false
            }

        }

        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<ArrayList<DemoGraphicRaceDetail>>(AppConstant.selectedNativeHawaianChildList)?.observe(
            viewLifecycleOwner) { selectedNativeHawaianChildList ->
            nativeHawaiianChildList = selectedNativeHawaianChildList
            if(selectedNativeHawaianChildList.size>0) {
                nativeHawaiiChildNames = ""
                nativeHawaiiOtherRace = ""
                for (item in selectedNativeHawaianChildList) {
                    item.isOther?.let { isOther ->
                        if (isOther)
                            item.otherRace?.let {
                                nativeHawaiiOtherRace = it
                            }
                        else
                            nativeHawaiiChildNames = nativeHawaiiChildNames + item.name + ", "
                    }
                }
                showNativeHawaiiInnerBox()

                for(item in variableRaceList){
                    if(item.raceId == 4) {
                        item.raceDetails = selectedNativeHawaianChildList
                        break
                    }
                }
                //updateDemoGraphicService()
            }
            else
            {
                demoGraphLayout.child_native_hawaiian.text =""
                demoGraphLayout.other_typed_native_hawaiian.text=""
                demoGraphLayout.native_hawaian_child_box_layout.visibility = View.GONE
                demoGraphLayout.native_hawaian_or_other_check_box.isChecked = false
            }

        }

        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<ArrayList<EthnicityDetailDemoGraphic>>(AppConstant.selectedEthnicityChildList)?.observe(
            viewLifecycleOwner) { theList ->
            // Do something with the result.

            ethnicityChildList = theList
            if(theList.size>0) {
                ethnicityChildNames = ""
                otherEthnicity = ""
                for (item in theList) {
                    item.isOther?.let { isOther ->
                        if (isOther)
                            item.otherEthnicity?.let {
                                otherEthnicity = it
                            }
                        else
                            ethnicityChildNames = ethnicityChildNames + item.name + ", "
                    }
                }
                showEthnicityInnerBox()
                variableEthnicityList[0].ethnicityDetails  = theList
                //updateDemoGraphicService()
            }
            else
            {
                demoGraphLayout.ethnicity_children.text =""
                demoGraphLayout.other_ethnicity.text=""
                demoGraphLayout.hispanic_or_latino_child_box_layout.visibility = View.GONE
                demoGraphLayout.hispanic_or_latino.isChecked = false
            }
        }
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
    fun updateReceivedFromInnerScreen(updateEvent: GovtScreenUpdateEvent) {
        if(updateEvent.whichBorrowerId == currentBorrowerId) {
            clickedContentCell.govt_detail_box.detail_title.text = updateEvent.detailTitle
            clickedContentCell.govt_detail_box.detail_text.text = updateEvent.detailDescription
            if (updateEvent.detailDescription.isNotEmpty() && updateEvent.detailDescription.isNotBlank())
                clickedContentCell.govt_detail_box.visibility = View.VISIBLE
            else
                clickedContentCell.govt_detail_box.visibility = View.INVISIBLE
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateUndisclosedBorrowerFunds(updateEvent: UndisclosedBorrowerFundUpdateEvent) {
        if(updateEvent.whichBorrowerId == currentBorrowerId) {
            clickedContentCell.govt_detail_box.detail_title.text =
                UndisclosedBorrowerFundFragment.UndisclosedBorrowerQuestionConstant
            clickedContentCell.govt_detail_box.detail_title.setTypeface(null, Typeface.NORMAL)
            clickedContentCell.govt_detail_box.detail_text.text =
                "$".plus(Common.addNumberFormat(updateEvent.detailDescription.toDouble()))
            clickedContentCell.govt_detail_box.detail_text.setTypeface(null, Typeface.BOLD)
            clickedContentCell.govt_detail_box.visibility = View.VISIBLE

            governmentParams.Questions.let { questions ->
                for (question in questions) {
                    question.parentQuestionId?.let { parentQuestionId ->
                        if (parentQuestionId == undisclosedLayout.id) {
                            question.answer = updateEvent.detailDescription
                            question.answerDetail = updateEvent.detailTitle
                        }
                    }
                }
            }
        }
    }

    @Parcelize
    data class OwnershipInterestAnswerData(
        val selectionOptionId: Int,
        val selectionOptionText: String
    ): Parcelable

    data class FamilyAnswerData(
        val IsAffiliatedWithSeller: Boolean = false,
        val AffiliationDescription: String? = null
    )


    var ownershipInterestAnswerData1:OwnershipInterestAnswerData?= null
    var ownershipInterestAnswerData2:OwnershipInterestAnswerData?= null
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateOwnershipInterest(updateEvent: OwnershipInterestUpdateEvent) {
        if(updateEvent.whichBorrowerId == currentBorrowerId) {
            var secondMatched = true
            ownerShipInnerScreenParams.clear()

            Timber.e(" 1- updateOwnershipInterest = " + updateEvent.index1 + " = " + updateEvent.answer1)
            Timber.e(" 2- updateOwnershipInterest = " + updateEvent.index2 + " = " + updateEvent.answer2)

            governmentParams.Questions.let { questions ->
                for (item in questions) {
                    item.parentQuestionId?.let { parentQuestionId ->
                        if (parentQuestionId == ownerShipConstraintLayout?.id) {
                            ownerShipConstraintLayout?.let { ownerShipConstraintLayout ->
                                if (secondMatched) {
                                    secondMatched = false


                                    ownerShipConstraintLayout.detail_title.text =
                                        OwnershipInterestInPropertyFragment.ownershipQuestionOne
                                    ownerShipConstraintLayout.detail_title.setTypeface(
                                        null,
                                        Typeface.NORMAL
                                    )
                                    ownerShipConstraintLayout.detail_text.text = updateEvent.answer1
                                    ownerShipConstraintLayout.detail_text.setTypeface(
                                        null,
                                        Typeface.BOLD
                                    )
                                    ownerShipConstraintLayout.visibility = View.VISIBLE
                                    ownerShipInnerScreenParams.add(updateEvent.answer1)
                                    ownershipInterestAnswerData1 = OwnershipInterestAnswerData(
                                        updateEvent.index1,
                                        updateEvent.answer1
                                    )
                                    item.answerData = ownershipInterestAnswerData1
                                } else {
                                    ownerShipConstraintLayout.detail_text2?.text =
                                        OwnershipInterestInPropertyFragment.ownershipQuestionTwo
                                    ownerShipConstraintLayout.detail_text2?.setTypeface(
                                        null,
                                        Typeface.NORMAL
                                    )
                                    ownerShipConstraintLayout.detail_text2?.text =
                                        updateEvent.answer2
                                    ownerShipConstraintLayout.detail_text2?.setTypeface(
                                        null,
                                        Typeface.BOLD
                                    )
                                    ownerShipConstraintLayout.govt_detail_box2?.visibility =
                                        View.VISIBLE
                                    ownerShipInnerScreenParams.add(updateEvent.answer2)
                                    ownershipInterestAnswerData2 = OwnershipInterestAnswerData(
                                        updateEvent.index2,
                                        updateEvent.answer2
                                    )
                                    item.answerData = OwnershipInterestAnswerData(
                                        updateEvent.index2,
                                        updateEvent.answer2
                                    )
                                }
                            }
                        }
                    }
                }
            }
            //updateGovernmentQuestionApiCall()
        }
    }


    private var bankruptcyMap = hashMapOf<String, String>()
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateBankruptcy(updateEvent: BankruptcyUpdateEvent) {
        if(updateEvent.whichBorrowerId == currentBorrowerId) {
            var displayValue = updateEvent.detailDescription
            displayValue = displayValue.trim()
            displayValue = displayValue.removeRange(displayValue.length - 1, displayValue.length)
            bankruptcyAnswerData = updateEvent.bankruptcyAnswerData
            bankruptcyConstraintLayout.govt_detail_box.detail_title.text = updateEvent.detailTitle
            bankruptcyConstraintLayout.govt_detail_box.detail_title.setTypeface(
                null,
                Typeface.NORMAL
            )
            bankruptcyConstraintLayout.govt_detail_box.detail_text.text = displayValue
            bankruptcyConstraintLayout.govt_detail_box.detail_text.setTypeface(null, Typeface.BOLD)
            bankruptcyConstraintLayout.govt_detail_box.visibility = View.VISIBLE


            //val thelist: MutableList<String, String> = ArrayList<String>()
            bankruptcyMap = hashMapOf<String, String>()
            bankruptcyMap.clear()
            //bankruptcyAnswerData = BankruptcyAnswerData()
            governmentParams.Questions.let { questions ->
                for (question in questions) {
                    question.parentQuestionId?.let { parentQuestionId ->
                        if (parentQuestionId == bankruptcyConstraintLayout.id) {
                            question.answer = "Yes"
                            question.answerDetail = bankruptcyAnswerData.extraDetail
                            if (bankruptcyAnswerData.`1`) {
                                bankruptcyMap.put("1", "Chapter 7")
                                // bankruptcyAnswerData.`1` = true
                            }

                            if (bankruptcyAnswerData.`2`) {
                                bankruptcyMap.put("2", "Chapter 11")
                                // bankruptcyAnswerData.`2` = true
                            }

                            if (bankruptcyAnswerData.`3`) {
                                bankruptcyMap.put("3", "Chapter 12")
                                //bankruptcyAnswerData.`3` = true
                            }


                            if (bankruptcyAnswerData.`4`) {
                                bankruptcyMap.put("4", "Chapter 13")
                                // bankruptcyAnswerData.`4` = true
                            }

                            question.answerData = bankruptcyMap

                        }

                    }
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateChildSupport(updateEvent: ChildSupportUpdateEvent) {
        if (updateEvent.whichBorrowerId == currentBorrowerId) {
            val list = updateEvent.childAnswerList
            childSupportAnswerDataList.clear()
            childSupportAnswerDataList = list
            if (list.size > 0) {
                childConstraintLayout.govt_detail_box.detail_title.text = list[0].liabilityName
                childConstraintLayout.govt_detail_box.detail_title.setTypeface(null, Typeface.BOLD)
                childConstraintLayout.govt_detail_box.detail_text.text =
                    "$".plus(Common.addNumberFormat(list[0].monthlyPayment.toDouble()))
                childConstraintLayout.govt_detail_box.detail_text.setTypeface(null, Typeface.NORMAL)
                //clickedContentCell.govt_detail_box.visibility = View.VISIBLE
            }
            if (list.size > 1) {
                childConstraintLayout.govt_detail_box2.detail_title2.text = list[1].liabilityName
                childConstraintLayout.govt_detail_box2.detail_title2.setTypeface(
                    null,
                    Typeface.BOLD
                )
                childConstraintLayout.govt_detail_box2.detail_text2.text =
                    "$".plus(Common.addNumberFormat(list[1].monthlyPayment.toDouble()))
                childConstraintLayout.govt_detail_box2.detail_text2.setTypeface(
                    null,
                    Typeface.NORMAL
                )
                //clickedContentCell.govt_detail_box2.visibility = View.VISIBLE
            }

            if (list.size > 2) {
                childConstraintLayout.govt_detail_box3.detail_title3.text = list[2].liabilityName
                childConstraintLayout.govt_detail_box3.detail_title3.setTypeface(
                    null,
                    Typeface.BOLD
                )
                childConstraintLayout.govt_detail_box3.detail_text3.text =
                    "$".plus(Common.addNumberFormat(list[2].monthlyPayment.toDouble()))
                childConstraintLayout.govt_detail_box3.detail_text3.setTypeface(
                    null,
                    Typeface.NORMAL
                )
                //clickedContentCell.govt_detail_box3.visibility = View.VISIBLE
            }

            showHideChildGovtBoxes(View.VISIBLE)
        }
    }

    private fun <T> stringToArray2(s: String?, clazz: Class<T>?): T {
        val newList = Gson().fromJson(s, clazz)!!
        return newList //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
    }

    private fun convertPixelsToDp(px: Float, context: Context): Int {
        return (px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    }

    private fun convertDpToPixel(dp: Float, context: Context): Int {
        return (dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    }



}






