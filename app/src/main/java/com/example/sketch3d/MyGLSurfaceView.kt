package com.example.sketch3d

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {
    // Renderer'ı kendi sınıf adıyla (MyRenderer) tanımlıyoruz
    val myRenderer = MyRenderer() 

    private var previousX: Float = 0f
    private var previousY: Float = 0f

    init {
        setEGLContextClientVersion(2)
        setRenderer(myRenderer) // Kendi renderer'ımızı atıyoruz
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val x: Float = e.x
        val y: Float = e.y

        when (e.action) {
            MotionEvent.ACTION_MOVE -> {
                val dx = x - previousX
                val dy = y - previousY
                
                // myRenderer üzerinden değişkenlere erişiyoruz
                myRenderer.angleX += dx * 0.3f
                myRenderer.angleY += dy * 0.3f
                requestRender()
            }
        }
        previousX = x
        previousY = y
        return true
    }
}
