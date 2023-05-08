package dev.sertan.android.core.ui.adapter

interface BaseListItem<T : Any> {

    val viewType: Int

    fun areItemsTheSame(newItem: T): Boolean

    fun areContentsTheSame(newItem: T): Boolean
}
