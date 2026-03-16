package com.example.sketch3d

import android.content.Context
import android.opengl.GLSurfaceView

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {
    val renderer = MyRenderer()

    init {
        setEGLContextClientVersion(2)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }
}
