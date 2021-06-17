package com.anytypeio.anytype.presentation.page.editor

import MockDataFactory
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.anytypeio.anytype.core_models.Block
import com.anytypeio.anytype.domain.block.interactor.UpdateText
import com.anytypeio.anytype.presentation.MockTypicalDocumentFactory
import com.anytypeio.anytype.presentation.page.PageViewModel.Companion.TEXT_CHANGES_DEBOUNCE_DURATION
import com.anytypeio.anytype.presentation.page.editor.model.BlockView
import com.anytypeio.anytype.presentation.page.editor.slash.SlashEvent
import com.anytypeio.anytype.presentation.page.editor.slash.SlashItem
import com.anytypeio.anytype.presentation.util.CoroutinesTestRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.times
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.verifyZeroInteractions
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(RobolectricTestRunner::class)
class EditorSlashWidgetCutFilterTest : EditorPresentationTestSetup() {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestRule = CoroutinesTestRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @After
    fun after() {
        coroutineTestRule.advanceTime(TEXT_CHANGES_DEBOUNCE_DURATION)
    }

    @Test
    fun `should invoke updateText useCase when clicked on Slash Item Text`() {
        `should invoke updateText useCase when clicked on Slash Item Style`(
            slashItem = SlashItem.Style.Type.Text
        )
    }

    @Test
    fun `should invoke updateText useCase when clicked on Slash Item Title`() {
        `should invoke updateText useCase when clicked on Slash Item Style`(
            slashItem = SlashItem.Style.Type.Title
        )
    }

    @Test
    fun `should invoke error toast when clicked on Slash Item Callout`() {
        val block = MockTypicalDocumentFactory.a
        val page = MockTypicalDocumentFactory.page("root")

        stubInterceptEvents()
        stubUpdateText()
        stubGetObjectTypes(objectTypes = listOf())
        stubOpenDocument(document = page)
        val vm = buildViewModel()

        vm.onStart(root)

        val toasts = mutableListOf<String>()

        runBlockingTest {

            val subscription = launch { vm.toasts.collect { toasts.add(it) } }

            vm.apply {

                onSelectionChanged(
                    id = block.id,
                    selection = IntRange(3, 3)
                )
                onBlockFocusChanged(
                    id = block.id,
                    hasFocus = true
                )
                onSlashTextWatcherEvent(
                    event = SlashEvent.Start(
                        cursorCoordinate = 820,
                        slashStart = 3
                    )
                )
                onSlashTextWatcherEvent(
                    event = SlashEvent.Filter(
                        filter = "/",
                        viewType = 0
                    )
                )
            }

            //TESTING

            vm.onSlashItemClicked(
                item = SlashItem.Style.Type.Callout
            )

            verifyZeroInteractions(updateText)

            assertEquals(
                expected = 1,
                actual = toasts.size
            )

            subscription.cancel()
        }
    }

    @Test
    fun `should invoke updateText useCase when clicked on Slash Item Heading`() {
        `should invoke updateText useCase when clicked on Slash Item Style`(
            slashItem = SlashItem.Style.Type.Heading
        )
    }

    @Test
    fun `should invoke updateText useCase when clicked on Slash Item Subheading`() {
        `should invoke updateText useCase when clicked on Slash Item Style`(
            slashItem = SlashItem.Style.Type.Subheading
        )
    }

    @Test
    fun `should invoke updateText useCase when clicked on Slash Item Highlighted`() {
        `should invoke updateText useCase when clicked on Slash Item Style`(
            slashItem = SlashItem.Style.Type.Highlighted
        )
    }

    @Test
    fun `should invoke updateText useCase when clicked on Slash Item Checkbox`() {
        `should invoke updateText useCase when clicked on Slash Item Style`(
            slashItem = SlashItem.Style.Type.Checkbox
        )
    }

    @Test
    fun `should invoke updateText useCase when clicked on Slash Item Bullet`() {
        `should invoke updateText useCase when clicked on Slash Item Style`(
            slashItem = SlashItem.Style.Type.Bulleted
        )
    }

    @Test
    fun `should invoke updateText useCase when clicked on Slash Item Numbered`() {
        `should invoke updateText useCase when clicked on Slash Item Style`(
            slashItem = SlashItem.Style.Type.Numbered
        )
    }

    @Test
    fun `should invoke updateText useCase when clicked on Slash Item Toggle`() {
        `should invoke updateText useCase when clicked on Slash Item Style`(
            slashItem = SlashItem.Style.Type.Toggle
        )
    }

    private fun `should invoke updateText useCase when clicked on Slash Item Style`(
        slashItem: SlashItem
    ) {

        val header = MockTypicalDocumentFactory.header
        val title = MockTypicalDocumentFactory.title

        val block = Block(
            id = MockDataFactory.randomUuid(),
            fields = Block.Fields.empty(),
            children = emptyList(),
            content = Block.Content.Text(
                text = "FooBar",
                marks = listOf(
                    Block.Content.Text.Mark(
                        range = IntRange(0, 6),
                        type = Block.Content.Text.Mark.Type.BOLD
                    )
                ),
                style = Block.Content.Text.Style.P
            )
        )

        val page = Block(
            id = root,
            fields = Block.Fields(emptyMap()),
            content = Block.Content.Smart(),
            children = listOf(header.id, block.id)
        )

        val doc = listOf(page, header, title, block)

        stubInterceptEvents()
        stubUpdateText()
        stubTurnIntoStyle()
        stubGetObjectTypes(objectTypes = listOf())
        stubOpenDocument(document = doc)
        val vm = buildViewModel()

        vm.onStart(root)

        vm.apply {
            onSelectionChanged(
                id = block.id,
                selection = IntRange(3, 3)
            )
            onBlockFocusChanged(
                id = block.id,
                hasFocus = true
            )
            onSlashTextWatcherEvent(
                event = SlashEvent.Start(
                    cursorCoordinate = 820,
                    slashStart = 3
                )
            )
            onSlashTextWatcherEvent(
                event = SlashEvent.Filter(
                    filter = "/",
                    viewType = 0
                )
            )
            onTextBlockTextChanged(
                view = BlockView.Text.Paragraph(
                    id = block.id,
                    text = "Foo/Bar",
                    marks = listOf(
                        Markup.Mark(
                            from = 0,
                            to = 7,
                            type = Markup.Type.BOLD
                        )
                    ),
                    isFocused = true,
                    mode = BlockView.Mode.EDIT,
                    isSelected = false,
                    cursor = null
                )
            )
            onSelectionChanged(
                id = block.id,
                selection = IntRange(4, 4)
            )
            onSlashTextWatcherEvent(
                event = SlashEvent.Filter(
                    filter = "/s",
                    viewType = 0
                )
            )
            onTextBlockTextChanged(
                view = BlockView.Text.Paragraph(
                    id = block.id,
                    text = "Foo/sBar",
                    marks = listOf(
                        Markup.Mark(
                            from = 0,
                            to = 8,
                            type = Markup.Type.BOLD
                        )
                    ),
                    isFocused = true,
                    mode = BlockView.Mode.EDIT,
                    isSelected = false,
                    cursor = null
                )
            )
            onSelectionChanged(
                id = block.id,
                selection = IntRange(5, 5)
            )
            onSlashTextWatcherEvent(
                event = SlashEvent.Filter(
                    filter = "/su",
                    viewType = 0
                )
            )
            onTextBlockTextChanged(
                view = BlockView.Text.Paragraph(
                    id = block.id,
                    text = "Foo/suBar",
                    marks = listOf(
                        Markup.Mark(
                            from = 0,
                            to = 9,
                            type = Markup.Type.BOLD
                        )
                    ),
                    isFocused = true,
                    mode = BlockView.Mode.EDIT,
                    isSelected = false,
                    cursor = null
                )
            )
            onSelectionChanged(
                id = block.id,
                selection = IntRange(6, 6)
            )
        }

        //TESTING

        vm.onSlashItemClicked(
            item = slashItem
        )

        val params = UpdateText.Params(
            context = root,
            target = block.id,
            text = "FooBar",
            marks = listOf(
                Block.Content.Text.Mark(
                    range = IntRange(0, 6),
                    type = Block.Content.Text.Mark.Type.BOLD
                )
            )
        )

        verifyBlocking(updateText, times(1)) { invoke(params) }
    }
}