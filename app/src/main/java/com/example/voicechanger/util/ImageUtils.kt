package com.example.voicechanger.util

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.io.File

fun ImageView.loadImage(resDrawable: Int, isCircle: Boolean) {
    if (isCircle) {
        Glide.with(this)
            .load(resDrawable)
            .circleCrop()
            .into(this)
    } else {
        Glide.with(this)
            .load(resDrawable)
            .into(this)
    }
}

fun ImageView.loadImage(urlImage: String?, placeHolder: Int, isCircle: Boolean = false) {
    if (isCircle) {
        Glide.with(this)
            .load(urlImage)
            .circleCrop()
            .placeholder(placeHolder)
            .into(this)
    } else {
        Glide.with(this)
            .load(urlImage)
            .placeholder(placeHolder)
            .into(this)
    }

}

fun ImageView.loadImage(urlImage: String?, isCircle: Boolean = false) {
    if (isCircle) {
        Glide.with(this)
            .load(urlImage)
            .circleCrop()
            .into(this)
    } else {
        Glide.with(this)
            .load(urlImage)
            .into(this)
    }
}

fun ImageView.loadImage(urlImage: File, placeHolder: Int, isCircle: Boolean = false) {
    if (isCircle) {
        Glide.with(this)
            .load(urlImage)
            .circleCrop()
            .placeholder(placeHolder)
            .into(this)
    } else {
        Glide.with(this)
            .load(urlImage)
            .placeholder(placeHolder)
            .into(this)
    }

}

fun ImageView.loadImage(urlImage: Uri?, placeHolder: Int, isCircle: Boolean = false) {
    if (isCircle) {
        Glide.with(this)
            .load(urlImage)
            .circleCrop()
            .placeholder(placeHolder)
            .into(this)
    } else {
        Glide.with(this)
            .load(urlImage)
            .placeholder(placeHolder)
            .into(this)
    }
}