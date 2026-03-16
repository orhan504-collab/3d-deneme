package com.example.sketch3d

import android.content.Context
import android.opengl.GLSurfaceView

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {
    // 'private' anahtar kelimesini kaldırdık ki dışarıdan erişilebilsin
    val renderer = MyRenderer()

    init {
        setEGLContextClientVersion(2)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }
}
