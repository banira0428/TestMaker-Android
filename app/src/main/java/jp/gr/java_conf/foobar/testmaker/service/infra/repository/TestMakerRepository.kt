package jp.gr.java_conf.foobar.testmaker.service.infra.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.infra.local.db.WorkbookDataSource
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.qualifiers.ApplicationContext
import jp.gr.java_conf.foobar.testmaker.service.domain.Test
import jp.gr.java_conf.foobar.testmaker.service.infra.firebase.FirebaseTest
import jp.gr.java_conf.foobar.testmaker.service.infra.firebase.RemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TestMakerRepository @Inject constructor(
    private val remote: RemoteDataSource,
    private val workbookDataSource: WorkbookDataSource,
    @ApplicationContext private val context: Context,
) {

    suspend fun downloadTest(testId: String): FirebaseTest = withContext(Dispatchers.Default) {
        remote.downloadTest(testId)
    }

    fun createObjectFromFirebase(firebaseTest: FirebaseTest, source: String): Test {

        val test = firebaseTest.toTest(context)

        test.id = workbookDataSource.generateWorkbookId()
        test.order = test.id.toInt()
        test.source = source

        val questionId = workbookDataSource.generateQuestionId()

        firebaseTest.questions.forEachIndexed { index, it ->
            val question = it.toQuest()
            question.order = index
            question.id = questionId + index
            test.addQuestion(question)
        }

        workbookDataSource.createWorkbook(test)

        return Test.createFromRealmTest(test)
    }

    suspend fun createTest(test: Test, overview: String, isPublic: Boolean) =
        remote.createTest(test, overview, isPublic)

    suspend fun uploadWorkbook(test: Test, overview: String, isPublic: Boolean) =
        remote.createTest(test, overview, isPublic)

    suspend fun createTestInGroup(test: Test, overview: String, groupId: String) {
        remote.createTest(test = test, overview = overview, isPublic = false, groupId = groupId)
    }

    fun getMyTests(): LiveData<List<DocumentSnapshot>> {
        return remote.getMyTests()
    }

    fun fetchMyTests() {
        remote.fetchMyTests()
    }

    suspend fun deleteTest(id: String) {
        remote.deleteTest(id)
    }

    fun setUser(user: FirebaseUser?) {
        remote.setUser(user)
    }

    suspend fun getTestsByUserId(userId: String) = remote.getTestsByUserId(userId)

}