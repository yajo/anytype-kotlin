package com.anytypeio.anytype.presentation.editor

import com.anytypeio.anytype.core_models.Block
import com.anytypeio.anytype.core_models.Event
import com.anytypeio.anytype.core_models.ext.content
import com.anytypeio.anytype.core_utils.ext.replace
import com.anytypeio.anytype.presentation.common.StateReducer
import timber.log.Timber

/**
 * Reduces external events (coming not from user, but from outside) to state.
 */
class DocumentExternalEventReducer : StateReducer<List<Block>, Event> {

    override val function: suspend (List<Block>, Event) -> List<Block>
        get() = { state, event ->
            reduce(
                state,
                event
            )
        }

    override suspend fun reduce(state: List<Block>, event: Event): List<Block> = when (event) {
        is Event.Command.ShowObject -> event.blocks
        is Event.Command.AddBlock -> state + event.blocks
        is Event.Command.UpdateStructure -> state.replace(
            replacement = { target ->
                target.copy(children = event.children)
            },
            target = { block -> block.id == event.id }
        )
        is Event.Command.DeleteBlock -> state.filter { !event.targets.contains(it.id) }
        is Event.Command.GranularChange -> state.replace(
            replacement = { block ->
                when (val content = block.content) {
                    is Block.Content.RelationBlock -> {
                        block.copy(
                            content = content.copy(
                                background = event.backgroundColor ?: content.background
                            )
                        )
                    }
                    is Block.Content.Text -> {
                        block.copy(
                            content = content.copy(
                                style = event.style ?: content.style,
                                color = event.color ?: content.color,
                                backgroundColor = event.backgroundColor ?: content.backgroundColor,
                                text = event.text ?: content.text,
                                marks = event.marks ?: content.marks,
                                isChecked = event.checked ?: content.isChecked,
                                align = event.alignment ?: content.align
                            )
                        )
                    }
                    else -> block.copy()
                }
            },
            target = { block -> block.id == event.id }
        )
        is Event.Command.UpdateFields -> state.replace(
            replacement = { block -> block.copy(fields = event.fields) },
            target = { block -> block.id == event.target }
        )

        is Event.Command.UpdateFileBlock -> state.replace(
            replacement = { block ->
                val content = block.content<Block.Content.File>()
                block.copy(
                    content = content.copy(
                        hash = event.hash ?: content.hash,
                        name = event.name ?: content.name,
                        mime = event.mime ?: content.mime,
                        size = event.size ?: content.size,
                        type = event.type ?: content.type,
                        state = event.state ?: content.state
                    )
                )
            },
            target = { block -> block.id == event.id }
        )
        is Event.Command.BookmarkGranularChange -> state.replace(
            replacement = { block ->
                val content = block.content<Block.Content.Bookmark>()
                block.copy(
                    content = content.copy(
                        url = event.url ?: content.url,
                        title = event.title ?: content.title,
                        description = event.description ?: content.description,
                        image = event.image ?: content.image,
                        favicon = event.favicon ?: content.favicon
                    )
                )
            },
            target = { block -> block.id == event.target }
        )
        is Event.Command.LinkGranularChange -> state.replace(
            replacement = { block ->
                val content = block.content<Block.Content.Link>()
                block.copy(
                    content = content.copy(
                        fields = event.fields ?: content.fields
                    )
                )
            },
            target = { block -> block.id == event.id }
        )
        is Event.Command.UpdateDividerBlock -> state.replace(
            replacement = { block ->
                val content = block.content<Block.Content.Divider>()
                block.copy(
                    content = content.copy(
                        style = event.style
                    )
                )
            },
            target = { block -> block.id == event.id }
        )
        is Event.Command.BlockEvent.SetRelation -> state.replace(
            replacement = { block ->
                val content = block.content<Block.Content.RelationBlock>()
                block.copy(
                    content = content.copy(
                        key = event.key
                    )
                )
            },
            target = { block -> block.id == event.id }
        )

        else -> state.also { Timber.d("Ignoring event: $event") }
    }
}