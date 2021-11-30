package jp.gr.java_conf.foobar.testmaker.service.view.online

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import jp.gr.java_conf.foobar.testmaker.service.R
import jp.gr.java_conf.foobar.testmaker.service.domain.CreateTestSource
import jp.gr.java_conf.foobar.testmaker.service.extensions.executeJobWithDialog
import jp.gr.java_conf.foobar.testmaker.service.extensions.observeNonNull
import jp.gr.java_conf.foobar.testmaker.service.extensions.showErrorToast
import jp.gr.java_conf.foobar.testmaker.service.extensions.showToast
import jp.gr.java_conf.foobar.testmaker.service.infra.db.SharedPreferenceManager
import jp.gr.java_conf.foobar.testmaker.service.infra.firebase.FirebaseTest
import jp.gr.java_conf.foobar.testmaker.service.infra.logger.TestMakerLogger
import jp.gr.java_conf.foobar.testmaker.service.view.main.MainActivity.Companion.REQUEST_NAVIGATE_HOME_PAGE
import jp.gr.java_conf.foobar.testmaker.service.view.share.DialogMenuItem
import jp.gr.java_conf.foobar.testmaker.service.view.share.ListDialogFragment
import jp.gr.java_conf.foobar.testmaker.service.view.share.component.ComposeAdView
import jp.gr.java_conf.foobar.testmaker.service.view.ui.theme.TestMakerAndroidTheme
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PublishedWorkbookListFragment: Fragment() {

    companion object {
        const val COLOR_MAX = 8F

        fun startActivity(activity: Activity) =
            activity.startActivity(
                Intent(
                    activity,
                    PublicTestsActivity::class.java,
                )
            )
    }

    private val viewModel: FirebaseViewModel by viewModel()
    private val sharedPreferenceManager: SharedPreferenceManager by inject()
    private val logger: TestMakerLogger by inject()

    @ExperimentalGraphicsApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel.getTests()

        viewModel.error.observeNonNull(this) {
            requireContext().showErrorToast(it)
        }

        return ComposeView(requireContext()).apply {
            setContent {

                val tests by viewModel.tests.observeAsState(emptyList())
                val isRefreshing by viewModel.loading.observeAsState(true)
                val isSearching = mutableStateOf(false)

                TestMakerAndroidTheme {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    if (isSearching.value) {
                                        SearchTextField(
                                            modifier = Modifier.fillMaxWidth()
                                        ){
                                            viewModel.getTests(it)
                                        }
                                    } else {
                                        Text(
                                            text = getString(R.string.label_public_tests),
                                            color = MaterialTheme.colors.onPrimary
                                        )
                                    }

                                },
                                backgroundColor = MaterialTheme.colors.primary,
                                actions = {
                                    IconButton(onClick = {
                                        isSearching.value = !isSearching.value
                                    }) {
                                        Image(
                                            painter = painterResource(
                                                id =
                                                if (isSearching.value) R.drawable.ic_close_white
                                                else R.drawable.ic_baseline_search_24
                                            ),
                                            contentDescription = "search",
                                        )
                                    }
                                },
                            )
                        },
                        content = {
                            Surface(color = MaterialTheme.colors.surface) {
                                Column {
                                    SwipeRefresh(
                                        modifier = Modifier
                                            .weight(weight = 1f, fill = true),
                                        state = rememberSwipeRefreshState(isRefreshing),
                                        onRefresh = {
                                            viewModel.getTests()
                                        }) {

                                        Column(
                                            modifier = Modifier.verticalScroll(state = rememberScrollState()),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {

                                            tests.map {
                                                ItemPublicTest(it, onClick = { test ->
                                                    onClickTest(test)
                                                })
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            logger.logEvent("upload_from_firebase_activity")
                                            UploadTestActivity.startActivity(requireActivity())
                                        },
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth(),
                                        contentPadding = PaddingValues(vertical = 16.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = MaterialTheme.colors.secondary
                                        ),

                                        ) {
                                        Text(
                                            text = getString(R.string.button_upload_test),
                                            color = MaterialTheme.colors.onSecondary
                                        )
                                    }

                                    ComposeAdView(isRemovedAd = sharedPreferenceManager.isRemovedAd)
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    private fun onClickTest(test: FirebaseTest){
        ListDialogFragment.newInstance(
            test.name,
            listOf(
                DialogMenuItem(
                    title = getString(R.string.download),
                    iconRes = R.drawable.ic_file_download_white,
                    action = { downloadTest(test) }),
                DialogMenuItem(
                    title = getString(R.string.info),
                    iconRes = R.drawable.ic_info_white,
                    action = { showInfoTest(test) }),
                DialogMenuItem(
                    title = getString(R.string.report),
                    iconRes = R.drawable.ic_baseline_flag_24,
                    action = { reportTest(test) })
            )
        ).show(
            childFragmentManager,
            "TAG"
        )
    }

    private fun downloadTest(test: FirebaseTest) {

        requireActivity().executeJobWithDialog(
            title = getString(R.string.downloading),
            task = {
                viewModel.downloadTest(test.documentId)
            },
            onSuccess = {
                viewModel.convert(it)

                Toast.makeText(
                    requireContext(),
                    getString(R.string.msg_success_download_test, it.name),
                    Toast.LENGTH_SHORT
                ).show()
                logger.logCreateTestEvent(it.name, CreateTestSource.PUBLIC_DOWNLOAD.title)

                setFragmentResult(REQUEST_NAVIGATE_HOME_PAGE, bundleOf())

            },
            onFailure = {
                requireContext().showToast(getString(R.string.msg_failure_download_test))
            }
        )
    }

    private fun showInfoTest(test: FirebaseTest) {

        ListDialogFragment.newInstance(
            test.name,
            listOf(
                DialogMenuItem(
                    title = getString(R.string.text_info_creator, test.userName),
                    iconRes = R.drawable.ic_account,
                    action = { }),
                DialogMenuItem(
                    title = getString(R.string.text_info_created_at, test.getDate()),
                    iconRes = R.drawable.ic_baseline_calendar_today_24,
                    action = { }),
                DialogMenuItem(
                    title = getString(R.string.text_info_overview, test.overview),
                    iconRes = R.drawable.ic_baseline_description_24,
                    action = { })
            )
        ).show(childFragmentManager, "TAG")

    }

    private fun reportTest(test: FirebaseTest) {

        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("testmaker.contact@gmail.com"))
        emailIntent.putExtra(
            Intent.EXTRA_SUBJECT,
            getString(R.string.report_subject, test.documentId)
        )
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.report_body))
        startActivity(Intent.createChooser(emailIntent, null))

    }
}
