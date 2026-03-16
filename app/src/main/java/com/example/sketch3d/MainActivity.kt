package com.example.sketch3d

import android.app.Activity
import android.opengl.GLSurfaceView
import android.os.Bundle

class MainActivity : Activity() {
    private lateinit var gLView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gLView = GLSurfaceView(this)
        gLView.setEGLContextClientVersion(2)
        gLView.setRenderer(MyRenderer()) // Bu artık dışarıdaki MyRenderer.kt'yi kullanacak
        setContentView(gLView)
    }
}
