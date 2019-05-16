package jp.gr.java_conf.foobar.testmaker.service.activities

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import com.isseiaoki.simplecropview.CropImageView
import jp.gr.java_conf.foobar.testmaker.service.Constants
import jp.gr.java_conf.foobar.testmaker.service.R
import jp.gr.java_conf.foobar.testmaker.service.databinding.ActivityEditBinding
import jp.gr.java_conf.foobar.testmaker.service.extensions.setImageWithGlide
import jp.gr.java_conf.foobar.testmaker.service.models.CategoryEditor
import jp.gr.java_conf.foobar.testmaker.service.models.Quest
import jp.gr.java_conf.foobar.testmaker.service.models.StructQuestion
import jp.gr.java_conf.foobar.testmaker.service.views.ColorChooser
import jp.gr.java_conf.foobar.testmaker.service.views.adapters.EditAdapter
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

/**
 * Created by keita on 2017/02/12.
 */

open class EditActivity : BaseActivity() {

    internal lateinit var editAdapter: EditAdapter

    internal var imagePath: String = ""
    internal var testId: Long = 0
    internal var questionId: Long = -1

    private lateinit var viewModel: EditViewModel

    private val fileName: String
        get() {
            val c = Calendar.getInstance()
            return c.get(Calendar.YEAR).toString() + "_" + (c.get(Calendar.MONTH) + 1) + "_" + c.get(Calendar.DAY_OF_MONTH) + "_" + c.get(Calendar.HOUR_OF_DAY) + "_" + c.get(Calendar.MINUTE) + "_" + c.get(Calendar.SECOND) + "_" + c.get(Calendar.MILLISECOND) + ".png"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        viewModel = EditViewModel()
        val binding = DataBindingUtil.setContentView<ActivityEditBinding>(this, R.layout.activity_edit)
        binding.lifecycleOwner = this
        binding.model = viewModel

        sendScreen("EditActivity")

        createAd(container)

        initToolBar()

        testId = intent.getLongExtra("testId", -1)

        realmController.migrateOrder(testId)

        initAdapter()

        initViews()

        showLayoutWrite()

        viewModel.spinnerAnswersPosition.observe(this, Observer { position ->
            position?.let {

                when (viewModel.formatQuestion.value) {
                    Constants.COMPLETE -> {
                        binding.editCompleteView.reloadAnswers(baseContext.resources.getStringArray(R.array.spinner_answers_complete)[it].toInt())
                    }
                    Constants.SELECT_COMPLETE -> {
                        binding.editSelectCompleteView.setAnswerNum(baseContext.resources.getStringArray(R.array.spinner_answers_select_complete)[it].toInt())
                    }
                }
            }
        })

        viewModel.spinnerSelectsPosition.observe(this, Observer { position ->
            position?.let {

                when (viewModel.formatQuestion.value) {
                    Constants.SELECT -> {
                        binding.editSelectView.reloadOthers(baseContext.resources.getStringArray(R.array.spinner_selects)[it].toInt() - 1)
                    }
                    Constants.SELECT_COMPLETE -> {
                        binding.editSelectCompleteView.reloadSelects(baseContext.resources.getStringArray(R.array.spinner_selects_complete)[it].toInt())
                    }
                }
            }
        })

        viewModel.isAuto.observe(this, Observer {
            sharedPreferenceManager.auto = it ?: false
            when (viewModel.formatQuestion.value) {
                Constants.SELECT -> {
                    binding.editSelectView.setAuto(it
                            ?: false, baseContext.resources.getStringArray(R.array.spinner_selects)[viewModel.spinnerSelectsPosition.value
                            ?: 0].toInt() - 1)
                }
                Constants.SELECT_COMPLETE -> {
                    binding.editSelectCompleteView.setAuto(it
                            ?: false, baseContext.resources.getStringArray(R.array.spinner_selects_complete)[viewModel.spinnerSelectsPosition.value
                            ?: 0].toInt())
                }
            }
        })

        viewModel.isCheckOrder.observe(this, Observer {
            sharedPreferenceManager.isCheckOrder = it ?: false
        })
    }

    private fun initAdapter() {

        editAdapter = EditAdapter(this, realmController, testId)

        editAdapter.setOnClickListener(object : EditAdapter.OnClickListener {
            override fun onClickEditQuestion(question: Quest) {

                showLayoutEdit()
                text_title.text = getString(R.string.edit_question)
                button_cancel.visibility = View.VISIBLE
                button_add.text = getString(R.string.save_question)
                set_problem.setText(question.problem)

                questionId = question.id

                if (question.imagePath != "") {

                    imagePath = question.imagePath

                    GlobalScope.launch(Dispatchers.Main) {
                        withContext(Dispatchers.Default) {
                            val imageOptions = BitmapFactory.Options()
                            imageOptions.inPreferredConfig = Bitmap.Config.RGB_565
                            try {

                                val input = baseContext.openFileInput(imagePath)
                                val bm = BitmapFactory.decodeStream(input, null, imageOptions)

                                input.close()

                                return@withContext bm

                            } catch (e: FileNotFoundException) {
                                e.printStackTrace()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }.let {
                            if (it is Bitmap) button_image.setImageWithGlide(baseContext, it)
                        }
                    }
                } else {
                    button_image.setImageResource(R.drawable.ic_photo_white)
                }

                viewModel.isEditingExplanation.value = question.explanation.isNotEmpty()

                when (question.type) {

                    Constants.WRITE -> {
                        showLayoutWrite()

                        set_answer_write.setText(question.answer)

                        sharedPreferenceManager.numAnswers = 1

                        button_type.text = getString(R.string.action_choose)
                    }

                    Constants.SELECT -> {

                        showLayoutSelect()

                        sharedPreferenceManager.numOthers = question.selections.size
                        edit_select_view.reloadOthers(question.selections.size)
                        edit_select_view.setAnswer(question.answer)
                        edit_select_view.setOthers(question.selections)
                        button_type.text = getString(R.string.action_write)
                        viewModel.isAuto.value = question.auto

                        edit_select_view.setAuto(sharedPreferenceManager.auto, sharedPreferenceManager.numOthers)

                    }
                    Constants.COMPLETE -> {

                        showLayoutComplete()
                        sharedPreferenceManager.numAnswers = question.answers.size
                        sharedPreferenceManager.isCheckOrder = question.isCheckOrder
                        edit_complete_view.reloadAnswers(question.answers.size)
                        edit_complete_view.setAnswers(question)
                        button_type.text = getString(R.string.action_choose)
                    }

                    Constants.SELECT_COMPLETE -> {

                        showLayoutSelectComplete()
                        sharedPreferenceManager.numAnswersSelect = question.answers.size
                        sharedPreferenceManager.numOthers = question.selections.size + question.answers.size - 1
                        edit_select_complete_view.setAnswerNum(question.answers.size)
                        edit_select_complete_view.reloadSelects(question.answers.size + question.selections.size)
                        edit_select_complete_view.setSelections(question.answers, question.selections)
                        button_type.text = getString(R.string.action_choose)
                        viewModel.isAuto.value = question.auto
                        edit_select_complete_view.setAuto(sharedPreferenceManager.auto, sharedPreferenceManager.numOthers + 1)

                    }
                }
            }

            override fun onClickDeleteQuestion(data: Quest) {

                val builder = AlertDialog.Builder(this@EditActivity, R.style.MyAlertDialogStyle)
                builder.setTitle(getString(R.string.delete_question))
                builder.setMessage(getString(R.string.message_delete, data.problem))
                builder.setPositiveButton(android.R.string.ok) { _, _ ->

                    if (data.imagePath != "") deleteFile(data.imagePath)
                    realmController.deleteQuestion(data)
                    editAdapter.notifyDataSetChanged()
                }
                builder.setNegativeButton(android.R.string.cancel, null)
                builder.create().show()
            }
        })
    }

    private fun showLayoutEdit() {

        viewModel.stateEditing.value = Constants.EDIT_QUESTION
        set_problem.isFocusable = true
        set_problem.requestFocus()
        button_add.visibility = View.VISIBLE

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        editAdapter.notifyDataSetChanged()

        if (resultCode != Activity.RESULT_OK) return
        if (resultData == null) return

        try {

            val uri = resultData.data

            val dialogLayout = LayoutInflater.from(this).inflate(R.layout.dialog_crop,
                    findViewById(R.id.layout_dialog_crop_image))

            val cropView = dialogLayout.findViewById<CropImageView>(R.id.cropImageView)
            cropView.imageBitmap = getBitmapFromUri(uri)

            val builder = AlertDialog.Builder(this@EditActivity, R.style.MyAlertDialogStyle)
            builder.setView(dialogLayout)
            builder.setTitle(getString(R.string.trim))
            builder.setPositiveButton(android.R.string.ok, null)
            builder.setNegativeButton(android.R.string.cancel, null)

            val dialog = builder.show()

            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {

                imagePath = fileName

                GlobalScope.launch(Dispatchers.Main) {
                    withContext(Dispatchers.Default) {
                        val imageOptions = BitmapFactory.Options()
                        imageOptions.inPreferredConfig = Bitmap.Config.RGB_565
                        try {

                            val outStream = baseContext.openFileOutput(imagePath, MODE_PRIVATE)
                            cropView.croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
                            outStream.close()

                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }.let {
                        button_image.setImageWithGlide(baseContext, cropView.croppedBitmap)
                    }
                }

                dialog.dismiss()
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun getBitmapFromUri(uri: Uri?): Bitmap {
        val parcelFileDescriptor = contentResolver.openFileDescriptor(uri!!, "r")
        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    }

    private fun cancelEditing() {
        hideLayoutEdit()
        button_cancel.visibility = View.GONE
        reset()
    }

    private fun hideLayoutEdit() {

        viewModel.stateEditing.value = Constants.NOT_EDITING
        button_cancel.visibility = View.GONE
        text_title.text = getString(R.string.add_question)

    }

    private fun addQuestion() {

        if (set_problem.text.toString() == "") {
            Toast.makeText(applicationContext, getString(R.string.message_shortage), Toast.LENGTH_LONG).show()
            return
        }

        when (viewModel.formatQuestion.value ?: 0) {

            Constants.WRITE -> {

                if (set_answer_write.text.toString().isEmpty()) {
                    Toast.makeText(applicationContext, getString(R.string.message_shortage), Toast.LENGTH_LONG).show()
                    return
                }

                val p = StructQuestion(set_problem.text.toString(), set_answer_write.text.toString())
                p.setImagePath(imagePath)
                p.setExplanation(set_explanation.text.toString())
                realmController.addQuestion(testId, p, questionId)
            }
            Constants.SELECT -> {

                if (!edit_select_view.isFilled()) {
                    Toast.makeText(applicationContext, getString(R.string.message_shortage), Toast.LENGTH_LONG).show()
                    return
                }

                val p = StructQuestion(set_problem.text.toString(), edit_select_view.getAnswer(), edit_select_view.getOthers())
                p.setAuto(sharedPreferenceManager.auto)
                p.setImagePath(imagePath)
                p.setExplanation(set_explanation.text.toString())
                realmController.addQuestion(testId, p, questionId)

            }

            Constants.COMPLETE -> {

                if (!edit_complete_view.isFilled()) {
                    Toast.makeText(applicationContext, getString(R.string.message_shortage), Toast.LENGTH_LONG).show()
                    return
                }

                if (edit_complete_view.isDuplicate() && !sharedPreferenceManager.isCheckOrder) {
                    Toast.makeText(applicationContext, getString(R.string.message_answer_duplicate), Toast.LENGTH_LONG).show()
                    return
                }

                val p = StructQuestion(set_problem.text.toString(), edit_complete_view.getAnswers())
                p.setImagePath(imagePath)
                p.isCheckOrder = sharedPreferenceManager.isCheckOrder
                p.setExplanation(set_explanation.text.toString())
                realmController.addQuestion(testId, p, questionId)

            }
            Constants.SELECT_COMPLETE -> {

                if (baseContext.resources.getStringArray(R.array.spinner_answers_select_complete)[viewModel.spinnerSelectsPosition.value
                                ?: 0].toInt() <= baseContext.resources.getStringArray(R.array.spinner_answers_select_complete)[viewModel.spinnerAnswersPosition.value
                                ?: 0].toInt()) {
                    Toast.makeText(applicationContext, getString(R.string.message_answers_num), Toast.LENGTH_LONG).show()
                    return
                }

                if (!edit_select_complete_view.isFilled()) {
                    Toast.makeText(applicationContext, getString(R.string.message_shortage), Toast.LENGTH_LONG).show()
                    return
                }

                val p = StructQuestion(set_problem.text.toString(), edit_select_complete_view.getAnswers(), edit_select_complete_view.getOthers())
                p.setAuto(sharedPreferenceManager.auto)
                p.isCheckOrder = false //todo 後に実装
                p.setImagePath(imagePath)
                p.setExplanation(set_explanation.text.toString())

                realmController.addQuestion(testId, p, questionId)
            }
        }

        reset()

        editAdapter.notifyDataSetChanged()

        button_cancel.visibility = View.GONE

        text_title.text = if (edit_select_view.visibility == View.GONE) getString(R.string.add_question_write) else getString(R.string.add_question_choose)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_edit, menu)

        val searchView = menu.findItem(R.id.menu_search).actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {

                editAdapter.searchWord = s
                editAdapter.filter = true
                editAdapter.notifyDataSetChanged()
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {

                editAdapter.searchWord = s
                editAdapter.filter = true
                editAdapter.notifyDataSetChanged()
                return false
            }
        })

        searchView.setOnCloseListener {

            editAdapter.searchWord = ""
            editAdapter.filter = false
            editAdapter.notifyDataSetChanged()
            false
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val actionId = item.itemId

        when {
            actionId == R.id.action_setting -> {

                val dialogLayout = LayoutInflater.from(this).inflate(R.layout.dialog_edit_test,
                        findViewById(R.id.layout_dialog_edit_test))

                val name = dialogLayout.findViewById<EditText>(R.id.edit_title)

                val buttonCate = dialogLayout.findViewById<Button>(R.id.button_category)

                val colorChooser = dialogLayout.findViewById<ColorChooser>(R.id.color_chooser)

                if (Build.VERSION.SDK_INT >= 21) buttonCate.stateListAnimator = null

                buttonCate.tag = realmController.getTest(testId).getCategory()

                if (realmController.getTest(testId).getCategory() == "") {

                    buttonCate.text = getString(R.string.category)
                } else {
                    buttonCate.text = realmController.getTest(testId).getCategory()
                }

                buttonCate.setOnClickListener {
                    val categoryEditor = CategoryEditor(this@EditActivity, buttonCate, realmController, null)
                    categoryEditor.setCategory()
                }

                buttonCate.setOnLongClickListener {

                    // アラートダイアログ を生成
                    val builder = AlertDialog.Builder(this@EditActivity, R.style.MyAlertDialogStyle)
                    builder.setMessage(getString(R.string.cancel_category))
                    builder.setPositiveButton(android.R.string.ok) { _, _ ->
                        buttonCate.tag = ""
                        buttonCate.text = getString(R.string.category)
                        buttonCate.background = ResourcesCompat.getDrawable(resources, R.drawable.button_blue, null)
                    }
                    builder.setNegativeButton(android.R.string.cancel, null)
                    builder.create().show()

                    false
                }

                name.setText(realmController.getTest(testId).title)

                colorChooser.setColorId(realmController.getTest(testId).color)

                val builder = AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                builder.setView(dialogLayout)
                builder.setTitle(getString(R.string.edit_exam))
                builder.setPositiveButton(android.R.string.ok, null)
                builder.setNegativeButton(android.R.string.cancel, null)

                val dialog = builder.show()

                val button = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                button.setOnClickListener {
                    // 場合によっては自分で明示的に閉じる必要がある
                    val sb = name.text as SpannableStringBuilder

                    if (sb.toString() == "") {

                        Toast.makeText(applicationContext, getString(R.string.message_wrong), Toast.LENGTH_SHORT).show()

                    } else {

                        realmController.updateTest(realmController.getTest(testId), sb.toString(), colorChooser.getColorId(), buttonCate.tag.toString())

                        dialog.dismiss()
                    }
                }

                dialog.show()

            }
            item.itemId == android.R.id.home -> {

                finish()

                return true
            }
            item.itemId == R.id.action_edit_pro -> {

                val i = Intent(this@EditActivity, EditProActivity::class.java)

                i.putExtra("testId", testId)
                startActivityForResult(i, 0)

                return true
            }

            item.itemId == R.id.action_reset_achievement -> {

                realmController.resetAchievement(testId)

                Toast.makeText(baseContext, getString(R.string.msg_reset_achievement), Toast.LENGTH_SHORT).show()

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun showLayoutWrite() {
        viewModel.formatQuestion.value = Constants.WRITE
    }

    fun showLayoutComplete() {
        viewModel.formatQuestion.value = Constants.COMPLETE
    }

    fun showLayoutSelect() {
        viewModel.formatQuestion.value = Constants.SELECT
    }

    fun showLayoutSelectComplete() {
        viewModel.formatQuestion.value = Constants.SELECT_COMPLETE
    }

    private fun reset() {

        set_problem.setText("")
        set_problem.requestFocus()
        set_answer_write.setText("")
        set_explanation.setText("")
        questionId = -1
        imagePath = ""
        button_image.setImageResource(R.drawable.ic_photo_white)
        button_image.setBackgroundResource(R.drawable.button_blue)

        button_add.text = getString(R.string.action_add)

        edit_select_view.reset()
        edit_complete_view.reset()
        edit_select_complete_view.reset()

        edit_select_view.setAuto(sharedPreferenceManager.auto, sharedPreferenceManager.numOthers)
        edit_select_complete_view.setAuto(sharedPreferenceManager.auto, sharedPreferenceManager.numOthers + 1)
    }

    private fun initViews() {

        if (sharedPreferenceManager.explanation) textInputLayout_explanation.visibility = View.VISIBLE

        button_expand.setOnClickListener {

            if (viewModel.stateEditing.value != Constants.NOT_EDITING) {

                hideLayoutEdit()

            } else {

                showLayoutEdit()
                text_title.text = if (edit_select_view.visibility == View.VISIBLE) getString(R.string.add_question_choose) else getString(R.string.add_question_write)

            }

            reset()
        }

        button_detail.setOnClickListener {
            viewModel.stateEditing.value = Constants.EDIT_CONFIG
        }

        radio_question.setOnCheckedChangeListener { group, checkedId ->
            val radio = findViewById<RadioButton>(checkedId)

            val tag = radio.tag
            if (tag is Int) viewModel.formatQuestion.value = tag

        }

        button_type.setOnClickListener {

            if (button_type.text == getString(R.string.action_choose)) {

                if (sharedPreferenceManager.numAnswersSelect > 1) {
                    showLayoutSelectComplete()
                    edit_select_complete_view.reloadSelects(sharedPreferenceManager.numOthers + 1)
                    edit_select_complete_view.setAnswerNum(sharedPreferenceManager.numAnswersSelect)
                    edit_select_complete_view.setAuto(sharedPreferenceManager.auto, sharedPreferenceManager.numOthers + 1)

                } else {
                    showLayoutSelect()
                    edit_select_view.reloadOthers(sharedPreferenceManager.numOthers)
                    edit_select_view.setAuto(sharedPreferenceManager.auto, sharedPreferenceManager.numOthers)

                }

                button_type.text = getString(R.string.action_write)
                text_title.text = getString(R.string.add_question_choose)

            } else {

                if (sharedPreferenceManager.numAnswers > 1) {
                    showLayoutComplete()
                } else {
                    showLayoutWrite()
                }

                button_type.text = getString(R.string.action_choose)
                text_title.text = getString(R.string.add_question_write)

            }

        }

        button_add.setOnClickListener { addQuestion() }

        button_cancel.setOnClickListener { cancelEditing() }

        button_image.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {

                if (imagePath != "") {

                    // リスト表示用のアラートダイアログ
                    val listDlg = AlertDialog.Builder(this@EditActivity, R.style.MyAlertDialogStyle)
                    listDlg.setItems(
                            resources.getStringArray(R.array.action_image)
                    ) { _, which ->

                        when (which) {
                            0 -> openImage() //差し替え
                            1 -> { //取り消し
                                imagePath = ""
                                button_image.setImageResource(R.drawable.ic_photo_white)
                                button_image.setBackgroundResource(R.drawable.button_blue)
                            }
                        }
                    }

                    listDlg.show()

                } else {
                    openImage()
                }
            }

            fun openImage() {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    startActivityForResult(Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), REQUEST_PICK_IMAGE)
                } else {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.type = "image/*"
                    startActivityForResult(intent, REQUEST_SAF_PICK_IMAGE)
                }
            }
        })


        if (Build.VERSION.SDK_INT >= 21) {
            button_add.stateListAnimator = null
            button_cancel.stateListAnimator = null
            button_type.stateListAnimator = null
            button_detail.stateListAnimator = null
        }

        recycler_view.layoutManager = LinearLayoutManager(applicationContext)
        recycler_view.setHasFixedSize(true) // アイテムは固定サイズ
        recycler_view.adapter = editAdapter

        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
            override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {

            }
            // ここで指定した方向にのみドラッグ可能

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {

                val from = viewHolder.adapterPosition
                val to = target.adapterPosition

                realmController.sortManual(from, to, testId)

                editAdapter.notifyItemMoved(from, to)

                return true
            }
        })

        touchHelper.attachToRecyclerView(recycler_view)
        recycler_view.addItemDecoration(touchHelper)
    }

    companion object {
        private const val REQUEST_PICK_IMAGE = 10011
        private const val REQUEST_SAF_PICK_IMAGE = 10012

    }
}
