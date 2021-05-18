package com.anytypeio.anytype.core_ui.features.page.slash.holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.anytypeio.anytype.core_utils.ext.gone
import com.anytypeio.anytype.core_utils.ext.visible
import com.anytypeio.anytype.presentation.page.editor.slash.SlashItem
import kotlinx.android.synthetic.main.item_slash_widget_object_type.view.*

class ObjectTypeMenuHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(item: SlashItem.ObjectType) = with(itemView) {
        ivIcon.setIcon(
            emoji = item.emoji,
            image = null,
            name = item.name
        )
        tvTitle.text = item.name
        if (item.description.isNullOrBlank()) {
            tvSubtitle.gone()
        } else {
            tvSubtitle.visible()
            tvSubtitle.text = item.description
        }
    }
}