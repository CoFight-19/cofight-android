package net.miksoft.covidcofight.presentation.common

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import net.miksoft.covidcofight.R

fun Context.showToast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.showGenericErrorToast() =
    Toast.makeText(this, this.getString(R.string.generic_error), Toast.LENGTH_SHORT).show()

fun Context.showInfoDialog(messageResId: Int) {
    AlertDialog.Builder(this)
        .setMessage(messageResId)
        .setPositiveButton(
            android.R.string.ok,
            null
        )
        .show()
}

fun Context.showConfirmationDialog(titleResId: Int, messageResId: Int, action: () -> Unit) {
    AlertDialog.Builder(this)
        .setTitle(titleResId)
        .setMessage(messageResId)
        .setPositiveButton(
            android.R.string.yes
        ) { _, _ ->
            action.invoke()
        }
        .setNegativeButton(android.R.string.no, null)
        .show()
}