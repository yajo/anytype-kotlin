package com.anytypeio.anytype.domain.objects

import com.anytypeio.anytype.core_models.Id
import com.anytypeio.anytype.core_models.Key
import com.anytypeio.anytype.core_models.ObjectWrapper
import com.anytypeio.anytype.core_models.Struct
import com.anytypeio.anytype.domain.`object`.amend
import com.anytypeio.anytype.domain.`object`.unset
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface StoreOfRelations {
    val size: Int
    suspend fun getByKey(key: Key): ObjectWrapper.Relation?
    suspend fun getById(id: Id): ObjectWrapper.Relation?
    suspend fun getAll(): List<ObjectWrapper.Relation>
    suspend fun merge(relations: List<ObjectWrapper.Relation>)
    suspend fun amend(target: Id, diff: Map<Id, Any?>)
    suspend fun unset(target: Id, keys: List<Key>)
    suspend fun set(target: Id, data: Struct)
    suspend fun remove(target: Id)
    suspend fun clear()
}

class DefaultStoreOfRelations : StoreOfRelations {

    private val mutex = Mutex()
    private val store = mutableMapOf<Id, ObjectWrapper.Relation>()
    private val keysToIds = mutableMapOf<Key, Id>()

    override val size: Int get() = store.size

    override suspend fun getByKey(key: Key): ObjectWrapper.Relation? = mutex.withLock {
        val id = keysToIds[key]
        if (id != null)
            store[id]
        else
            null
    }

    override suspend fun getById(id: Id): ObjectWrapper.Relation? = mutex.withLock {
        store[id]
    }

    override suspend fun getAll(): List<ObjectWrapper.Relation> = mutex.withLock {
        store.values.toList()
    }

    override suspend fun merge(relations: List<ObjectWrapper.Relation>): Unit = mutex.withLock {
        relations.forEach { o ->
            val current = store[o.id]
            if (current == null) {
                store[o.id] = o.also { keysToIds[it.key] = o.id }
            } else {
                store[o.id] = current.amend(o.map)
            }
        }
    }

    override suspend fun amend(target: Id, diff: Map<Id, Any?>): Unit = mutex.withLock {
        val current = store[target]
        if (current != null) {
            store[target] = current.amend(diff)
        } else {
            store[target] = ObjectWrapper.Relation(diff).also { keysToIds[it.key] = target }
        }
    }

    override suspend fun set(
        target: Id,
        data: Map<String, Any?>
    ): Unit = mutex.withLock {
        store[target] = ObjectWrapper.Relation(data).also { keysToIds[it.key] = target }
    }

    override suspend fun unset(
        target: Id,
        keys: List<Id>
    ): Unit = mutex.withLock {
        val current = store[target]
        if (current != null) {
            store[target] = current.unset(keys)
        }
    }

    override suspend fun remove(target: Id) : Unit = mutex.withLock {
        val current = store[target]
        if (current != null) {
            keysToIds.remove(current.key)
            store.remove(target)
        }
    }

    override suspend fun clear(): Unit = mutex.withLock {
        keysToIds.clear()
        store.clear()
    }
}