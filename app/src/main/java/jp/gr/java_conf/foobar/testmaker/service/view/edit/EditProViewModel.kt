package jp.gr.java_conf.foobar.testmaker.service.view.edit

import androidx.lifecycle.ViewModel
import jp.gr.java_conf.foobar.testmaker.service.domain.Test
import jp.gr.java_conf.foobar.testmaker.service.infra.test.TestMakerRepository

class EditProViewModel(private val repository: TestMakerRepository) : ViewModel() {

    fun getTest(testId: Long): Test = repository.getTest(testId)
    fun addOrUpdateTest(test: Test): Long = repository.addOrUpdateTest(test)
    fun getMaxQuestionId(): Long = repository.getMaxQuestionId()


}