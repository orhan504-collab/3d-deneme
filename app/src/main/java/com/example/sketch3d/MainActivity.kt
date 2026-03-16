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

        // 1. OpenGL ekranını bağla
        val container = findViewById<FrameLayout>(R.id.gl_container)
        glView = MyGLSurfaceView(this)
        container.addView(glView)

        // 2. Butonları bağla (Hata almamak için ID'leri kontrol et)
        val btnAddCube = findViewById<ImageButton>(R.id.btn_add_cube)
        val btnTools = findViewById<ImageButton>(R.id.btn_tools)

        btnAddCube.setOnClickListener {
            // Şimdilik sadece log basalım veya basit bir işlem yapalım
        }
    }
}
