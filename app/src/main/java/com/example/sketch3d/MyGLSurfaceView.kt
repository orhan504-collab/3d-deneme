package com.example.sketch3d

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.view.ScaleGestureDetector

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {
    val renderer = MyRenderer()
    private var previousX: Float = 0f
    private var previousY: Float = 0f
    private val scaleDetector: ScaleGestureDetector

    init {
        setEGLContextClientVersion(2)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
        scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(d: ScaleGestureDetector): Boolean {
                renderer.zoomScale *= d.scaleFactor
                renderer.zoomScale = renderer.zoomScale.coerceIn(0.1f, 10.0f)
                return true
            }
        })
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(e)
        val x = e.x; val y = e.y
        if (e.pointerCount > 1) { previousX = x; previousY = y; return true }

        when (e.action) {
            MotionEvent.ACTION_MOVE -> {
                val dx = x - previousX
                val dy = y - previousY
                val activeCube = renderer.cubes.firstOrNull()
                
                if (activeCube != null) {
                    // Taşıma hassasiyeti
                    activeCube.posX += dx * 0.5f * renderer.zoomScale
                    activeCube.posZ += dy * 0.5f * renderer.zoomScale
                } else {
                    renderer.angleX += dx * 0.3f
                    renderer.angleY += dy * 0.3f
                }
            }
        }
        previousX = x; previousY = y
        return true
    }
}
