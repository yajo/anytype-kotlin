package com.anytypeio.anytype.ui_settings.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anytypeio.anytype.core_ui.foundation.*
import com.anytypeio.anytype.ui_settings.R

@Composable
fun AccountAndDataScreen() {
    Column {
        Box(Modifier.padding(vertical = 6.dp).align(Alignment.CenterHorizontally)) {
            Dragger()
        }
        Toolbar(stringResource(R.string.account_and_data))
        Section(stringResource(R.string.access))
        Option(
            image = R.drawable.ic_key,
            text = stringResource(R.string.keychain_phrase)
        )
        Divider(paddingStart = 60.dp)
        Pincode()
        Divider(paddingStart = 60.dp)
        Section(stringResource(R.string.data))
        Action(
            name = stringResource(R.string.clear_file_cache),
            color = colorResource(R.color.text_primary)
        )
        Divider()
        Section(stringResource(R.string.account))
        Action(
            name = stringResource(R.string.reset_account),
            color = colorResource(R.color.anytype_text_red)
        )
        Divider()
        Action(
            name = stringResource(R.string.delete_account),
            color = colorResource(R.color.anytype_text_red)
        )
        Divider()
        Box(Modifier.height(20.dp))
        Action(
            name = stringResource(R.string.log_out),
            color = colorResource(R.color.text_primary)
        )
        Divider()
        Box(Modifier.height(54.dp))
    }
}

@Composable
fun Section(name: String) {
    Box(
        modifier = Modifier.height(52.dp).fillMaxWidth(),
        contentAlignment = Alignment.BottomStart
    ) {
        Text(
            text = name,
            fontSize = 13.sp,
            modifier = Modifier.padding(
                start = 20.dp,
                bottom = 8.dp
            ),
            color = colorResource(R.color.text_secondary)
        )
    }
}

@Composable
fun Pincode() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(52.dp)
    ) {
        Image(
            painterResource(R.drawable.ic_pin_code),
            contentDescription = "Pincode icon",
            modifier = Modifier.padding(
                start = 20.dp
            )
        )
        Text(
            text = stringResource(R.string.pin_code),
            color = colorResource(R.color.text_primary),
            modifier = Modifier.padding(
                start = 12.dp
            )
        )
        Box(
            modifier = Modifier.weight(1.0f, true),
            contentAlignment = Alignment.CenterEnd
        ) {
            Row {
                Text(
                    text = stringResource(R.string.off),
                    fontSize = 17.sp,
                    color = colorResource(R.color.text_secondary),
                    modifier = Modifier.padding(end = 10.dp)
                )
                Arrow()
            }
        }
    }
}

@Composable
fun Action(
    name: String,
    color: Color = Color.Unspecified
) {
    Box(
        modifier = Modifier.height(52.dp).fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = name,
            color = color,
            fontSize = 17.sp,
            modifier = Modifier.padding(
                start = 20.dp
            )
        )
    }
}