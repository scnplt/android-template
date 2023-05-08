package dev.sertan.android.core.ui.extension

import android.view.View
import androidx.core.view.isVisible

fun View.show() {
    if (visibility != View.VISIBLE) isVisible = true
}

fun View.hide() {
    if (visibility == View.VISIBLE) isVisible = false
}
