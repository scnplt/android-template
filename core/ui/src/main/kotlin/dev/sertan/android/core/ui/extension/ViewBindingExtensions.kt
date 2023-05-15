package dev.sertan.android.core.ui.extension

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty

fun <VB : ViewBinding> AppCompatActivity.viewBinding(inflate: (LayoutInflater) -> VB) =
    lazy { inflate(layoutInflater) }

fun <VB : ViewBinding> Fragment.viewBinding(
    viewBindingFactory: (View) -> VB
): ReadOnlyProperty<Fragment, VB> = FragmentViewBindingDelegate(viewBindingFactory)

fun <VB : ViewBinding> ViewGroup.viewBinding(inflater: (LayoutInflater, ViewGroup) -> VB): VB =
    inflater(LayoutInflater.from(context), this)

fun <VB : ViewBinding> viewBinding(
    parent: ViewGroup,
    inflater: (LayoutInflater, ViewGroup, Boolean) -> VB
): VB = inflater(LayoutInflater.from(parent.context), parent, false)
