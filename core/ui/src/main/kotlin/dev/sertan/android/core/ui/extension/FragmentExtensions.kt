package dev.sertan.android.core.ui.extension

import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun Fragment.repeatOnLifecycleStarted(block: suspend CoroutineScope.() -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch { repeatOnLifecycle(Lifecycle.State.STARTED, block) }
}

fun Fragment.navTo(navDirections: NavDirections): Unit = findNavController().navigate(navDirections)

fun Fragment.navTo(@IdRes destinationResId: Int, args: Bundle? = null): Unit =
    findNavController().navigate(destinationResId, args)

fun Fragment.popBackStack(): Boolean = findNavController().popBackStack()

fun <T> Fragment.popBackStack(key: String, data: T): Boolean = with(findNavController()) {
    previousBackStackEntry?.savedStateHandle?.let {
        if (!it.contains(key)) it[key] = data
        popBackStack()
    } ?: false
}

fun <T> Fragment.savedStateHandleListener(key: String, callback: (T) -> Unit) {
    val savedStateHandle = findNavController().currentBackStackEntry?.savedStateHandle ?: return
    savedStateHandle.getLiveData<T>(key).observe(viewLifecycleOwner) { data ->
        callback(data)
        savedStateHandle.remove<T>(key)
    }
}

fun Fragment.doIfPermissionGranted(
    resultLauncher: ActivityResultLauncher<String>,
    permission: String,
    block: () -> Unit
) {
    val permissionResult = ContextCompat.checkSelfPermission(requireContext(), permission)
    if (permissionResult == PackageManager.PERMISSION_GRANTED) return block()
    resultLauncher.launch(permission)
}

fun Fragment.resultLauncher(
    onGranted: () -> Unit = {},
    onDenied: () -> Unit = {}
): ActivityResultLauncher<String> = registerForActivityResult(RequestPermission()) {
    if (it) onGranted() else onDenied()
}
