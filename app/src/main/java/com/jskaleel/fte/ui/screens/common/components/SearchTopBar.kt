package com.jskaleel.fte.ui.screens.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jskaleel.fte.R
import com.jskaleel.fte.core.CallBack
import com.jskaleel.fte.core.StringCallBack
import com.jskaleel.fte.ui.theme.FTEBooksTheme

@Composable
fun SearchTopBar(
    onActiveChange: (Boolean) -> Unit,
    onQueryChange: StringCallBack,
    onClearClick: CallBack,
    onKeyboardSearchClick: StringCallBack,
    query: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp)
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(shape = RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onBackground,
                shape = RoundedCornerShape(12.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(weight = 1f)
                .onFocusChanged {
                    if (it.isFocused) onActiveChange(true)
                },
            placeholder = {
                Text(
                    text = stringResource(R.string.search_books),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = onClearClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear"
                        )
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.onBackground
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onKeyboardSearchClick(query)
                }
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchTopBarPreview() {
    FTEBooksTheme {
        SearchTopBar(
            query = "Search Query",
            onQueryChange = {},
            onClearClick = {},
            onActiveChange = {},
            onKeyboardSearchClick = {}
        )
    }
}
