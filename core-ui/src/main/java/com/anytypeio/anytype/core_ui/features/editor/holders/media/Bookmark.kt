package com.anytypeio.anytype.core_ui.features.editor.holders.media

import android.graphics.drawable.Drawable
import android.text.Spannable
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.anytypeio.anytype.core_ui.R
import com.anytypeio.anytype.core_ui.common.SearchHighlightSpan
import com.anytypeio.anytype.core_ui.common.SearchTargetHighlightSpan
import com.anytypeio.anytype.core_ui.databinding.ItemBlockBookmarkBinding
import com.anytypeio.anytype.core_ui.features.editor.BlockViewDiffUtil
import com.anytypeio.anytype.core_ui.features.editor.EditorTouchProcessor
import com.anytypeio.anytype.core_utils.ext.dimen
import com.anytypeio.anytype.core_utils.ext.gone
import com.anytypeio.anytype.core_utils.ext.removeSpans
import com.anytypeio.anytype.core_utils.ext.visible
import com.anytypeio.anytype.presentation.editor.editor.listener.ListenerType
import com.anytypeio.anytype.presentation.editor.editor.model.BlockView
import com.anytypeio.anytype.presentation.editor.editor.model.BlockView.Media.Bookmark.Companion.SEARCH_FIELD_DESCRIPTION_KEY
import com.anytypeio.anytype.presentation.editor.editor.model.BlockView.Media.Bookmark.Companion.SEARCH_FIELD_TITLE_KEY
import com.anytypeio.anytype.presentation.editor.editor.model.BlockView.Media.Bookmark.Companion.SEARCH_FIELD_URL_KEY
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import timber.log.Timber

class Bookmark(val binding: ItemBlockBookmarkBinding) : Media(binding.root) {

    override val root: View = itemView
    override val container = binding.containerWithBackground
    private val title = binding.bookmarkTitle
    private val selected = binding.selected
    private val description = binding.bookmarkDescription
    private val url = binding.bookmarkUrl
    private val image = binding.bookmarkImage
    private val logo = binding.bookmarkLogo
    private val error = binding.loadBookmarkPictureError
    private val card = binding.bookmarkRoot
    override val clickContainer: View = card

    override val editorTouchProcessor: EditorTouchProcessor = EditorTouchProcessor(
        fallback = { e -> clickContainer.onTouchEvent(e) }
    )

    init {
        clickContainer.setOnTouchListener { v, e -> editorTouchProcessor.process(v, e) }
    }

    private val listener: RequestListener<Drawable> = object : RequestListener<Drawable> {

        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            error.visible()
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            error.gone()
            return false
        }
    }

    fun bind(item: BlockView.Media.Bookmark, clicked: (ListenerType) -> Unit) {
        super.bind(item, clicked)
        title.setText(item.title, TextView.BufferType.EDITABLE)
        description.setText(item.description, TextView.BufferType.EDITABLE)
        url.setText(item.url, TextView.BufferType.EDITABLE)
        if (item.imageUrl != null) {
            image.visible()
            Glide.with(image)
                .load(item.imageUrl)
                .centerCrop()
                .listener(listener)
                .into(image)
        } else {
            image.gone()
        }
        if (item.faviconUrl != null) {
            logo.visible()
            Glide.with(logo).load(item.faviconUrl).into(logo)
        } else {
            logo.gone()
        }
        applyBackground(item.backgroundColor)
        applyRootMargins(item)
    }

    private fun applySearchHighlight(item: BlockView.Media.Bookmark) {
        if (item.searchFields.isEmpty()) {
            clearSearchHighlights(title)
            clearSearchHighlights(description)
            clearSearchHighlights(url)
        } else {
            item.searchFields.forEach { field ->
                val input = when (field.key) {
                    SEARCH_FIELD_TITLE_KEY -> title
                    SEARCH_FIELD_DESCRIPTION_KEY -> description
                    SEARCH_FIELD_URL_KEY -> url
                    else -> null
                }
                if (input != null)
                    applySearchHighlight(field, input)
                else
                    Timber.e("Unexpected search field key: $field")

            }
        }
    }

    private fun applySearchHighlight(field: BlockView.Searchable.Field, input: TextView) {
        input.editableText.removeSpans<SearchHighlightSpan>()
        input.editableText.removeSpans<SearchTargetHighlightSpan>()
        field.highlights.forEach { highlight ->
            input.editableText.setSpan(
                SearchHighlightSpan(),
                highlight.first,
                highlight.last,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        if (field.isTargeted) {
            input.editableText.setSpan(
                SearchTargetHighlightSpan(),
                field.target.first,
                field.target.last,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun clearSearchHighlights(input: TextView) {
        input.editableText.removeSpans<SearchHighlightSpan>()
        input.editableText.removeSpans<SearchTargetHighlightSpan>()
    }

    override fun onMediaBlockClicked(item: BlockView.Media, clicked: (ListenerType) -> Unit) {
        clicked(ListenerType.Bookmark.View(item = item as BlockView.Media.Bookmark))
    }

    override fun processChangePayload(payloads: List<BlockViewDiffUtil.Payload>, item: BlockView) {
        super.processChangePayload(payloads, item)
        check(item is BlockView.Media.Bookmark)
        payloads.forEach { payload ->
            if (payload.isSearchHighlightChanged) {
                applySearchHighlight(item)
            }
            if (payload.isBackgroundColorChanged) {
                applyBackground(item.backgroundColor)
            }
        }
    }

    override fun indentize(item: BlockView.Indentable) {
        val leftPadding =
            dimen(R.dimen.default_document_item_padding_start) + item.indent * dimen(R.dimen.indent)
        root.updatePadding(left = leftPadding)
    }

    override fun select(isSelected: Boolean) {
        selected.isSelected = isSelected
    }

    private fun applyRootMargins(item: BlockView.Media.Bookmark) {
        if (item.isPreviousBlockMedia) {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                apply { topMargin = 0 }
            }
        } else {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                apply {
                    topMargin =
                        root.resources.getDimension(R.dimen.default_link_card_root_margin_top).toInt()
                }
            }
        }
    }
}