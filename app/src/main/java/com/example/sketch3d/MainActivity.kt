package com.example.sketch3d

import android.app.Activity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton

class MainActivity : Activity() {
    private var glView: MyGLSurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. OpenGL Görünümünü Container'a bağla
        val container = findViewById<FrameLayout>(R.id.gl_container)
        glView = MyGLSurfaceView(this)
        container.addView(glView)

        // 2. Butonları bağla (Örnek: Küp Ekle butonu)
        // XML'deki ImageButton'a bir ID vermemişsin, 
        // önce XML'e id ekleyelim, sonra burada kullanalım.
    }
}
