package com.nitiaayog.apnesaathi.model

import com.google.gson.annotations.SerializedName
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants

data class AssessmentQuestions(

    @SerializedName(ApiConstants.QuestionId)
    private val id: String? = "",

    @SerializedName(ApiConstants.QuestionData)
    private val mQuestion: String? = "",

    @SerializedName(ApiConstants.AvailableOptions)
    private val mOptionsList: MutableList<AssessmentOptions>? = mutableListOf()
) {

    val questionId: String = id ?: ""
    val question: String = mQuestion ?: ""
    val optionsList: MutableList<AssessmentOptions> = mOptionsList ?: mutableListOf()
}