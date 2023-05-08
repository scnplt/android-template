package dev.sertan.android.core.ui.adapter

import androidx.recyclerview.widget.ListAdapter

abstract class BaseListAdapter<I : BaseListItem<I>> :
    ListAdapter<I, BaseViewHolder<I>>(BaseListItemDiffUtil()) {

    override fun getItemViewType(position: Int): Int = getItem(position).viewType

    override fun onBindViewHolder(holder: BaseViewHolder<I>, position: Int): Unit =
        holder.bind(getItem(position))
}
