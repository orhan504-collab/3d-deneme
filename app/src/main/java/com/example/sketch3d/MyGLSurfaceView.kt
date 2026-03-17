package com.example.sketch3d

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector

class MyGLSurfaceView(context: Context, attrs: AttributeSet? = null) : GLSurfaceView(context, attrs) {

    // XML üzerinden yükleme için AttributeSet desteği eklendi
    val renderer: MyRenderer = MyRenderer(context)

    private var previousX: Float = 0f
    private var previousY: Float = 0f
    private val scaleDetector: ScaleGestureDetector

    init {
        // OpenGL ES 2.0 ayarı
        setEGLContextClientVersion(2)
        setRenderer(renderer)
        
        // Verimlilik ve akıcılık için sürekli çizim modu
        renderMode = RENDERMODE_CONTINUOUSLY

        // İki parmakla yakınlaştırma (Zoom) algılayıcısı
        scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                renderer.zoomScale *= detector.scaleFactor
                // Yakınlaştırma sınırları (0.1x - 10x)
                renderer.zoomScale = renderer.zoomScale.coerceIn(0.1f, 10.0f)
                return true
            }
        })
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        // Önce zoom hareketini işle
        scaleDetector.onTouchEvent(e)
        
        val x = e.x
        val y = e.y

        // Çoklu dokunma algılandığında döndürme/taşıma işlemini durdur
        if (e.pointerCount > 1) {
            previousX = x
            previousY = y
            return true
        }

        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                // Dokunma başladığında koordinatları kaydet (Sıçramayı önler)
                previousX = x
                previousY = y
                
                // İleride buraya: Dokunulan noktadaki Vertex/Edge seçimi için Raycasting eklenecek
            }
            
            MotionEvent.ACTION_MOVE -> {
                val dx = x - previousX
                val dy = y - previousY

                // Null güvenliği: Renderer içindeki nesne listesini kontrol et
                try {
                    val activeCube = renderer.cubes.firstOrNull()
                    
                    if (activeCube != null) {
                        // Eğer bir küp varsa ve 'Obje Modu' gibi bir taşıma gerekiyorsa:
                        // Hassasiyet zoom seviyesine göre dinamik olarak ayarlandı
                        activeCube.posX += dx * 0.01f * renderer.zoomScale
                        activeCube.posZ += dy * 0.01f * renderer.zoomScale
                    } else {
                        // Küp yoksa sahneyi döndür (Gözlem modu)
                        renderer.angleX += dx * 0.3f
                        renderer.angleY += dy * 0.3f
                    }
                } catch (ex: Exception) {
                    // Liste boşsa veya henüz yüklenmediyse sessizce geç
                }
            }
        }
        
        previousX = x
        previousY = y
        return true
    }

    // Uygulama arka plana alındığında OpenGL döngüsünü durdurarak pil tasarrufu sağlar
    override fun onPause() {
        super.onPause()
        // Gerekirse özel duraklatma mantığı buraya
    }
}
