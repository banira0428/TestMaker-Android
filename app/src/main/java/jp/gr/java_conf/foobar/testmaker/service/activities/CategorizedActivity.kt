package jp.gr.java_conf.foobar.testmaker.service.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import jp.gr.java_conf.foobar.testmaker.service.R
import jp.gr.java_conf.foobar.testmaker.service.views.adapters.CategorizedAdapter
import jp.gr.java_conf.foobar.testmaker.service.views.adapters.TestAndFolderAdapter
import kotlinx.android.synthetic.main.activity_categorized.*

class CategorizedActivity : ShowTestsActivity() {

    private lateinit var adapter: TestAndFolderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorized)

        sendScreen("CategorizedActivity")

        createAd(container)

        initToolBar()

        initTestAdapter()

        parentAdapter = CategorizedAdapter(this, realmController.mixedList,
                null, realmController, intent.getStringExtra("category"),
                testAdapter

        )

        initTestAndFolderAdapter(realmController.getCategorizedList(intent.getStringExtra("category")),ArrayList())

        recycler_view.layoutManager = LinearLayoutManager(applicationContext)
        recycler_view.setHasFixedSize(true) // アイテムは固定サイズ
        recycler_view.adapter = testAndFolderAdapter

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.itemId == android.R.id.home) {

            finish()

            return true
        }

        return super.onOptionsItemSelected(item)
    }

}
