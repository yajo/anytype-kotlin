package com.anytypeio.anytype.core_models.ext

import com.anytypeio.anytype.core_models.DVRecord
import com.anytypeio.anytype.core_models.Event
import com.anytypeio.anytype.core_models.Id
import com.anytypeio.anytype.core_models.Relation

fun List<Relation>.process(
    event: Event.Command.ObjectRelations
) : List<Relation> = when(event) {
    is Event.Command.ObjectRelations.Amend -> amend(event.relations)
    is Event.Command.ObjectRelations.Remove -> remove(event.keys)
    is Event.Command.ObjectRelations.Set -> set(event.relations)
}

fun List<Relation>.set(relations: List<Relation>) : List<Relation> {
    return relations
}

fun List<Relation>.amend(relations: List<Relation>) : List<Relation> {
    val map = this.associateBy { it.key }.toMutableMap()
    relations.forEach { relation ->
        map[relation.key] = relation
    }
    return map.values.toList()
}

fun List<Relation>.remove(keys: List<Id>) : List<Relation> {
    return filter { !keys.contains(it.key) }
}

fun Any?.addIds(ids: List<Id>): List<Id> {
    val remaining = mutableListOf<Id>()
    if (this is List<*>) {
        remaining.addAll(typeOf())
        remaining.addAll(ids)
    }
    return remaining.toList()
}

inline fun <reified T> List<*>.typeOf(): List<T> {
    val retlist = mutableListOf<T>()
    this.forEach {
        if (it is T) {
            retlist.add(it)
        }
    }
    return retlist
}

fun DVRecord.addIds(key:Id, ids: List<Id>): DVRecord {
    return this.mapValues { entry->
        if (entry.key == key) {
            val value: Any? = entry.value
            val remaining = mutableListOf<Id>()
            if (value is List<*>) {
                remaining.addAll(value.typeOf())
                remaining.addAll(ids)
            }
            remaining
        } else {
            entry
        }
    }
}