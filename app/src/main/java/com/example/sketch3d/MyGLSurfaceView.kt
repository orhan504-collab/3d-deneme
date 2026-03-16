package com.example.sketch3d

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {
    // Buradaki tipleme hatayı çözer
    val myRenderer = MyRenderer()

    private var previousX: Float = 0f
    private var previousY: Float = 0f

    init {
        setEGLContextClientVersion(2)
        setRenderer(myRenderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val x: Float = e.x
        val y: Float = e.y
        if (e.action == MotionEvent.ACTION_MOVE) {
            val dx = x - previousX
            val dy = y - previousY
            myRenderer.angleX += dx * 0.3f
            myRenderer.angleY += dy * 0.3f
        }
        previousX = x
        previousY = y
        return true
    }
}
