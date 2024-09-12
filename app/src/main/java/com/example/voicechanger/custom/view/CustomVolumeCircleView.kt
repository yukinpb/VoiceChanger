package com.example.voicechanger.custom.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.example.voicechanger.R
import com.example.voicechanger.util.Constants
import kotlin.math.min

class CustomVolumeCircleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var maxRadius: Float = 0f
    private var currentVolumeLevel: Float = 0f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val numberOfLayers = Constants.NUMBER_OF_LAYER_VOICE_CIRCLE

    fun updateVolumeLevel(volume: Float) {
        currentVolumeLevel = volume.coerceIn(0f, 1f)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        Log.d(TAG, "onDraw: $currentVolumeLevel")

        val cx = width / 2f
        val cy = height / 2f
        val layerStep = maxRadius / numberOfLayers

        val primaryColor = ContextCompat.getColor(context, R.color.colorPrimary)

        val maxLayer = (currentVolumeLevel * numberOfLayers).toInt().coerceIn(1, numberOfLayers)

        for (i in 1..maxLayer) {
            val layerRadius = layerStep * i

            val alpha = (currentVolumeLevel * 255).toInt().coerceIn(0, 255)

            paint.color = Color.argb(
                alpha,
                Color.red(primaryColor),
                Color.green(primaryColor),
                Color.blue(primaryColor)
            )

            canvas.drawCircle(cx, cy, layerRadius, paint)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        maxRadius = min(w, h) / 2f
    }

    companion object {
        private const val TAG = "CustomVolumeCircleView"
    }
}