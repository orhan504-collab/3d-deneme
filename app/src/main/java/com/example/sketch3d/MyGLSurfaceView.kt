package com.example.sketch3d

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector

class MyGLSurfaceView(context: Context, attrs: AttributeSet? = null) : GLSurfaceView(context, attrs) {

    // Renderer nesnesi (Eğer MyRenderer context istemiyorsa parametreyi silin)
    val renderer: MyRenderer = MyRenderer(context)

    private var previousX: Float = 0f
    private var previousY: Float = 0f
    private val scaleDetector: ScaleGestureDetector

    init {
        setEGLContextClientVersion(2)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY

        scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                renderer.zoomScale *= detector.scaleFactor
                renderer.zoomScale = renderer.zoomScale.coerceIn(0.1f, 10.0f)
                return true
            }
        })
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(e)
        val x = e.x
        val y = e.y

        if (e.pointerCount > 1) {
            previousX = x
            previousY = y
            return true
        }

        when (e.action) {
            MotionEvent.ACTION_MOVE -> {
                val dx = x - previousX
                val dy = y - previousY

                // Null güvenliği için renderer.cubes listesini kontrol ediyoruz
                try {
                    val activeCube = renderer.cubes.firstOrNull()
                    if (activeCube != null) {
                        activeCube.posX += dx * 0.01f * renderer.zoomScale
                        activeCube.posZ += dy * 0.01f * renderer.zoomScale
                    } else {
                        renderer.angleX += dx * 0.3f
                        renderer.angleY += dy * 0.3f
                    }
                } catch (ex: Exception) {
                    // Liste henüz hazır değilse hata vermemesi için
                }
            }
        }
        previousX = x
        previousY = y
        return true
    }
}
