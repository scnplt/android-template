package dev.sertan.android.core.ui.adapter

import androidx.recyclerview.widget.DiffUtil

internal class BaseListItemDiffUtil<I : BaseListItem<I>> : DiffUtil.ItemCallback<I>() {

    override fun areItemsTheSame(oldItem: I, newItem: I): Boolean =
        oldItem.areItemsTheSame(newItem)

    override fun areContentsTheSame(oldItem: I, newItem: I): Boolean =
        oldItem.areContentsTheSame(newItem)
}
