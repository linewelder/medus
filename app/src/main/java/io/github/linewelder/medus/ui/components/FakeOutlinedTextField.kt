package io.github.linewelder.medus.ui.components

import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.*

/**
 * UI element that looks like an `OutlinedTextField`, but activates logic (`onClick`) when clicked.
 * For values that require a separate interface to edit (for example a popup dialog).
 */
@Composable
fun FakeOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    label: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    isError: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        enabled = false,
        label = label,
        textStyle =  textStyle,
        colors = colors.copy(
            disabledTextColor = colors.unfocusedTextColor,
            disabledLabelColor = if (isError) colors.errorLabelColor else colors.unfocusedLabelColor,
            disabledIndicatorColor =
                if (isError) colors.errorIndicatorColor else colors.unfocusedIndicatorColor,
            disabledContainerColor = colors.unfocusedContainerColor,
        ),
        modifier = if (onClick != null) {
            modifier.then(Modifier.clickable(onClick = onClick))
        } else {
            modifier
        },
    )
}
