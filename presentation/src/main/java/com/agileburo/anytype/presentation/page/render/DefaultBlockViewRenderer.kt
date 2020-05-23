package com.agileburo.anytype.presentation.page.render

import com.agileburo.anytype.core_ui.features.page.BlockView
import com.agileburo.anytype.core_utils.tools.Counter
import com.agileburo.anytype.domain.block.model.Block
import com.agileburo.anytype.domain.block.model.Block.Content
import com.agileburo.anytype.domain.common.Id
import com.agileburo.anytype.domain.misc.UrlBuilder
import com.agileburo.anytype.domain.page.EditorMode
import com.agileburo.anytype.presentation.mapper.*
import com.agileburo.anytype.presentation.page.toggle.ToggleStateHolder

class DefaultBlockViewRenderer(
    private val counter: Counter,
    private val urlBuilder: UrlBuilder,
    private val toggleStateHolder: ToggleStateHolder
) : BlockViewRenderer, ToggleStateHolder by toggleStateHolder {

    override suspend fun Map<Id, List<Block>>.render(
        mode: EditorMode,
        root: Block,
        focus: Id,
        anchor: Id,
        indent: Int,
        details: Block.Details
    ): List<BlockView> {

        val children = getValue(anchor)

        val result = mutableListOf<BlockView>()

        buildTitle(
            mode = mode,
            anchor = anchor,
            root = root,
            result = result,
            details = details,
            focus = focus
        )

        counter.reset()

        children.forEach { block ->
            when (val content = block.content) {
                is Content.Text -> {
                    when (content.style) {
                        Content.Text.Style.TITLE -> {
                            counter.reset()
                            result.add(title(mode, block, content, root, focus))
                        }
                        Content.Text.Style.P -> {
                            counter.reset()
                            result.add(paragraph(mode, block, content, focus, indent))
                        }
                        Content.Text.Style.NUMBERED -> {
                            counter.inc()
                            result.add(
                                numbered(
                                    mode,
                                    block,
                                    content,
                                    counter.current(),
                                    focus,
                                    indent
                                )
                            )
                        }
                        Content.Text.Style.TOGGLE -> {
                            counter.reset()
                            result.add(
                                toggle(
                                    mode = mode,
                                    block = block,
                                    content = content,
                                    indent = indent,
                                    isEmpty = block.children.isEmpty(),
                                    focus = focus
                                )
                            )
                            if (toggleStateHolder.isToggled(block.id)) {
                                result.addAll(
                                    render(
                                        mode = mode,
                                        focus = focus,
                                        indent = indent.inc(),
                                        anchor = block.id,
                                        root = root,
                                        details = details
                                    )
                                )
                            }
                        }
                        Content.Text.Style.H1 -> {
                            counter.reset()
                            result.add(headerOne(mode, block, content, indent))
                        }
                        Content.Text.Style.H2 -> {
                            counter.reset()
                            result.add(headerTwo(mode, block, content, indent))
                        }
                        Content.Text.Style.H3, Content.Text.Style.H4 -> {
                            counter.reset()
                            result.add(headerThree(mode, block, content, indent))
                        }
                        Content.Text.Style.QUOTE -> {
                            counter.reset()
                            result.add(highlight(mode, block, content, indent))
                        }
                        Content.Text.Style.CODE_SNIPPET -> {
                            counter.reset()
                            result.add(code(mode, block, content, focus))
                        }
                        Content.Text.Style.BULLET -> {
                            counter.reset()
                            result.add(bulleted(mode, block, content, focus, indent))
                        }
                        Content.Text.Style.CHECKBOX -> {
                            counter.reset()
                            result.add(checkbox(mode, block, content, focus, indent))
                        }
                    }
                }
                is Content.Bookmark -> {
                    counter.reset()
                    result.add(bookmark(mode, content, block, indent))
                }
                is Content.Divider -> {
                    counter.reset()
                    result.add(divider(block))
                }
                is Content.Link -> {
                    counter.reset()
                    result.add(page(block, content, indent, details))
                }
                is Content.File -> {
                    counter.reset()
                    result.add(file(mode, content, block, indent))
                }
                is Content.Layout -> {
                    counter.reset()
                    result.addAll(
                        render(
                            mode = mode,
                            focus = focus,
                            indent = indent,
                            anchor = block.id,
                            root = root,
                            details = details
                        )
                    )
                }
            }
        }

        return result
    }

    private fun buildTitle(
        mode: EditorMode,
        anchor: Id,
        root: Block,
        result: MutableList<BlockView>,
        details: Block.Details,
        focus: Id
    ) {
        if (anchor == root.id) {
            result.add(
                BlockView.Title(
                    mode = if (mode == EditorMode.EDITING) BlockView.Mode.EDIT else BlockView.Mode.READ,
                    id = anchor,
                    focused = anchor == focus,
                    text = details.details[root.id]?.name,
                    emoji = details.details[root.id]?.icon?.let { name ->
                        if (name.isNotEmpty())
                            name
                        else
                            null
                    }
                )
            )
        }
    }

    private fun paragraph(
        mode: EditorMode,
        block: Block,
        content: Content.Text,
        focus: Id,
        indent: Int
    ): BlockView.Paragraph = BlockView.Paragraph(
        mode = if (mode == EditorMode.EDITING) BlockView.Mode.EDIT else BlockView.Mode.READ,
        id = block.id,
        text = content.text,
        marks = content.marks(),
        focused = block.id == focus,
        color = content.color,
        backgroundColor = content.backgroundColor,
        indent = indent,
        alignment = content.align?.toView()
    )

    private fun headerThree(
        mode: EditorMode,
        block: Block,
        content: Content.Text,
        indent: Int
    ): BlockView.HeaderThree = BlockView.HeaderThree(
        mode = if (mode == EditorMode.EDITING) BlockView.Mode.EDIT else BlockView.Mode.READ,
        id = block.id,
        text = content.text,
        color = content.color,
        backgroundColor = content.backgroundColor,
        indent = indent,
        alignment = content.align?.toView()
    )

    private fun headerTwo(
        mode: EditorMode,
        block: Block,
        content: Content.Text,
        indent: Int
    ): BlockView.HeaderTwo = BlockView.HeaderTwo(
        mode = if (mode == EditorMode.EDITING) BlockView.Mode.EDIT else BlockView.Mode.READ,
        id = block.id,
        text = content.text,
        color = content.color,
        backgroundColor = content.backgroundColor,
        indent = indent,
        alignment = content.align?.toView()
    )

    private fun headerOne(
        mode: EditorMode,
        block: Block,
        content: Content.Text,
        indent: Int
    ): BlockView.HeaderOne = BlockView.HeaderOne(
        mode = if (mode == EditorMode.EDITING) BlockView.Mode.EDIT else BlockView.Mode.READ,
        id = block.id,
        text = content.text,
        color = content.color,
        backgroundColor = content.backgroundColor,
        indent = indent,
        alignment = content.align?.toView()
    )

    private fun checkbox(
        mode: EditorMode,
        block: Block,
        content: Content.Text,
        focus: Id,
        indent: Int
    ): BlockView.Checkbox = BlockView.Checkbox(
        mode = if (mode == EditorMode.EDITING) BlockView.Mode.EDIT else BlockView.Mode.READ,
        id = block.id,
        text = content.text,
        marks = content.marks(),
        isChecked = content.isChecked == true,
        color = content.color,
        backgroundColor = content.backgroundColor,
        focused = block.id == focus,
        indent = indent
    )

    private fun bulleted(
        mode: EditorMode,
        block: Block,
        content: Content.Text,
        focus: Id,
        indent: Int
    ): BlockView.Bulleted = BlockView.Bulleted(
        mode = if (mode == EditorMode.EDITING) BlockView.Mode.EDIT else BlockView.Mode.READ,
        id = block.id,
        text = content.text,
        indent = indent,
        marks = content.marks(),
        focused = block.id == focus,
        color = content.color,
        backgroundColor = content.backgroundColor
    )

    private fun code(
        mode: EditorMode,
        block: Block,
        content: Content.Text,
        focus: Id
    ): BlockView.Code = BlockView.Code(
        mode = if (mode == EditorMode.EDITING) BlockView.Mode.EDIT else BlockView.Mode.READ,
        id = block.id,
        text = content.text,
        focused = block.id == focus
    )

    private fun highlight(
        mode: EditorMode,
        block: Block,
        content: Content.Text,
        indent: Int
    ): BlockView.Highlight = BlockView.Highlight(
        mode = if (mode == EditorMode.EDITING) BlockView.Mode.EDIT else BlockView.Mode.READ,
        id = block.id,
        text = content.text,
        indent = indent
    )

    private fun toggle(
        mode: EditorMode,
        block: Block,
        content: Content.Text,
        indent: Int,
        focus: Id,
        isEmpty: Boolean
    ): BlockView.Toggle = BlockView.Toggle(
        mode = if (mode == EditorMode.EDITING) BlockView.Mode.EDIT else BlockView.Mode.READ,
        id = block.id,
        text = content.text,
        marks = content.marks(),
        color = content.color,
        backgroundColor = content.backgroundColor,
        indent = indent,
        focused = block.id == focus,
        toggled = toggleStateHolder.isToggled(block.id),
        isEmpty = isEmpty
    )

    private fun numbered(
        mode: EditorMode,
        block: Block,
        content: Content.Text,
        number: Int,
        focus: Id,
        indent: Int
    ): BlockView.Numbered = BlockView.Numbered(
        mode = if (mode == EditorMode.EDITING) BlockView.Mode.EDIT else BlockView.Mode.READ,
        id = block.id,
        text = content.text,
        number = number,
        focused = block.id == focus,
        color = content.color,
        backgroundColor = content.backgroundColor,
        indent = indent,
        marks = content.marks()
    )

    private fun bookmark(
        mode: EditorMode,
        content: Content.Bookmark,
        block: Block,
        indent: Int
    ): BlockView = content.url?.let { url ->
        if (content.title != null && content.description != null) {
            BlockView.Bookmark.View(
                id = block.id,
                url = url,
                title = content.title,
                description = content.description,
                imageUrl = content.image?.let { urlBuilder.image(it) },
                faviconUrl = content.favicon?.let { urlBuilder.image(it) },
                indent = indent,
                mode = if (mode == EditorMode.EDITING) BlockView.Mode.EDIT else BlockView.Mode.READ
            )
        } else {
            BlockView.Bookmark.Error(
                id = block.id,
                url = url,
                indent = indent,
                mode = if (mode == EditorMode.EDITING) BlockView.Mode.EDIT else BlockView.Mode.READ
            )
        }
    } ?: BlockView.Bookmark.Placeholder(
        id = block.id,
        indent = indent,
        mode = if (mode == EditorMode.EDITING) BlockView.Mode.EDIT else BlockView.Mode.READ
    )

    private fun divider(block: Block) = BlockView.Divider(id = block.id)

    private fun file(
        mode: EditorMode,
        content: Content.File,
        block: Block,
        indent: Int
    ): BlockView = when (content.type) {
        Content.File.Type.IMAGE -> content.toPictureView(
            id = block.id,
            urlBuilder = urlBuilder,
            indent = indent,
            mode = if (mode == EditorMode.EDITING) BlockView.Mode.EDIT else BlockView.Mode.READ
        )
        Content.File.Type.FILE -> content.toFileView(
            id = block.id,
            urlBuilder = urlBuilder,
            indent = indent,
            mode = if (mode == EditorMode.EDITING) BlockView.Mode.EDIT else BlockView.Mode.READ
        )
        Content.File.Type.VIDEO -> content.toVideoView(
            id = block.id,
            urlBuilder = urlBuilder,
            indent = indent,
            mode = if (mode == EditorMode.EDITING) BlockView.Mode.EDIT else BlockView.Mode.READ
        )
        else -> throw IllegalStateException("Unexpected file type: ${content.type}")
    }

    private fun title(
        mode: EditorMode,
        block: Block,
        content: Content.Text,
        root: Block,
        focus: Id
    ): BlockView.Title = BlockView.Title(
        mode = if (mode == EditorMode.EDITING) BlockView.Mode.EDIT else BlockView.Mode.READ,
        id = block.id,
        text = content.text,
        emoji = root.fields.icon?.let { name ->
            if (name.isNotEmpty())
                name
            else
                null
        },
        focused = block.id == focus
    )

    private fun page(
        block: Block,
        content: Content.Link,
        indent: Int,
        details: Block.Details
    ): BlockView.Page = BlockView.Page(
        id = block.id,
        isEmpty = true,
        emoji = details.details[content.target]?.icon?.let { name ->
            if (name.isNotEmpty())
                name
            else
                null
        },
        text = details.details[content.target]?.name,
        indent = indent
    )
}