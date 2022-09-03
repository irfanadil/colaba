package com.rnsoft.colabademo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class GovernmentQuestionsModelClass(
    val code: String?=null,
    @SerializedName("data") val questionData :  ArrayList<QuestionData>?=null,
    val message: String?=null,
    val status: String?=null,
    var passedBorrowerId:Int?
): Parcelable


@Parcelize
data class QuestionData(
    val id: Int?=null,
    val parentQuestionId:Int? = null,
    var answer: String?,
    var answerDetail: String?= "",

    var answerData :  @RawValue Any? = null,
    val firstName: String?=null,
    val lastName: String?=null,
    val ownTypeId: Int?=null,
    var question: String?=null,
    val questionSectionId: Int?=null,
    val selectionOptionId: Int?=null,

    val headerText:String? = "title1"
):Parcelable

