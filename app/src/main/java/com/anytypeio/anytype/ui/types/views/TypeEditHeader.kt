package com.anytypeio.anytype.ui.types.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anytypeio.anytype.R
import com.anytypeio.anytype.core_ui.foundation.Dragger
import com.anytypeio.anytype.presentation.types.TypeEditViewModel
import com.anytypeio.anytype.ui.library.views.list.items.noRippleClickable
import com.anytypeio.anytype.ui.settings.fonts
import com.anytypeio.anytype.ui.settings.typography

@Composable
fun TypeEditHeader(
    vm: TypeEditViewModel
) {

    Box(modifier = Modifier.fillMaxWidth()) {
        Dragger(modifier = Modifier.align(Alignment.Center))
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(EditHeaderDefaults.Height)
            .padding(EditHeaderDefaults.PaddingValues)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(id = R.string.type_editing_title),
            style = typography.h3,
        )
        Box(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(id = R.string.type_editing_uninstall),
                color = colorResource(id = R.color.palette_system_red),
                modifier = Modifier
                    .fillMaxWidth()
                    .noRippleClickable {
                        vm.uninstallType()
                    },
                textAlign = TextAlign.End,
                style = EditHeaderDefaults.TextButtonStyle
            )
        }
    }

}

@Immutable
private object EditHeaderDefaults {
    val Height = 54.dp
    val PaddingValues = PaddingValues(start = 12.dp, top = 18.dp, end = 16.dp, bottom = 12.dp)
    val TextButtonStyle = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp
    )
}