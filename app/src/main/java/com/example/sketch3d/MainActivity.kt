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

        // Yeşil Artı Butonuna Tıklama Olayı
        binding.btnAddCube.setOnClickListener {
            // Renderer içindeki listeye küp ekle
            glView?.renderer?.addCube()
            
            /* MyGLSurfaceView içinde RENDERMODE_CONTINUOUSLY kullandığımız için 
               requestRender() çağırmaya teknik olarak gerek yok, 
               ama alışkanlık olması açısından durabilir.
            */
            glView?.requestRender()
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
