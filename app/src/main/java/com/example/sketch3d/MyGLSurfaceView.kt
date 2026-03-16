package com.example.sketch3d

import android.content.Context
import android.opengl.GLSurfaceView

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer = MyRenderer()

    init {
        setEGLContextClientVersion(2) // OpenGL ES 2.0
        setRenderer(renderer)
    }
}
