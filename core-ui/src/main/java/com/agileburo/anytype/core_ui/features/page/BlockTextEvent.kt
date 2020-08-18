package com.agileburo.anytype.core_ui.features.page

import android.text.Editable
import com.agileburo.anytype.core_ui.common.Markup

sealed class BlockTextEvent {

    sealed class TextEvent : BlockTextEvent() {
        data class Default(val target: String, val text: Editable) : BlockTextEvent()
        data class Pattern(val target: String, val text: Editable) : BlockTextEvent()
    }

    data class FocusEvent(val target: String, val focused: Boolean) : BlockTextEvent()

    data class SelectionEvent(val target: String, val range: IntRange) : BlockTextEvent()

    sealed class MentionEvent : BlockTextEvent() {
        data class Start(val cursorCoordinate: Int, val mentionStart: Int) : MentionEvent()
        data class Text(val text: CharSequence) : MentionEvent()
        object Stop : MentionEvent()
    }

    sealed class KeyboardEvent : BlockTextEvent() {
        data class EndLineEnter(val target: String, val text: Editable) : KeyboardEvent()
        data class SplitLineEnter(val target: String, val index: Int, val text: CharSequence) :
            KeyboardEvent()

        data class EmptyBlockBackspace(val target: String) : KeyboardEvent()
        data class NonEmptyBlockBackspace(val target: String, val editable: Editable) :
            KeyboardEvent()
    }

    sealed class Action : BlockTextEvent() {
        data class Copy(val selection: IntRange) : Action()
        data class Paste(val selection: IntRange) : Action()
    }

    data class MarkupEvent(val type: Markup.Type, val range: IntRange) : BlockTextEvent()
}