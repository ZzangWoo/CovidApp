package com.doong.ronaapp.Dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.doong.ronaapp.R

class LoadingActivity(context: Context) : Dialog(context) {
    private var c: Context? = null

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCanceledOnTouchOutside(false)
        c = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.loading_activity)

        val logo = findViewById<ImageView>(R.id.loadingIcon)
        val animation: Animation = AnimationUtils.loadAnimation(c, R.anim.loading)
        logo.animation = animation
    }

    override fun show() {
        super.show()
    }

    override fun dismiss() {
        super.dismiss()
    }
}