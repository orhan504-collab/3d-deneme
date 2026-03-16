package com.example.sketch3d

import android.app.Activity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast

class MainActivity : Activity() {
    private var glView: MyGLSurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContentView(R.layout.activity_main)

            // 1. OpenGL Container kontrolü
            val container = findViewById<FrameLayout>(R.id.gl_container)
            if (container != null) {
                glView = MyGLSurfaceView(this)
                container.addView(glView)
            }

            // 2. Butonları güvenli bir şekilde bağla (Null Check)
            val btnAddCube = findViewById<ImageButton>(R.id.btn_add_cube)
            btnAddCube?.setOnClickListener {
                Toast.makeText(this, "Küp Ekleme Özelliği Yakında!", Toast.LENGTH_SHORT).show()
            }

            val btnTools = findViewById<ImageButton>(R.id.btn_tools)
            btnTools?.setOnClickListener {
                // Araçlar menüsü işlemleri
            }
            
        } catch (e: Exception) {
            // Eğer bir hata olursa uygulamanın neden durduğunu anlamak için log
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        glView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        glView?.onResume()
    }
}
