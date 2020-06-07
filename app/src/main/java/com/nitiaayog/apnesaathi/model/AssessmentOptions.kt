package com.nitiaayog.apnesaathi.model

import com.google.gson.annotations.SerializedName
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.ApiStatus
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants

data class AssessmentOptions(

    @SerializedName(ApiConstants.OptionId)
    private val mOptionsId: String? = "",

    @SerializedName(ApiConstants.QuestionId)
    private val mQuestionId: String? = "",

    @SerializedName(ApiConstants.Option)
    private val mOptionText: String? = "",

    @SerializedName(ApiConstants.IsCorrectOption)
    private val mIsCorrectOption: String? = ""

) : ApiStatus() {

    val optionId: String = mOptionsId ?: ""
    val questionId: String = mQuestionId ?: ""
    val optionText: String = mOptionText ?: ""
    val isCorrectOption: String = mIsCorrectOption ?: ""
}