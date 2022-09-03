package com.rnsoft.colabademo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class QuestionAdapter internal constructor(
    private var questionsModel: ArrayList<BorrowerQuestionsModel> ,
    private val governmentQuestionClickListener: GovernmentQuestionClickListener,
    private val totalBorrowers:ArrayList<BorrowersInformation>?=null) :
    RecyclerView.Adapter<QuestionAdapter.BaseViewHolder>(){

    abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) { abstract fun bind(item: BorrowerQuestionsModel) }


    inner class GovtQuestionViewHolder(view: View) : BaseViewHolder(view) {
        private var questionTitle: TextView = view.findViewById(R.id.questionTitle)
        private var question: TextView = view.findViewById(R.id.question)

        private var noAnswerImage: ImageView = view.findViewById(R.id.no_question_image)



        private var answer1Name:TextView = view.findViewById(R.id.answer1_name)
        private var answer2Name:TextView = view.findViewById(R.id.answer2_name)
        private var answer3Name:TextView = view.findViewById(R.id.answer3_name)
        private var answer4Name:TextView = view.findViewById(R.id.answer4_name)
        private var answer5Name:TextView = view.findViewById(R.id.answer5_name)


        private var answer1Icon: ImageView = view.findViewById(R.id.answer1_icon)
        private var answer2Icon: ImageView = view.findViewById(R.id.answer2_icon)
        private var answer3Icon: ImageView = view.findViewById(R.id.answer3_icon)
        private var answer4Icon: ImageView = view.findViewById(R.id.answer4_icon)
        private var answer5Icon: ImageView = view.findViewById(R.id.answer5_icon)

        private var answer1Yes:TextView = view.findViewById(R.id.answer1_yes)
        private var answer2Yes:TextView = view.findViewById(R.id.answer2_no)
        private  var answer3Yes:TextView = view.findViewById(R.id.answer3_na)
        private var answer4Yes:TextView = view.findViewById(R.id.answer4_na)
        private  var answer5Yes:TextView = view.findViewById(R.id.answer5_na)

        private  var topContainer:ConstraintLayout = view.findViewById(R.id.top_container)

        init {
            topContainer.setOnClickListener {
                governmentQuestionClickListener.navigateToGovernmentActivity(adapterPosition)
            }
        }

        private var answerIconArrayList:ArrayList<ImageView> = arrayListOf(answer1Icon,answer2Icon,answer3Icon,answer4Icon,answer5Icon)
        private var borrowerNameArray:ArrayList<TextView> = arrayListOf(answer1Name, answer2Name, answer3Name, answer4Name, answer5Name)
        private var answerArrayList:ArrayList<TextView> = arrayListOf(answer1Yes, answer2Yes, answer3Yes, answer4Yes, answer5Yes)

        override fun bind(item: BorrowerQuestionsModel) {
            questionTitle.text = item.questionDetail?.questionHeader
            question.text = item.questionDetail?.questionText

            totalBorrowers?.let { totalBorrowers->
                for (i in (totalBorrowers.size - 2) until borrowerNameArray.size) {
                    borrowerNameArray[i].visibility = View.GONE
                    answerArrayList[i].visibility = View.GONE
                    answerIconArrayList[i].visibility = View.GONE
                }
                item.questionResponses?.let { answers ->

                    if (answers.isEmpty()) {
                        Timber.e(" question = " + item.questionDetail?.questionHeader)
                        noAnswerImage.visibility = View.VISIBLE
                        for (i in 0 until totalBorrowers.size - 1) {
                            borrowerNameArray[i].visibility = View.INVISIBLE
                            answerArrayList[i].visibility = View.INVISIBLE
                            answerIconArrayList[i].visibility = View.INVISIBLE
                        }
                    } else {
                        noAnswerImage.visibility = View.INVISIBLE
                        for (i in 0 until totalBorrowers.size - 2) {
                            borrowerNameArray[i].visibility = View.VISIBLE
                            answerArrayList[i].visibility = View.VISIBLE
                            answerIconArrayList[i].visibility = View.VISIBLE
                        }
                        for (answer in answers.indices) {
                            val concatStr = "- " + answers[answer].borrowerFirstName
                            borrowerNameArray[answer].text = concatStr
                            val whatAnswer = answers[answer].questionResponseText
                            if (whatAnswer.isNullOrEmpty() || whatAnswer.isNullOrBlank()) {
                                answerArrayList[answer].text = "N/a"
                                answerIconArrayList[answer].setImageResource(R.drawable.ic_answer_dash)
                            } else
                                if (whatAnswer.equals("Yes", true)) {
                                    answerIconArrayList[answer].setImageResource(R.drawable.ic_answer_tick)
                                    answerArrayList[answer].text = whatAnswer
                                } else
                                    if (whatAnswer.equals("No", true)) {
                                        answerIconArrayList[answer].setImageResource(R.drawable.ic_answer_cross)
                                        answerArrayList[answer].text = whatAnswer
                                    } else {
                                        answerArrayList[answer].text = whatAnswer
                                        answerIconArrayList[answer].setImageResource(R.drawable.ic_answer_tick)
                                    }
                        }
                    }

                    /*
                if(answer1.isNullOrEmpty() || answer1.isNullOrBlank()){


                        answer1Icon.visibility = View.GONE
                        answer1Name.visibility = View.GONE
                        answer1Yes.visibility = View.GONE

                }
                else
                    answer1Name.text = answer1

                if(answer2.isNullOrEmpty() || answer2.isNullOrBlank()){

                        answer2Icon.visibility = View.GONE
                        answer2Name.visibility = View.GONE
                        answer2No.visibility = View.GONE
                }
                else
                    answer2Name.text = answer2

                if(answer3.isNullOrEmpty() || answer3.isNullOrBlank()){
                        answer3Icon.visibility = View.GONE
                        answer3Name.visibility = View.GONE
                        answer3Na.visibility = View.GONE

                }
                else
                    answer3Name.text = answer3

                 */

                }
            }
        }
    }

    inner class DemoGraphicViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private var raceEthnicityTextView: TextView = itemView.findViewById(R.id.race_ethnicity)
        private  var topContainer:ConstraintLayout = itemView.findViewById(R.id.top_container)
        init {
            topContainer.setOnClickListener {
                governmentQuestionClickListener.navigateToGovernmentActivity(adapterPosition)
            }
        }

        private var answer1Yes:TextView = itemView.findViewById(R.id.answer1_yes)
        private var answer2Yes:TextView = itemView.findViewById(R.id.answer2_no)
        private  var answer3Yes:TextView = itemView.findViewById(R.id.answer3_na)
        private var answer4Yes:TextView = itemView.findViewById(R.id.answer4_na)
        private  var answer5Yes:TextView = itemView.findViewById(R.id.answer5_na)

        private var answer1Name:TextView = itemView.findViewById(R.id.answer1_name)
        private var answer2Name:TextView = itemView.findViewById(R.id.answer2_name)
        private var answer3Name:TextView = itemView.findViewById(R.id.answer3_name)
        private var answer4Name:TextView = itemView.findViewById(R.id.answer4_name)
        private var answer5Name:TextView = itemView.findViewById(R.id.answer5_name)



        private var borrowerNameArray :ArrayList<TextView> = arrayListOf(answer1Yes, answer2Yes, answer3Yes, answer4Yes, answer5Yes)
        private var borrowerDetailArray:ArrayList<TextView> = arrayListOf(answer1Name,answer2Name,answer3Name,answer4Name,answer5Name)

        override fun bind(item: BorrowerQuestionsModel) {

            totalBorrowers?.let { totalBorrowers ->
                for (i in (totalBorrowers.size - 2) until borrowerNameArray.size) {
                    borrowerNameArray[i].visibility = View.GONE
                    borrowerDetailArray[i].visibility = View.GONE
                }

                for (i in 0 until totalBorrowers.size - 2) {
                    borrowerNameArray[i].visibility = View.VISIBLE
                    borrowerDetailArray[i].visibility = View.VISIBLE
                    borrowerNameArray[i].text = totalBorrowers[i].firstName
                    var raceEthnicity = ""
                    item.races?.let { races ->
                        for (race in races) {
                            raceEthnicity += race.name + "/"
                            if (race.raceDetails.size > 0) {
                                var raceSubTypeString = ""
                                for (raceSubType in race.raceDetails) {
                                    raceSubTypeString = raceSubType.name + "/"
                                }
                                raceEthnicity += raceSubTypeString
                            }

                        }
                    }
                    /*
                    item.ethnicities?.let { ethnicities ->
                        for (ethnicity in ethnicities) {
                            raceEthnicity += ethnicity.name + "/"
                            ethnicity.ethnicityDetails?.let { ethnicityDetails ->
                                if (ethnicityDetails.size > 0) {
                                    var ethnicitySubTypeString = ""
                                    for (ethnicitySubType in ethnicityDetails) {
                                        ethnicitySubTypeString = ethnicitySubType.name + "/"
                                    }
                                    raceEthnicity += ethnicitySubTypeString
                                }
                            }
                        }
                    }
                    if (raceEthnicity.lastIndexOf("/") == raceEthnicity.length - 1 && raceEthnicity.length > 1) {
                        raceEthnicity = raceEthnicity.substring(0, raceEthnicity.length - 1)
                    }
                    */
                    raceEthnicity = "$raceEthnicity - Gender"
                    borrowerDetailArray[i].text = raceEthnicity
                }
            }



        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val holder: BaseViewHolder?
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        holder = if(viewType == R.layout.demographic_horizontal)
            DemoGraphicViewHolder(inflater.inflate(R.layout.demographic_horizontal, parent, false))
        else
            GovtQuestionViewHolder(inflater.inflate(R.layout.list_govt_question_horizontal, parent, false))
        return holder
    }



    override fun getItemViewType(position: Int): Int {
        return if(questionsModel[position].isDemoGraphic)
            R.layout.demographic_horizontal
        else
            R.layout.list_govt_question_horizontal
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int){ holder.bind(questionsModel[position]) }

    override fun getItemCount() = questionsModel.size







}