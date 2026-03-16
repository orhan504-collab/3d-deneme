package com.example.sketch3d

import android.app.Activity
import android.os.Bundle
import com.example.sketch3d.databinding.ActivityMainBinding

class MainActivity : Activity() {
    private lateinit var binding: ActivityMainBinding
    private var glView: MyGLSurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // View Binding başlatma
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // gl_container içine OpenGL görünümünü ekle
        glView = MyGLSurfaceView(this)
        binding.glContainer.addView(glView)

        // Butonlara erişim artık çok daha kolay ve güvenli
        binding.btnAddCube.setOnClickListener {
            // Küp ekleme kodu buraya gelecek
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
