package com.example.sketch3d

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.view.ScaleGestureDetector

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {

    val renderer = MyRenderer()

    private var previousX: Float = 0f
    private var previousY: Float = 0f

    // Zoom (Yakınlaşma/Uzaklaşma) için dedektör
    private val scaleDetector: ScaleGestureDetector

    init {
        setEGLContextClientVersion(2)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY

        // Zoom algılayıcıyı tanımlıyoruz
        scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                // Renderer içindeki kamera mesafesini (zoom) değiştir
                renderer.zoomScale *= detector.scaleFactor
                // Zoom sınırları (Çok fazla uzaklaşmasın veya içine girmesin)
                renderer.zoomScale = renderer.zoomScale.coerceIn(0.5f, 5.0f)
                return true
            }
        })
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        // Önce iki parmakla zoom hareketini kontrol et
        scaleDetector.onTouchEvent(e)

        val x: Float = e.x
        val y: Float = e.y

        // Eğer iki parmak ekrandaysa döndürme/taşıma yapma (Sadece zoom)
        if (e.pointerCount > 1) {
            previousX = x
            previousY = y
            return true
        }

        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                // Dokunulan yerde bir küp var mı kontrolü ileride buraya gelecek
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = x - previousX
                val dy = y - previousY

                // EĞER "Taşıma Modu" aktifse (ileride eklenecek butonla):
                // renderer.selectedCube?.let { it.posX += dx * 0.01f; it.posZ += dy * 0.01f }
                
                // Şimdilik varsayılan olarak dünyayı döndürüyoruz
                renderer.angleX += dx * 0.3f
                renderer.angleY += dy * 0.3f
            }
        }

        previousX = x
        previousY = y
        return true
    }
}
