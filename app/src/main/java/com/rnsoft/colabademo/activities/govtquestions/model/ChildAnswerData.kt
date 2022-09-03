package com.rnsoft.colabademo

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class ChildAnswerParent(
    val childAnswerDataList: ArrayList<ChildAnswerData>
)

@Parcelize
data class ChildAnswerData(
    val liabilityName: String,
    val liabilityTypeId: Int,
    val monthlyPayment: Int,
    val name: String,
    val remainingMonth: Int
):Parcelable



data class TestChildAnswerData(
    val liabilityName: String = "Child Support two",
    val liabilityTypeId: Int = 2,
    val monthlyPayment: Int = 700,
    val name: String = "TestChildAnswerData",
    val remainingMonth: Int = 7
)

/*
 {
                    "liabilityTypeId": 1,
                    "liabilityName": "Child Support",
                    "remainingMonth": 2,
                    "monthlyPayment": 3535,
                    "name": "gdfgd"
                }
 */


 /*

   if (question.id == 140) {
                    // child support
                    if (question.id == 140) {
                        question.answerData = arrayListOf(
                            TestChildAnswerData()
                        )


                        //governmentParams.Questions.remove(question)

                    }



                    else
                    if (question.parentQuestionId != null) {
                        if (question.parentQuestionId == 20 || question.parentQuestionId == 130) {


                        }
                    }

                }
  */