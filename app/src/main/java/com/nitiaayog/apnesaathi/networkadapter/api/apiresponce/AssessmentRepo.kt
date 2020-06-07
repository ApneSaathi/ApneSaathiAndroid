package com.nitiaayog.apnesaathi.networkadapter.api.apiresponce

import com.google.gson.annotations.SerializedName
import com.nitiaayog.apnesaathi.model.AssessmentQuestions
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants

class AssessmentRepo(
    @SerializedName(ApiConstants.AssessmentQuestions)
    private val mQuestionsList: MutableList<AssessmentQuestions>? = mutableListOf()
) : BaseRepo() {

    val questionsList: MutableList<AssessmentQuestions> = mQuestionsList ?: mutableListOf()
}