package com.rnsoft.colabademo

import android.content.SharedPreferences
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
open class GovtDetailBaseFragment: BaseFragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val borrowerAppViewModel: BorrowerApplicationViewModel by activityViewModels()
    protected var updateGovernmentQuestionByBorrowerId:GovernmentParams? = null
    protected var userName:String? = null
    protected var questionId:Int = 0
    protected var whichBorrowerId:Int = 0

    protected fun fillWithData(detailTextView:TextInputEditText){
        updateGovernmentQuestionByBorrowerId?.let { updateGovernmentQuestionByBorrowerId ->
            for (item in updateGovernmentQuestionByBorrowerId.Questions) {
                if (item.id == questionId) {
                    item.answerDetail?.let {
                        detailTextView.setText(it)
                    }
                }
            }
        }
    }

    protected val backToGovernmentScreen: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findNavController().popBackStack()
        }
    }

    protected fun updateGovernmentAndSaveData(getDetailString:String , govtTitleString:String = "Detail" ) {

        updateGovernmentQuestionByBorrowerId?.let { updateGovernmentQuestionByBorrowerId ->
            for (item in updateGovernmentQuestionByBorrowerId.Questions) {
                if (item.id == questionId) {
                    item.answerDetail = getDetailString
                }
            }
            lifecycleScope.launchWhenStarted {
                sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                    borrowerAppViewModel.addOrUpdateGovernmentQuestions(authToken, updateGovernmentQuestionByBorrowerId)
                    EventBus.getDefault().post(GovtScreenUpdateEvent(govtTitleString, getDetailString , whichBorrowerId))
                    findNavController().popBackStack()
                }
            }
        }
    }

}


    /*
    private fun updateGovernmentData(testData: QuestionData){
        updateGovernmentQuestionByBorrowerId?.let { updateGovernmentQuestionByBorrowerId ->
            for (item in updateGovernmentQuestionByBorrowerId.Questions) {
                if (item.id == testData.id) {
                    item.answer = testData.answer
                }
            }
        }
    }

    private fun checkEmptyFields():Boolean{
        var bool = true
        if(binding.edDetails.text?.isEmpty() == true || binding.edDetails.text?.isBlank() == true) {
            CustomMaterialFields.setError(binding.layoutDetail, "This field is required." , requireContext())
            bool = false
        }
        else
            CustomMaterialFields.clearError(binding.layoutDetail,  requireContext())

        return bool
    }

     */



