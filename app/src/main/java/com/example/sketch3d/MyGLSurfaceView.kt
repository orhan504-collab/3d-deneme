package com.example.sketch3d

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {
    val renderer = MyRenderer()
    private var previousX: Float = 0f
    private var previousY: Float = 0f

    init {
        setEGLContextClientVersion(2)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val x: Float = e.x
        val y: Float = e.y

        when (e.action) {
            MotionEvent.ACTION_MOVE -> {
                val dx: Float = x - previousX
                val dy: Float = y - previousY
                
                renderer.angleX += dx * 0.3f
                renderer.angleY += dy * 0.3f
                requestRender()
            }
        }
        previousX = x
        previousY = y
        return true
    }
}
