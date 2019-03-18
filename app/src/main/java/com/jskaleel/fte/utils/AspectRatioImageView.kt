package com.jskaleel.fte.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import com.jskaleel.fte.R

class AspectRatioImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ImageView(context, attrs) {

    private var aspectRatio: Float = 0.toFloat()
    private var aspectRatioEnabled: Boolean = false
    private var dominantMeasurement: Int = 0

    init {

        val a = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioImageView)
        aspectRatio = a.getFloat(R.styleable.AspectRatioImageView_aspectRatio, DEFAULT_ASPECT_RATIO)
        aspectRatioEnabled =
            a.getBoolean(R.styleable.AspectRatioImageView_aspectRatioEnabled, DEFAULT_ASPECT_RATIO_ENABLED)
        dominantMeasurement =
            a.getInt(R.styleable.AspectRatioImageView_dominantMeasurement, DEFAULT_DOMINANT_MEASUREMENT)
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (!aspectRatioEnabled) return

        val newWidth: Int
        val newHeight: Int
        when (dominantMeasurement) {
            MEASUREMENT_WIDTH -> {
                newWidth = measuredWidth
                //        newHeight = (int) (newWidth * aspectRatio);
                newHeight = (newWidth / aspectRatio).toInt()
            }

            MEASUREMENT_HEIGHT -> {
                newHeight = measuredHeight
                newWidth = (newHeight * aspectRatio).toInt()
            }

            else -> throw IllegalStateException("Unknown measurement with ID $dominantMeasurement")
        }//newHeight = getMeasuredHeight();

        setMeasuredDimension(newWidth, newHeight)
    }

    fun getAspectRatio(): Float {
        return aspectRatio
    }

    fun setAspectRatio(aspectRatio: Float) {
        this.aspectRatio = aspectRatio
        if (aspectRatioEnabled) {
            requestLayout()
        }
    }

    fun getAspectRatioEnabled(): Boolean {
        return aspectRatioEnabled
    }

    fun setAspectRatioEnabled(aspectRatioEnabled: Boolean) {
        this.aspectRatioEnabled = aspectRatioEnabled
        requestLayout()
    }

    fun getDominantMeasurement(): Int {
        return dominantMeasurement
    }

    fun setDominantMeasurement(dominantMeasurement: Int) {
        if (dominantMeasurement != MEASUREMENT_HEIGHT && dominantMeasurement != MEASUREMENT_WIDTH) {
            throw IllegalArgumentException("Invalid measurement type.")
        }
        this.dominantMeasurement = dominantMeasurement
        requestLayout()
    }

    companion object {
        // NOTE: These must be kept in sync with the AspectRatioImageView attributes in attrs.xml.
        const val MEASUREMENT_WIDTH = 0
        const val MEASUREMENT_HEIGHT = 1

        private const val DEFAULT_ASPECT_RATIO = 1f
        private const val DEFAULT_ASPECT_RATIO_ENABLED = false
        private const val DEFAULT_DOMINANT_MEASUREMENT = MEASUREMENT_WIDTH
    }
}