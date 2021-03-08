package com.anomalydev.worldnewsforyou.util

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

/**
 *  This extension function creates a snackbar while extending Fragment
 *  to get reference to view hierarchy of the layout.
 *
 */
fun Fragment.showSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    view: View = requireView()
) {
    Snackbar.make(view, message, duration).show()
}

// Used to treat when statement as an expression for error cases
val <T> T.exhaustive: T
    get() = this