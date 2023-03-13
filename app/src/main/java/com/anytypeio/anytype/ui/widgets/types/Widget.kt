package com.anytypeio.anytype.ui.widgets.types

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anytypeio.anytype.R

@Composable
fun EmptyWidgetPlaceholder(
    @StringRes text: Int
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = text),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = 10.dp),
            style = TextStyle(
                fontSize = 13.sp,
                color = colorResource(id = R.color.text_secondary)
            )
        )
    }
}