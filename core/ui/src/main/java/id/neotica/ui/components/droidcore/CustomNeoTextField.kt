package id.neotica.ui.components.droidcore

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun CustomNeoTextField(
    value: MutableState<String>,
    onValueChange: ((String) -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    placeHolder: String? = null,
    imeAction: ImeAction = ImeAction.Done,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isError: Boolean? = false,
    focusRequester: FocusRequester? = null,
    padding: PaddingValues = PaddingValues(),
    fillMaxWidth: Boolean = true,
    customModifier: Modifier? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    label: String? = null
) {
    var textFieldValue by remember { value }
    val keyboardController = LocalSoftwareKeyboardController.current

    focusRequester?.let {
        DisposableEffect(Unit) {
            onDispose { keyboardController?.hide() }
        }
        LaunchedEffect(Unit) {
            it.requestFocus()
        }
    }


    OutlinedTextField(
        value = textFieldValue,
        onValueChange =  {
            textFieldValue = it
            onValueChange?.invoke(it)
        },
        leadingIcon = icon,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.LightGray,
        ),
        placeholder = { Text(placeHolder?: "") },
        modifier = Modifier
            .heightIn(min = 48.dp)
            .padding(padding)
            .then(customModifier ?: Modifier)
            .then(if(fillMaxWidth) Modifier.fillMaxWidth() else Modifier)
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier),
        shape = RoundedCornerShape(10.dp),
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction
        ),
        visualTransformation = visualTransformation,
        isError = isError == true,
        trailingIcon = trailingIcon,
        maxLines = 1,
        label = {
            if (label != null) {
                Text(text = label)
            }
        }
    )
}