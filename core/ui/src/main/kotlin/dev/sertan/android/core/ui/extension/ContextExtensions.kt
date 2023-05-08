package dev.sertan.android.core.ui.extension

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.showToast(message: String): Unit =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.showToast(@StringRes messageResId: Int): Unit =
    Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
