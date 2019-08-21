package jp.gr.java_conf.foobar.testmaker.service.activities

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.gr.java_conf.foobar.testmaker.service.models.Cate
import jp.gr.java_conf.foobar.testmaker.service.models.Test
import jp.gr.java_conf.foobar.testmaker.service.models.TestMakerRepository

class MainViewModel(private val repository: TestMakerRepository) : ViewModel() {

    fun getTests(): List<Test> = repository.getTests()

    fun getNonCategorizedTests(): List<Test> = repository.getNonCategorizedTests()
    fun getExistingCategoryList(): List<Cate> = repository.getExistingCategoryList()

    val title: MutableLiveData<String> = MutableLiveData()
    var isEditing: MutableLiveData<Boolean> = MutableLiveData()

}