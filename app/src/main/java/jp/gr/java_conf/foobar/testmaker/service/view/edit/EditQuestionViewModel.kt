package jp.gr.java_conf.foobar.testmaker.service.view.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.gr.java_conf.foobar.testmaker.service.domain.Question

class EditQuestionViewModel : ViewModel() {

    var testId = -1L
    var selectedQuestion = Question()
        set(value) {
            field = value
            inputForm(field)
        }

    val question = MutableLiveData("")
    val answer = MutableLiveData("")
    val explanation = MutableLiveData("")
    val isCheckedImage = MutableLiveData(false)
    val isCheckedAuto = MutableLiveData(false)
    val isCheckedExplanation = MutableLiveData(false)
    val isResetForm = MutableLiveData(true)
    val imagePath = MutableLiveData("")
    val sizeOfOthers = MutableLiveData(2)
    val sizeOfAnswers = MutableLiveData(2)
    val isVisibleSetting = MutableLiveData(false)

    private fun inputForm(question: Question) {
        this.question.value = question.question
        answer.value = question.answer
        explanation.value = question.explanation
        isCheckedExplanation.value = question.explanation.isNotEmpty()
        imagePath.value = question.imagePath
        isCheckedImage.value = question.imagePath.isNotEmpty()
        isCheckedAuto.value = question.isAutoGenerateOthers
    }

    fun onClickSetting() {
        isVisibleSetting.value = !(isVisibleSetting.value ?: false)
    }
}