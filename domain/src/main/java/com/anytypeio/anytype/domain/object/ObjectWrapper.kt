package com.anytypeio.anytype.domain.`object`

import com.anytypeio.anytype.core_models.Id
import com.anytypeio.anytype.core_models.ObjectType
import com.anytypeio.anytype.core_models.ext.typeOf
import com.anytypeio.anytype.domain.cover.CoverType
import com.anytypeio.anytype.domain.relations.Relations

/**
 * Wrapper for easily parsing object's relations when object is represented as an untyped structure.
 */
sealed class ObjectWrapper {

    /**
     * @property map [map] map with raw data containing relations.
     */
    class Basic(val map: Map<String, Any?>) {

        private val default = map.withDefault { null }

        val name: String? by default

        val iconEmoji: String? by default
        val iconImage: String? by default

        val coverId: String? by default

        val coverType: CoverType?
            get() = when (val value = map[Relations.COVER_TYPE]) {
                is Double -> CoverType.values().find { type ->
                    type.code == value.toInt()
                }
                else -> null
            }

        val isArchived: Boolean? by default

        val type: List<String>
            get() = when (val value = map[Relations.TYPE]) {
                is String -> listOf(value)
                is List<*> -> value.typeOf()
                else -> emptyList()
            }

        val layout: ObjectType.Layout?
            get() = when (val value = map[Relations.LAYOUT]) {
                is Double -> ObjectType.Layout.values().find { layout ->
                    layout.code == value.toInt()
                }
                else -> null
            }

        val id: Id by default
    }
}