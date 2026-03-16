package com.example.sketch3d

import android.app.Activity
import android.os.Bundle
import android.widget.FrameLayout

class MainActivity : Activity() {
    private lateinit var glView: MyGLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // XML layout'u bağlıyoruz
        setContentView(R.layout.activity_main)

        // XML'deki FrameLayout'u bul
        val container = findViewById<FrameLayout>(R.id.gl_container)
        
        // OpenGL görünümünü oluştur ve container'a ekle
        glView = MyGLSurfaceView(this)
        container.addView(glView)
    }
}
