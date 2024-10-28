/*
 * Copyright 2023 Sertan Canpolat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    viewBindingFactory: (View) -> VB,
): ReadOnlyProperty<Fragment, VB> = FragmentViewBindingDelegate(viewBindingFactory)

fun <VB : ViewBinding> ViewGroup.viewBinding(inflater: (LayoutInflater, ViewGroup) -> VB): VB =
    inflater(LayoutInflater.from(context), this)

fun <VB : ViewBinding> viewBinding(
    parent: ViewGroup,
    inflater: (LayoutInflater, ViewGroup, Boolean) -> VB,
): VB = inflater(LayoutInflater.from(parent.context), parent, false)
