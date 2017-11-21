package xyz.siddharthseth.panettone

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.RelativeLayout

class CustomZoomView(context: Context?, attrs: AttributeSet?) : RelativeLayout(context, attrs) {
    private var scaleDetector: ScaleGestureDetector? = null
    var zoomFactor = 0.0f
    val TAG = "CustomZoomView"

    fun CustomZoomView(context: Context) {
        scaleDetector = ScaleGestureDetector(context, ScaleListener())
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        scaleDetector?.onTouchEvent(event)
        return true
    }

    fun resetZoomFactor() {
        zoomFactor = 0.0f
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            zoomFactor += ((detector.scaleFactor.toDouble() - 1) / 3f).toFloat()
            zoomFactor = if (zoomFactor > 1.0f) 1.0f else zoomFactor
            zoomFactor = if (zoomFactor < 0.0f) 0.0f else zoomFactor

            CameraFragment.setZoomLevel(zoomFactor)

            Log.d(TAG, "scale factor " + detector.scaleFactor)
            invalidate()
            return true
        }
    }
}