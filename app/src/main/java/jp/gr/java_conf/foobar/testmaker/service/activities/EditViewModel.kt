package jp.gr.java_conf.foobar.testmaker.service.activities

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.gr.java_conf.foobar.testmaker.service.Constants
import jp.gr.java_conf.foobar.testmaker.service.R
import jp.gr.java_conf.foobar.testmaker.service.extensions.valueNonNull
import jp.gr.java_conf.foobar.testmaker.service.models.LocalQuestion
import jp.gr.java_conf.foobar.testmaker.service.models.Quest
import jp.gr.java_conf.foobar.testmaker.service.models.TestMakerRepository
import jp.gr.java_conf.foobar.testmaker.service.views.EditCompleteView
import jp.gr.java_conf.foobar.testmaker.service.views.EditSelectCompleteView
import jp.gr.java_conf.foobar.testmaker.service.views.EditSelectView

class EditViewModel(private val repository: TestMakerRepository, val context: Context) : ViewModel() {

    val formatQuestion: MutableLiveData<Int> = MutableLiveData()
    val stateEditing: MutableLiveData<Int> = MutableLiveData()
    val spinnerAnswersPosition: MutableLiveData<Int> = MutableLiveData()
    val spinnerSelectsPosition: MutableLiveData<Int> = MutableLiveData()
    val isEditingExplanation: MutableLiveData<Boolean> = MutableLiveData()
    val isAuto: MutableLiveData<Boolean> = MutableLiveData()
    val isCheckOrder: MutableLiveData<Boolean> = MutableLiveData()

    val question: MutableLiveData<String> = MutableLiveData()
    val answer: MutableLiveData<String> = MutableLiveData()
    val explanation: MutableLiveData<String> = MutableLiveData()

    var imagePath: String = ""
    var testId: Long = -1L
    var questionId: Long = -1
    var editingView: View? = null


    init {
        spinnerAnswersPosition.value = 0
        spinnerSelectsPosition.value = 0
        formatQuestion.value = Constants.WRITE
        stateEditing.value = Constants.NOT_EDITING
        isEditingExplanation.value = false
        isAuto.value = false
        isCheckOrder.value = false
        question.value = ""
        explanation.value = ""
    }

    fun editQuestion() {
        stateEditing.value = Constants.EDIT_QUESTION
    }

    fun deleteQuestion(question: Quest) {
        repository.deleteQuestion(question)
    }

    fun getQuestions(): LiveData<ArrayList<Quest>> {
        return repository.getQuestions(testId)
    }

    fun fetchQuestions() {
        repository.fetchQuestions(testId)
    }

    fun clearQuestions() {
        repository.clearQuestions()
    }

    fun loadImage(setImage: (Bitmap) -> Unit) {
        repository.loadImage(imagePath, setImage)
    }

    fun saveImage(bitmap: Bitmap) {
        repository.saveImage(imagePath, bitmap)
    }

    fun addQuestion(onSuccess: () -> Unit, onFailure: (String) -> Unit) {

        if (question.valueNonNull().isEmpty()) onFailure(context.getString(R.string.message_shortage))

        val question = LocalQuestion(
                type = formatQuestion.valueNonNull(),
                question = question.valueNonNull(),
                imagePath = imagePath,
                explanation = explanation.valueNonNull())

        val form = editingView

        when (formatQuestion.valueNonNull()) {

            Constants.WRITE -> {

                if (answer.valueNonNull().isEmpty()) {

                    onFailure(context.getString(R.string.message_shortage))

                    return
                }
                question.answer = answer.valueNonNull()

            }
            Constants.SELECT -> {

                if(form is EditSelectView){
                    if (!form.isFilled()) {

                        onFailure(context.getString(R.string.message_shortage))

                        return
                    }

                    question.answer = form.getAnswer()
                    question.others = form.getOthers()
                    question.isAuto = repository.isAuto()
                }else{
                    return
                }
            }

            Constants.COMPLETE -> {

                if(form is EditCompleteView){
                    if (!form.isFilled()) {
                        onFailure(context.getString(R.string.message_shortage))
                        return
                    }

                    if (form.isDuplicate() && !repository.isCheckOrder()) {
                        onFailure(context.getString(R.string.message_answer_duplicate))
                        return
                    }

                    question.answers = form.getAnswers()
                    question.answers.forEach { question.answer += "$it " }
                    question.isCheckOrder = repository.isCheckOrder()
                }else{
                    return
                }
            }
            Constants.SELECT_COMPLETE -> {

                if(form is EditSelectCompleteView){
                    if (!form.isFilled()) {
                        onFailure(context.getString(R.string.message_shortage))
                        return
                    }

                    if (context.resources.getStringArray(R.array.spinner_selects_complete)[spinnerSelectsPosition.valueNonNull()].toInt()
                            <= context.resources.getStringArray(R.array.spinner_answers_select_complete)[spinnerAnswersPosition.valueNonNull()].toInt()) {
                        onFailure(context.getString(R.string.message_answers_num))
                        return
                    }

                    question.answers = form.getAnswers()
                    question.answers.forEach { question.answer += "$it " }
                    question.others = form.getOthers()
                    question.isAuto = repository.isAuto()
                    question.isCheckOrder = false //todo 後に実装
                }else{
                    return
                }
            }
        }

        repository.addQuestion(testId,question,questionId)
        fetchQuestions()
        onSuccess()
    }
}