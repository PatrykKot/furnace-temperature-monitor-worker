package com.kotlarz.helper.ui

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import com.github.johnpersano.supertoasts.library.Style
import com.github.johnpersano.supertoasts.library.SuperActivityToast
import com.kotlarz.R

class ToastHelper {
    companion object {
        fun createConnectionCheckingToast(context: Context): SuperActivityToast {
            val infoToast = SuperActivityToast.create(context, Style.grey(), Style.TYPE_PROGRESS_CIRCLE)
            infoToast.progressIndeterminate = true
            infoToast.isIndeterminate = true
            infoToast.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
            infoToast.frame = Style.FRAME_KITKAT
            infoToast.animations = Style.ANIMATIONS_FADE
            infoToast.text = context.resources.getString(R.string.checkingConnection)
            infoToast.typefaceStyle = Typeface.BOLD
            return infoToast
        }

        fun createInfoToast(context: Context, style: Style, textId: Int): SuperActivityToast {
            val infoToast = SuperActivityToast.create(context, style, Style.TYPE_STANDARD)
            infoToast.text = context.resources.getString(textId)
            infoToast.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
            infoToast.frame = Style.FRAME_KITKAT
            infoToast.animations = Style.ANIMATIONS_FADE
            infoToast.typefaceStyle = Typeface.BOLD
            return infoToast
        }
    }
}