package jp.gr.java_conf.foobar.testmaker.service.domain

import android.os.Parcelable
import jp.gr.java_conf.foobar.testmaker.service.Constants
import jp.gr.java_conf.foobar.testmaker.service.infra.api.QuestionResponse
import jp.gr.java_conf.foobar.testmaker.service.infra.firebase.FirebaseQuestion
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Question(
        val id: Long = 0,
        val question: String = "",
        val answer: String = "",
        var explanation: String = "",
        var isCorrect: Boolean = false,
        var imagePath: String = "",
        var others: List<String> = emptyList(),
        var answers: List<String> = emptyList(),
        var type: Int = 0,
        var isAutoGenerateOthers: Boolean = false,
        var isSolved: Boolean = false,
        var order: Int = 0,
        var isCheckOrder: Boolean = false,
        var documentId: String = ""
) : Parcelable {

    fun toFirebaseQuestion(imageUrl: String = "") = FirebaseQuestion(
        question = question,
        answer = answer,
        answers = answers,
        others = others,
        explanation = explanation,
        imageRef = imageUrl,
        type = type,
        auto = isAutoGenerateOthers,
        checkOrder = isCheckOrder,
        order = order
    )


    fun toQuestionModel() = QuestionModel(
        id = id,
        problem = question,
        answer = answer,
        answers = answers,
        wrongChoices = others,
        format = format,
        imageUrl = imagePath,
        explanation = explanation,
        isAutoGenerateWrongChoices = isAutoGenerateOthers,
        isCheckOrder = isCheckOrder,
        isAnswering = isSolved,
        answerStatus = if (isCorrect) AnswerStatus.CORRECT else AnswerStatus.INCORRECT,
        order = order
    )

    @IgnoredOnParcel
    private val format = when (type) {
        Constants.WRITE -> QuestionFormat.WRITE
        Constants.SELECT -> QuestionFormat.SELECT
        Constants.COMPLETE -> QuestionFormat.COMPLETE
        Constants.SELECT_COMPLETE -> QuestionFormat.SELECT_COMPLETE
        else -> QuestionFormat.WRITE
    }

    @IgnoredOnParcel
    val hasLocalImage = imagePath.isNotEmpty() && !imagePath.contains("/")

    companion object {
        fun createFromRealmQuestion(realmQuestion: Quest) = Question(
                id = realmQuestion.id,
                question = realmQuestion.problem,
                answer = realmQuestion.answer,
                explanation = realmQuestion.explanation,
                isCorrect = realmQuestion.correct,
                imagePath = realmQuestion.imagePath,
                others = realmQuestion.selections.map { it.selection },
                answers = realmQuestion.answers.map { it.selection },
                type = realmQuestion.type,
                isAutoGenerateOthers = realmQuestion.auto,
                isSolved = realmQuestion.solving,
                order = realmQuestion.order,
                isCheckOrder = realmQuestion.isCheckOrder,
                documentId = realmQuestion.documentId
        )

        fun createFromQuestionResponse(questionResponse: QuestionResponse, order: Int) = Question(
            question = questionResponse.question,
            answer = questionResponse.answer,
            explanation = questionResponse.explanation,
            answers = questionResponse.answers,
            others = questionResponse.others,
            type = questionResponse.type,
            isCheckOrder = questionResponse.isCheckOrder,
            isAutoGenerateOthers = questionResponse.isAutoGenerateOthers,
            imagePath = questionResponse.imagePath,
            order = order
        )
    }
}