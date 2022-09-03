package com.rnsoft.colabademo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class GovernmentParams(
    val BorrowerId: Int = 0,
    val LoanApplicationId: Int = 5,
    val Questions: ArrayList<QuestionData> = arrayListOf()
):Parcelable



data class TestGovernmentParams(
    var BorrowerId: Int = 0,
    var LoanApplicationId: Int = 5,
    var Questions: ArrayList<ChildQuestionData> = arrayListOf()
)


data class ChildQuestionData(
    val id: Int?=null,
    val parentQuestionId:Int? = null,
    var answer: String?,
    var answerDetail: String?= "",
    val headerText:String? = "title1",
    var answerData: ArrayList<ChildAnswerData>? = null,

    val firstName: String?=null,
    val lastName: String?=null,
    val ownTypeId: Int?=null,
    val question: String?=null,
    val questionSectionId: Int?=null,
    val selectionOptionId: Int?=null
)