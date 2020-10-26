package com.anytypeio.anytype.presentation.page.editor

import MockDataFactory
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.anytypeio.anytype.core_ui.features.page.BlockView
import com.anytypeio.anytype.domain.block.interactor.SplitBlock
import com.anytypeio.anytype.domain.block.interactor.UpdateTextStyle
import com.anytypeio.anytype.domain.block.model.Block
import com.anytypeio.anytype.domain.event.model.Event
import com.anytypeio.anytype.domain.ext.content
import com.anytypeio.anytype.presentation.MockBlockFactory
import com.anytypeio.anytype.presentation.page.PageViewModel
import com.anytypeio.anytype.presentation.util.CoroutinesTestRule
import com.anytypeio.anytype.presentation.util.TXT
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verifyBlocking
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.MockitoAnnotations

class EditorListBlockTest : EditorPresentationTestSetup() {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestRule = CoroutinesTestRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `should start splitting new bulleted-list item on endline-enter-pressed event inside a bullet block`() {

        // SETUP

        val style = Block.Content.Text.Style.BULLET
        val child = MockDataFactory.randomUuid()

        val page = MockBlockFactory.makeOnePageWithOneTextBlock(
            root = root,
            child = child,
            style = style
        )

        stubInterceptEvents()
        stubOpenDocument(page)
        stubSplitBlock()
        stubUpdateText()

        val vm = buildViewModel()

        val target = page.last()

        val txt = target.content<Block.Content.Text>().text

        // TESTING

        vm.onStart(root)

        vm.onBlockFocusChanged(
            id = child,
            hasFocus = true
        )

        vm.onEndLineEnterClicked(
            id = child,
            text = txt,
            marks = emptyList()
        )

        verifyBlocking(splitBlock, times(1)) {
            invoke(
                params = SplitBlock.Params(
                    context = root,
                    block = page.last(),
                    range = txt.length..txt.length,
                    isToggled = null
                )
            )
        }

        clearPendingTextUpdate()
    }

    @Test
    fun `should start creating a new checkbox item on endline-enter-pressed event inside a bullet block`() {

        // SETUP

        val style = Block.Content.Text.Style.CHECKBOX
        val child = MockDataFactory.randomUuid()

        val page = MockBlockFactory.makeOnePageWithOneTextBlock(
            root = root,
            child = child,
            style = style
        )

        stubInterceptEvents()
        stubOpenDocument(page)
        stubSplitBlock()
        stubUpdateText()

        val vm = buildViewModel()

        val target = page.last()

        val txt = target.content<Block.Content.Text>().text

        // TESTING

        vm.onStart(root)

        vm.onBlockFocusChanged(
            id = child,
            hasFocus = true
        )

        vm.onEndLineEnterClicked(
            id = child,
            text = page.last().content<Block.Content.Text>().text,
            marks = emptyList()
        )

        verifyBlocking(splitBlock, times(1)) {
            invoke(
                params = SplitBlock.Params(
                    context = root,
                    block = page.last(),
                    range = txt.length..txt.length,
                    isToggled = null
                )
            )
        }

        clearPendingTextUpdate()
    }

    @Test
    fun `should start splitting a new numbered item on endline-enter-pressed event inside a bullet block`() {

        // SETUP

        val style = Block.Content.Text.Style.NUMBERED
        val child = MockDataFactory.randomUuid()

        val page = MockBlockFactory.makeOnePageWithOneTextBlock(
            root = root,
            child = child,
            style = style
        )

        stubInterceptEvents()
        stubOpenDocument(page)
        stubSplitBlock()
        stubUpdateText()

        val vm = buildViewModel()

        val target = page.last()

        val txt = target.content<Block.Content.Text>().text

        // TESTING

        vm.onStart(root)

        vm.onBlockFocusChanged(
            id = child,
            hasFocus = true
        )

        vm.onEndLineEnterClicked(
            id = child,
            text = page.last().content<Block.Content.Text>().text,
            marks = emptyList()
        )

        verifyBlocking(splitBlock, times(1)) {
            invoke(
                params = SplitBlock.Params(
                    context = root,
                    block = page.last(),
                    range = txt.length..txt.length,
                    isToggled = null
                )
            )
        }

        clearPendingTextUpdate()
    }

    @Test
    fun `should start splitting toggle block on endline-enter-pressed event inside a bullet block`() {

        val style = Block.Content.Text.Style.TOGGLE
        val child = MockDataFactory.randomUuid()

        val page = MockBlockFactory.makeOnePageWithOneTextBlock(
            root = root,
            child = child,
            style = style
        )

        val target = page.last()

        val txt = target.content<Block.Content.Text>().text

        stubInterceptEvents()
        stubOpenDocument(page)
        stubUpdateText()
        stubSplitBlock()

        val vm = buildViewModel()

        vm.onStart(root)

        vm.onBlockFocusChanged(
            id = child,
            hasFocus = true
        )

        vm.onEndLineEnterClicked(
            id = child,
            text = target.content<Block.Content.Text>().text,
            marks = emptyList()
        )

        verifyBlocking(splitBlock, times(1)) {
            invoke(
                params = SplitBlock.Params(
                    context = root,
                    block = page.last(),
                    range = txt.length..txt.length,
                    isToggled = false
                )
            )
        }

        clearPendingTextUpdate()
    }

    @Test
    fun `should convert checkbox block with empty text to paragraph on enter-pressed event`() {

        // SETUP

        val title = Block(
            id = MockDataFactory.randomUuid(),
            content = Block.Content.Text(
                text = MockDataFactory.randomString(),
                style = Block.Content.Text.Style.TITLE,
                marks = emptyList()
            ),
            children = emptyList(),
            fields = Block.Fields.empty()
        )

        val header = Block(
            id = MockDataFactory.randomUuid(),
            content = Block.Content.Layout(
                type = Block.Content.Layout.Type.HEADER
            ),
            fields = Block.Fields.empty(),
            children = listOf(title.id)
        )

        val style = Block.Content.Text.Style.CHECKBOX
        val child = MockDataFactory.randomUuid()

        val checkbox = Block(
            id = child,
            fields = Block.Fields(emptyMap()),
            content = Block.Content.Text(
                text = "",
                marks = emptyList(),
                style = style
            ),
            children = emptyList()
        )

        val page = listOf(
            Block(
                id = root,
                fields = Block.Fields(emptyMap()),
                content = Block.Content.Smart(
                    type = Block.Content.Smart.Type.PAGE
                ),
                children = listOf(header.id, child)
            ),
            header,
            title,
            checkbox
        )

        stubInterceptEvents()
        stubOpenDocument(page)
        stubCreateBlock(root)

        stubUpdateTextStyle(
            events = listOf(
                Event.Command.GranularChange(
                    context = root,
                    id = child,
                    style = Block.Content.Text.Style.P
                )
            )
        )

        val vm = buildViewModel()

        // TESTING

        vm.onStart(root)

        vm.onBlockFocusChanged(
            id = child,
            hasFocus = true
        )

        // expected state before on-enter-pressed event

        val before = ViewState.Success(
            blocks = listOf(
                BlockView.Title.Document(
                    id = title.id,
                    text = title.content<TXT>().text,
                    isFocused = false
                ),
                BlockView.Text.Checkbox(
                    id = child,
                    text = "",
                    isFocused = false,
                    isChecked = false,
                    indent = 0
                )
            )
        )

        vm.state.test().assertValue(before)

        vm.onEndLineEnterClicked(
            id = child,
            marks = emptyList(),
            text = page.last().content<Block.Content.Text>().text
        )

        verifyBlocking(updateTextStyle, times(1)) {
            invoke(
                params = eq(
                    UpdateTextStyle.Params(
                        context = root,
                        targets = listOf(child),
                        style = Block.Content.Text.Style.P
                    )
                )
            )
        }

        verifyZeroInteractions(createBlock)

        // expected state after on-enter-pressed event

        val after = ViewState.Success(
            blocks = listOf(
                BlockView.Title.Document(
                    id = title.id,
                    text = title.content<TXT>().text,
                    isFocused = false
                ),
                BlockView.Text.Paragraph(
                    id = child,
                    text = "",
                    isFocused = true
                )
            )
        )

        vm.state.test().assertValue(after)
    }

    @Test
    fun `should convert bullet block with empty text to paragraph on enter-pressed event`() {

        // SETUP

        val title = Block(
            id = MockDataFactory.randomUuid(),
            content = Block.Content.Text(
                text = MockDataFactory.randomString(),
                style = Block.Content.Text.Style.TITLE,
                marks = emptyList()
            ),
            children = emptyList(),
            fields = Block.Fields.empty()
        )

        val header = Block(
            id = MockDataFactory.randomUuid(),
            content = Block.Content.Layout(
                type = Block.Content.Layout.Type.HEADER
            ),
            fields = Block.Fields.empty(),
            children = listOf(title.id)
        )

        val style = Block.Content.Text.Style.BULLET
        val child = MockDataFactory.randomUuid()

        val checkbox = Block(
            id = child,
            fields = Block.Fields(emptyMap()),
            content = Block.Content.Text(
                text = "",
                marks = emptyList(),
                style = style
            ),
            children = emptyList()
        )

        val page = listOf(
            Block(
                id = root,
                fields = Block.Fields(emptyMap()),
                content = Block.Content.Smart(
                    type = Block.Content.Smart.Type.PAGE
                ),
                children = listOf(header.id, child)
            ),
            header,
            title,
            checkbox
        )

        stubInterceptEvents()
        stubOpenDocument(page)
        stubCreateBlock(root)

        stubUpdateTextStyle(
            events = listOf(
                Event.Command.GranularChange(
                    context = root,
                    id = child,
                    style = Block.Content.Text.Style.P
                )
            )
        )

        val vm = buildViewModel()

        // TESTING

        vm.onStart(root)

        vm.onBlockFocusChanged(
            id = child,
            hasFocus = true
        )

        // expected state before on-enter-pressed event

        val before = ViewState.Success(
            blocks = listOf(
                BlockView.Title.Document(
                    id = title.id,
                    text = title.content<TXT>().text,
                    isFocused = false
                ),
                BlockView.Text.Bulleted(
                    id = child,
                    text = "",
                    isFocused = false,
                    indent = 0
                )
            )
        )

        vm.state.test().assertValue(before)

        vm.onEndLineEnterClicked(
            id = child,
            marks = emptyList(),
            text = page.last().content<Block.Content.Text>().text
        )

        verifyBlocking(updateTextStyle, times(1)) {
            invoke(
                params = eq(
                    UpdateTextStyle.Params(
                        context = root,
                        targets = listOf(child),
                        style = Block.Content.Text.Style.P
                    )
                )
            )
        }

        verifyZeroInteractions(createBlock)

        // expected state after on-enter-pressed event

        val after = ViewState.Success(
            blocks = listOf(
                BlockView.Title.Document(
                    id = title.id,
                    text = title.content<TXT>().text,
                    isFocused = false
                ),
                BlockView.Text.Paragraph(
                    id = child,
                    text = "",
                    isFocused = true
                )
            )
        )

        vm.state.test().assertValue(after)
    }

    @Test
    fun `should convert toggle block with empty text to paragraph on enter-pressed event`() {

        // SETUP

        val style = Block.Content.Text.Style.TOGGLE
        val child = MockDataFactory.randomUuid()

        val title = Block(
            id = MockDataFactory.randomUuid(),
            content = Block.Content.Text(
                text = MockDataFactory.randomString(),
                style = Block.Content.Text.Style.TITLE,
                marks = emptyList()
            ),
            children = emptyList(),
            fields = Block.Fields.empty()
        )

        val header = Block(
            id = MockDataFactory.randomUuid(),
            content = Block.Content.Layout(
                type = Block.Content.Layout.Type.HEADER
            ),
            fields = Block.Fields.empty(),
            children = listOf(title.id)
        )

        val checkbox = Block(
            id = child,
            fields = Block.Fields(emptyMap()),
            content = Block.Content.Text(
                text = "",
                marks = emptyList(),
                style = style
            ),
            children = emptyList()
        )

        val page = listOf(
            Block(
                id = root,
                fields = Block.Fields(emptyMap()),
                content = Block.Content.Smart(
                    type = Block.Content.Smart.Type.PAGE
                ),
                children = listOf(header.id, child)
            ),
            header,
            title,
            checkbox
        )

        stubInterceptEvents()
        stubOpenDocument(page)
        stubCreateBlock(root)

        stubUpdateTextStyle(
            events = listOf(
                Event.Command.GranularChange(
                    context = root,
                    id = child,
                    style = Block.Content.Text.Style.P
                )
            )
        )

        val vm = buildViewModel()

        // TESTING

        vm.onStart(root)

        vm.onBlockFocusChanged(
            id = child,
            hasFocus = true
        )

        // expected state before on-enter-pressed event

        val before = ViewState.Success(
            blocks = listOf(
                BlockView.Title.Document(
                    id = title.id,
                    text = title.content<Block.Content.Text>().text,
                    isFocused = false
                ),
                BlockView.Text.Toggle(
                    id = child,
                    text = "",
                    isFocused = false,
                    indent = 0,
                    isEmpty = true
                )
            )
        )

        vm.state.test().assertValue(before)

        vm.onEndLineEnterClicked(
            id = child,
            marks = emptyList(),
            text = page.last().content<Block.Content.Text>().text
        )

        verifyBlocking(updateTextStyle, times(1)) {
            invoke(
                params = eq(
                    UpdateTextStyle.Params(
                        context = root,
                        targets = listOf(child),
                        style = Block.Content.Text.Style.P
                    )
                )
            )
        }

        verifyZeroInteractions(createBlock)

        // expected state after on-enter-pressed event

        val after = ViewState.Success(
            blocks = listOf(
                BlockView.Title.Document(
                    id = title.id,
                    text = title.content<Block.Content.Text>().text,
                    isFocused = false
                ),
                BlockView.Text.Paragraph(
                    id = child,
                    text = "",
                    isFocused = true
                )
            )
        )

        vm.state.test().assertValue(after)
    }

    @Test
    fun `should convert numbered block with empty text to paragraph on enter-pressed event`() {

        // SETUP

        val title = Block(
            id = MockDataFactory.randomUuid(),
            content = Block.Content.Text(
                text = MockDataFactory.randomString(),
                style = Block.Content.Text.Style.TITLE,
                marks = emptyList()
            ),
            children = emptyList(),
            fields = Block.Fields.empty()
        )

        val header = Block(
            id = MockDataFactory.randomUuid(),
            content = Block.Content.Layout(
                type = Block.Content.Layout.Type.HEADER
            ),
            fields = Block.Fields.empty(),
            children = listOf(title.id)
        )

        val style = Block.Content.Text.Style.NUMBERED
        val child = MockDataFactory.randomUuid()

        val checkbox = Block(
            id = child,
            fields = Block.Fields(emptyMap()),
            content = Block.Content.Text(
                text = "",
                marks = emptyList(),
                style = style
            ),
            children = emptyList()
        )

        val page = listOf(
            Block(
                id = root,
                fields = Block.Fields(emptyMap()),
                content = Block.Content.Smart(
                    type = Block.Content.Smart.Type.PAGE
                ),
                children = listOf(header.id, child)
            ),
            header,
            title,
            checkbox
        )

        stubInterceptEvents()
        stubOpenDocument(page)
        stubCreateBlock(root)

        stubUpdateTextStyle(
            events = listOf(
                Event.Command.GranularChange(
                    context = root,
                    id = child,
                    style = Block.Content.Text.Style.P
                )
            )
        )

        val vm = buildViewModel()

        // TESTING

        vm.onStart(root)

        vm.onBlockFocusChanged(
            id = child,
            hasFocus = true
        )

        // expected state before on-enter-pressed event

        val before = ViewState.Success(
            blocks = listOf(
                BlockView.Title.Document(
                    id = title.id,
                    text = title.content<Block.Content.Text>().text,
                    isFocused = false
                ),
                BlockView.Text.Numbered(
                    id = child,
                    text = "",
                    isFocused = false,
                    indent = 0,
                    number = 1
                )
            )
        )

        vm.state.test().assertValue(before)

        vm.onEndLineEnterClicked(
            id = child,
            marks = emptyList(),
            text = page.last().content<TXT>().text
        )

        verifyBlocking(updateTextStyle, times(1)) {
            invoke(
                params = eq(
                    UpdateTextStyle.Params(
                        context = root,
                        targets = listOf(child),
                        style = Block.Content.Text.Style.P
                    )
                )
            )
        }

        verifyZeroInteractions(createBlock)

        // expected state after on-enter-pressed event

        val after = ViewState.Success(
            blocks = listOf(
                BlockView.Title.Document(
                    id = title.id,
                    text = title.content<Block.Content.Text>().text,
                    isFocused = false
                ),
                BlockView.Text.Paragraph(
                    id = child,
                    text = "",
                    isFocused = true
                )
            )
        )

        vm.state.test().assertValue(after)
    }

    private fun clearPendingTextUpdate() {
        coroutineTestRule.advanceTime(PageViewModel.TEXT_CHANGES_DEBOUNCE_DURATION)
    }
}