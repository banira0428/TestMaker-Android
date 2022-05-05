package jp.gr.java_conf.foobar.testmaker.service.view.edit.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import com.example.ui.question.EditQuestionViewModel
import jp.gr.java_conf.foobar.testmaker.service.R

@Composable
fun EditQuestionForm(
    viewModel: EditQuestionViewModel,
    fragmentManager: FragmentManager
) {

    val uiState by viewModel.uiState.collectAsState()

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column {
        InputQuestionForm(
            modifier = Modifier.weight(weight = 1f, fill = true),
            focusRequester = focusRequester,
            viewModel = viewModel,
            fragmentManager = fragmentManager
        )
        Button(
            enabled = uiState.shouldEnableCreateButton,
            onClick = {
                viewModel.onUpdateButtonClicked()
                focusRequester.requestFocus()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(text = stringResource(id = R.string.button_create_question))
        }
    }
}