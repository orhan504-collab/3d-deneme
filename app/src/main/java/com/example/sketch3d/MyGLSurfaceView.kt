package com.example.sketch3d

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.view.ScaleGestureDetector

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {

    val renderer = MyRenderer()

    private var previousX: Float = 0f
    private var previousY: Float = 0f

    // Zoom dedektörü
    private val scaleDetector: ScaleGestureDetector

    init {
        setEGLContextClientVersion(2)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY

        scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                renderer.zoomScale *= detector.scaleFactor
                renderer.zoomScale = renderer.zoomScale.coerceIn(0.2f, 10.0f)
                return true
            }
        })
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(e)

        val x: Float = e.x
        val y: Float = e.y

        // İki parmak zoom yaparken rotasyon veya taşıma yapma
        if (e.pointerCount > 1) {
            previousX = x
            previousY = y
            return true
        }

        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                // Dokunma başladığında koordinatları kaydet
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = x - previousX
                val dy = y - previousY

                // TAŞIMA MANTIĞI: 
                // Renderer içindeki listeye otomatik eklenen ilk küpü hedef alıyoruz
                val activeCube = renderer.cubes.firstOrNull()

                if (activeCube != null) {
                    // Küpü ızgara üzerinde (X ve Z ekseninde) kaydırıyoruz
                    // Hassasiyeti zoom seviyesine göre ayarlıyoruz ki uzaktayken de rahat taşınsın
                    activeCube.posX += dx * 0.1f * renderer.zoomScale
                    activeCube.posZ += dy * 0.1f * renderer.zoomScale
                } else {
                    // Eğer küp yoksa ekranı döndürmeye devam et
                    renderer.angleX += dx * 0.3f
                    renderer.angleY += dy * 0.3f
                }
            }
        }

        previousX = x
        previousY = y
        return true
    }
}
