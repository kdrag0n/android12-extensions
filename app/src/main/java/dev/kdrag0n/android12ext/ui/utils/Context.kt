package dev.kdrag0n.android12ext.ui.utils

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment

fun Fragment.openUri(uri: String) {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
}
