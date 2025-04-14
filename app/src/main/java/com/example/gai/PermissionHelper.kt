package com.example.gai

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * Helper object to manage permission-related utilities.
 */
object PermissionHelper {
    /**
     * Opens the app's settings screen so that the user can manually allow permissions.
     */
    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:" + context.packageName)
        context.startActivity(intent)
    }
}
