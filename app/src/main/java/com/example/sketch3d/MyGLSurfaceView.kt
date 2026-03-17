package com.example.sketch3d

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector

// XML'den yüklenebilmesi için AttributeSet parametresi eklendi
class MyGLSurfaceView(context: Context, attrs: AttributeSet? = null) : GLSurfaceView(context, attrs) {
    
    // Renderer'ın Context alacak şekilde güncellendiğinden emin olun
    val renderer: MyRenderer = MyRenderer(context)
    
    private var previousX: Float = 0f
    private var previousY: Float = 0f
    private val scaleDetector: ScaleGestureDetector

    init {
        // OpenGL ES 2.0 context'i kullan
        setEGLContextClientVersion(2)
        
        // Renderer'ı ata
        setRenderer(renderer)
        
        // Sürekli çizim modu (Animasyonlar ve akıcı hareket için)
        renderMode = RENDERMODE_CONTINUOUSLY

        // Zoom (ölçeklendirme) algılayıcı
        scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                // Renderer içindeki zoom değerini güncelle
                renderer.zoomScale *= detector.scaleFactor
                // Yakınlaştırma sınırlarını belirle
                renderer.zoomScale = renderer.zoomScale.coerceIn(0.1f, 10.0f)
                return true
            }
        })
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        // Önce zoom hareketini kontrol et
        scaleDetector.onTouchEvent(e)
        
        val x = e.x
        val y = e.y

        // Çoklu dokunma (zoom) varsa döndürme/taşıma işlemini atla
        if (e.pointerCount > 1) { 
            previousX = x
            previousY = y
            return true 
        }

        when (e.action) {
            MotionEvent.ACTION_MOVE -> {
                val dx = x - previousX
                val dy = y - previousY
                
                // Renderer içindeki aktif küpü veya sahneyi kontrol et
                // Not: renderer.cubes listenizin boş olmadığından emin olun
                val activeCube = renderer.cubes.firstOrNull()
                
                if (activeCube != null) {
                    // Küpü taşıma mantığı (Hassasiyet zoom değerine göre ayarlandı)
                    activeCube.posX += dx * 0.01f * renderer.zoomScale
                    activeCube.posZ += dy * 0.01f * renderer.zoomScale
                } else {
                    // Sahne döndürme mantığı
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
