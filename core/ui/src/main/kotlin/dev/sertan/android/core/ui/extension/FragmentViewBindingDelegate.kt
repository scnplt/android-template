package dev.sertan.android.core.ui.extension

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal class FragmentViewBindingDelegate<VB : ViewBinding>(
    private val viewBindingFactory: (View) -> VB
) : ReadOnlyProperty<Fragment, VB> {

    private var binding: VB? = null

    private val lifecycleOwner = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            owner.lifecycle.removeObserver(this)
            binding = null
        }
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): VB {
        return binding ?: viewBindingFactory(thisRef.requireView()).also {
            binding = it
            thisRef.viewLifecycleOwner.lifecycle.addObserver(lifecycleOwner)
        }
    }
}
