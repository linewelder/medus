package io.github.linewelder.medus.core.database

import java.util.*

/**
 * Strongly typed ID class for models (entities)
 * @param T Type of the model (entity) the ID refers to
 */
@JvmInline
value class Id<T>(val value: UUID) {
    fun <U> unsafeCast() = Id<U>(this.value)
    override fun toString() = value.toString()

    companion object {
        fun <T> random() = Id<T>(UUID.randomUUID())
        fun <T> fromString(string: String) = Id<T>(UUID.fromString(string))
        fun <T> empty() = Id<T>(UUID(0L, 0L))
    }
}
