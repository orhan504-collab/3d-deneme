package com.example.sketch3d

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector

class MyGLSurfaceView(context: Context, attrs: AttributeSet? = null) : GLSurfaceView(context, attrs) {

    val renderer: MyRenderer = MyRenderer(context)
    private var previousX: Float = 0f
    private var previousY: Float = 0f
    private val scaleDetector: ScaleGestureDetector

    init {
        setEGLContextClientVersion(2)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY

        // Zoom (Ölçeklendirme) Algılayıcı
        scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                // Zoom hassasiyetini artırdık
                val scale = detector.scaleFactor
                // Renderer'daki zoomScale değerini ters orantılı güncelliyoruz (Kamera yaklaşması için)
                renderer.zoomScale /= scale 
                renderer.zoomScale = renderer.zoomScale.coerceIn(0.1f, 5.0f)
                return true
            }
        })
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        // Zoom işlemini kontrol et
        scaleDetector.onTouchEvent(e)
        
        val x = e.x
        val y = e.y

        // İki parmakla zoom yapılıyorsa döndürme/taşıma işlemini iptal et
        if (e.pointerCount > 1) {
            previousX = x
            previousY = y
            return true
        }

        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                previousX = x
                previousY = y
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = x - previousX
                val dy = y - previousY

                // Hassasiyet katsayıları (Deneyerek optimize edildi)
                val rotationFactor = 0.3f
                val translationFactor = 0.5f * renderer.zoomScale

                val activeCube = renderer.cubes.firstOrNull()
                
                // EKRANIN ALT KISMINDAKİ MENÜYE DEĞMİYORSA (Örn: Ekranın %80'inden yukarısı)
                if (y < height * 0.8f) {
                    if (activeCube != null) {
                        // KÜP TAŞIMA: Hassasiyet artırıldı
                        activeCube.posX += dx * translationFactor
                        activeCube.posZ += dy * translationFactor
                    } else {
                        // SAHNE DÖNDÜRME: Daha hızlı tepki
                        renderer.angleX += dx * rotationFactor
                        renderer.angleY += dy * rotationFactor
                    }
                }
            }
        }
        previousX = x
        previousY = y
        return true
    }
}
