package com.anytypeio.anytype.ui.update

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anytypeio.anytype.R
import com.anytypeio.anytype.core_ui.foundation.noRippleClickable
import com.anytypeio.anytype.presentation.update.MigrationErrorViewModel.ViewAction
import kotlinx.coroutines.launch


@Composable
fun MigrationErrorScreen(onViewAction: (ViewAction) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.background_primary))
    ) {
        Cards(onViewAction)
        CloseButton(closeClicks = { onViewAction(ViewAction.CloseScreen) })
        BackHandler(enabled = true) { onViewAction(ViewAction.CloseScreen) }
    }
}

@Composable
fun Cards(onViewAction: (ViewAction) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = stringResource(id = R.string.almost_there),
            style = MaterialTheme.typography.h2.copy(
                color = colorResource(id = R.color.text_primary)
            ),
            modifier = Modifier.padding(top = 56.dp)
        )
        Text(
            text = stringResource(id = R.string.almost_there_subtitle),
            style = MaterialTheme.typography.body1.copy(
                fontSize = 17.sp,
                color = colorResource(id = R.color.text_primary)
            ),
            modifier = Modifier.padding(top = 12.dp)
        )
        InfoCard(
            modifier = Modifier.padding(top = 32.dp),
            title = stringResource(id = R.string.i_did_not_not_complete_migration),
            toggleClick = { onViewAction(ViewAction.ToggleMigrationNotReady) },
            expanded = true,
            content = {
                val hereText = stringResource(id = R.string.here)
                val text = buildAnnotatedString {
                    append(stringResource(id = R.string.update_steps_first))
                    append(" ")
                    pushStringAnnotation(
                        tag = ANNOTATION_TAG,
                        annotation = hereText
                    )
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(hereText)
                    }
                    pop()
                    append(stringResource(R.string.update_steps_last))
                }
                ClickableText(
                    modifier = Modifier.padding(top = 12.dp),
                    text = text,
                    style = MaterialTheme.typography.body2.copy(
                        fontSize = 15.sp,
                        color = colorResource(id = R.color.text_primary),
                        lineHeight = 22.sp
                    ),
                    onClick = { offset ->
                        text.getStringAnnotations(
                            tag = ANNOTATION_TAG,
                            start = offset,
                            end = offset
                        ).firstOrNull().let {
                            if (it?.item == hereText) {
                                onViewAction(ViewAction.DownloadDesktop)
                            }
                        }
                    }
                )
            },
        )
        InfoCard(
            modifier = Modifier.padding(top = 20.dp),
            title = stringResource(id = R.string.i_completed_migration),
            expanded = false,
            toggleClick = { onViewAction(ViewAction.ToggleMigrationReady) },
            content = {
                Column() {
                    Text(
                        modifier = Modifier.padding(top = 12.dp),
                        text = stringResource(id = R.string.migration_error_msg),
                        style = MaterialTheme.typography.body2.copy(
                            fontSize = 15.sp,
                            color = colorResource(id = R.color.text_primary),
                            lineHeight = 22.sp
                        )
                    )
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 0.dp),
                        onClick = { onViewAction(ViewAction.VisitForum) },
                        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.black)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(
                            0.dp, 10.dp, 0.dp, 10.dp
                        ),
                        content = {
                            Text(
                                text = stringResource(id = R.string.visit_forum),
                                style = MaterialTheme.typography.h3.copy(
                                    color = colorResource(id = R.color.library_action_btn_text_color)
                                )
                            )
                        },
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 0.dp, pressedElevation = 0.dp
                        )
                    )
                }
            }
        )
    }
}

@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    title: String,
    expanded: Boolean,
    toggleClick: () -> Unit,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {

    val cardOpened = remember { mutableStateOf(expanded) }

    val rotationDegree = remember {
        Animatable(
            if (expanded) ROTATION_CLOSED else ROTATION_OPENED
        )
    }
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = modifier,
        backgroundColor = colorResource(id = R.color.shape_transparent),
        elevation = 0.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Box {
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_down),
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 22.dp, end = 12.dp)
                    .rotate(rotationDegree.value)
                    .noRippleClickable {
                        cardOpened.value = !cardOpened.value
                        coroutineScope.launch {
                            if (cardOpened.value) {
                                toggleClick()
                                rotationDegree.animateTo(ROTATION_CLOSED)
                            } else {
                                rotationDegree.animateTo(ROTATION_OPENED)
                            }
                        }
                    }
            )

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h2.copy(
                        color = colorResource(id = R.color.text_primary),
                        fontSize = 17.sp
                    )
                )
                AnimatedVisibility(visible = cardOpened.value) {
                    content()
                }
            }
        }
    }
}


private const val ANNOTATION_TAG = "here_text_tag"
private const val ROTATION_OPENED = 0F
private const val ROTATION_CLOSED = 180F

@Composable
private fun CloseButton(closeClicks: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(id = R.drawable.ic_navigation_close),
            contentDescription = "close image",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 12.dp, end = 12.dp)
                .noRippleClickable { closeClicks.invoke() }
        )
    }
}