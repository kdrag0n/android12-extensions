package dev.kdrag0n.android12ext.ui.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

fun Context.openUri(uri: String) {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
}
