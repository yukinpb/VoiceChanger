package com.example.voicechanger.custom.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class CustomVolumeCircleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var maxRadius: Float = 0f
    private var currentVolumeLevel: Float = 0f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val numberOfLayers = 5

    fun updateVolumeLevel(volume: Float) {
        currentVolumeLevel = volume
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f
        val layerStep = maxRadius / numberOfLayers

        for (i in 1..numberOfLayers) {
            val layerRadius = layerStep * i

            val alpha = ((i / numberOfLayers.toFloat()) * currentVolumeLevel * 255).toInt()

            val clampedAlpha = alpha.coerceIn(0, 255)

            paint.color = Color.argb(clampedAlpha, 0, 255, 0)

            canvas.drawCircle(cx, cy, layerRadius, paint)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        maxRadius = min(w, h) / 2f
    }
}