package com.example.sketch3d

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout

class MainActivity : Activity() {
    private lateinit var glView: MyGLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // R.layout.activity_main, /res/layout/activity_main.xml dosyasını bağlar
        setContentView(R.layout.activity_main)

        // XML'deki FrameLayout'u buluyoruz
        val container = findViewById<FrameLayout>(R.id.gl_container)
        glView = MyGLSurfaceView(this)
        container.addView(glView)

        // Butonun tıklanmasını yönetiyoruz
        val btnCube = findViewById<Button>(R.id.btn_cube)
        btnCube.setOnClickListener {
            glView.renderer.toggleCube()
        }
    }
}
