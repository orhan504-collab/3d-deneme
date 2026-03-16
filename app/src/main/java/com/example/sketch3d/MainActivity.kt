package com.example.sketch3d

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout

class MainActivity : Activity() {
    private lateinit var glView: MyGLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val container = findViewById<FrameLayout>(R.id.gl_container)
        glView = MyGLSurfaceView(this)
        container.addView(glView)

        findViewById<Button>(R.id.btn_cube).setOnClickListener {
            glView.renderer.toggleCube()
        }
    }
}
