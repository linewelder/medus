package io.github.linewelder.medus.core.model

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

        /**
         * Create a dummy ID for UI previews. [index] allows to create a unique ID for each entity.
         */
        fun <T> dummy(index: Int) = Id<T>(UUID(0L, index.toLong()))
    }
}
