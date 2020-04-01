package jp.gr.java_conf.foobar.testmaker.service.view.result

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import jp.gr.java_conf.foobar.testmaker.service.R
import jp.gr.java_conf.foobar.testmaker.service.databinding.ActivityNewResultBinding
import jp.gr.java_conf.foobar.testmaker.service.domain.Question
import jp.gr.java_conf.foobar.testmaker.service.domain.Test
import jp.gr.java_conf.foobar.testmaker.service.view.edit.EditQuestionActivity
import jp.gr.java_conf.foobar.testmaker.service.view.main.MainActivity
import jp.gr.java_conf.foobar.testmaker.service.view.main.TestViewModel
import jp.gr.java_conf.foobar.testmaker.service.view.share.BaseActivity
import jp.studyplus.android.sdk.Studyplus
import jp.studyplus.android.sdk.record.StudyRecord
import jp.studyplus.android.sdk.record.StudyRecordAmountTotal
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResultActivity : BaseActivity() {

    private lateinit var test: Test
    private val testViewModel: TestViewModel by viewModel()

    private val binding by lazy { DataBindingUtil.setContentView<ActivityNewResultBinding>(this, R.layout.activity_new_result) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        testViewModel.tests.find { it.id == intent.getLongExtra("id", -1L) }?.let {
            test = it
        }

        setSupportActionBar(binding.toolbar)
        binding.toolbar.title = test.title
        createAd(binding.adView)

        val questions = test.questions.filter { it.isSolved }


        binding.recyclerView.adapter = ResultController(this, object : ResultController.OnClickQuestionListener {
            override fun onClickQuestion(question: Question) {
                EditQuestionActivity.startActivity(this@ResultActivity, test.id, question.id)
            }

            override fun onClickHome() {
                startActivity(Intent(this@ResultActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                })
            }
        }).also {
            it.setData(questions)
        }.adapter


//        retry.setOnClickListener {
//
//            AlertDialog.Builder(this@ResultActivity, R.style.MyAlertDialogStyle)
//                    .setTitle(getString(R.string.retry))
//                    .setItems(resources.getStringArray(R.array.action_reload)) { _, which ->
//                        when (which) {
//                            0 -> {//全問やり直し
//
//                                sharedPreferenceManager.refine = false
//                                if (sharedPreferenceManager.manual) {
//                                    SelfJudgePlayActivity.startActivity(this@ResultActivity, test.id, true)
//                                } else {
//                                    PlayActivity.startActivity(this@ResultActivity, test.id, true)
//                                }
//                            }
//
//                            1 -> { //不正解のみやり直し
//
//                                if (questions.any { !it.isCorrect }) {
//
//                                    sharedPreferenceManager.refine = true
//                                    if (sharedPreferenceManager.manual) {
//                                        SelfJudgePlayActivity.startActivity(this@ResultActivity, test.id, true)
//                                    } else {
//                                        PlayActivity.startActivity(this@ResultActivity, test.id, true)
//                                    }
//                                } else {
//
//                                    Toast.makeText(applicationContext, getString(R.string.message_null_wrongs), Toast.LENGTH_SHORT).show()
//
//                                }
//                            }
//                        }
//
//                    }.show()
//        }


        val record = StudyRecord(
                duration = (intent.getLongExtra("duration", 0) / 1000).toInt(),
                amount = StudyRecordAmountTotal(questions.size),
                comment = "${test.title} で勉強しました")

        when (sharedPreferenceManager.uploadStudyPlus) {
            resources.getStringArray(R.array.upload_setting_study_plus_values)[1] ->
                uploadStudyPlus(record)
            resources.getStringArray(R.array.upload_setting_study_plus_values)[2] -> {
                // upload_study_plus.visibility = View.VISIBLE
            }
        }

//        upload_study_plus.setOnClickListener {
//            uploadStudyPlus(record)
//        }
    }

    private fun uploadStudyPlus(record: StudyRecord) {

        if (!Studyplus.instance.isAuthenticated(baseContext)) return

        Studyplus.instance.postRecord(this@ResultActivity, record,
                object : Studyplus.Companion.OnPostRecordListener {
                    override fun onResult(success: Boolean, recordId: Long?, throwable: Throwable?) {
                        if (success) {

                            Toast.makeText(baseContext, getString(R.string.msg_upload_study_plus), Toast.LENGTH_LONG).show()
                        } else {
                            throwable?.apply {
                                Toast.makeText(baseContext, getString(R.string.msg_failed_upload_study_plus), Toast.LENGTH_LONG).show()
                                printStackTrace()
                            }
                        }
                    }
                })
    }

    override fun onBackPressed() {
        startActivity(Intent(this@ResultActivity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
        super.onBackPressed()
    }

    companion object {

        fun startActivity(activity: Activity, id: Long, duration: Long) {
            val intent = Intent(activity, ResultActivity::class.java).apply {
                putExtra("id", id)
                putExtra("duration", duration)
            }
            activity.startActivity(intent)
        }
    }

}
