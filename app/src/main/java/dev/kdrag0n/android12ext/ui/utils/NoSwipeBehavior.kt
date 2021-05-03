package dev.kdrag0n.android12ext.ui.utils

import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar

class NoSwipeBehavior : BaseTransientBottomBar.Behavior() {
    override fun canSwipeDismissView(child: View) = false
}