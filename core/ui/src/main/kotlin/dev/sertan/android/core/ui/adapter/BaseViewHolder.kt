package dev.sertan.android.core.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseViewHolder<I : BaseListItem<I>>(
    private val binding: ViewBinding
) : RecyclerView.ViewHolder(binding.root) {

    open fun bind(item: I): Unit = Unit
}
