package com.example.sketch3d

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyRenderer : GLSurfaceView.Renderer {
    private var program: Int = 0

    // Vertex ve Fragment Shader kodlarını doğrudan buraya tanımlıyoruz (assets'ten okumak yerine)
    private val vertexShaderCode = "attribute vec4 vPosition; void main() { gl_Position = vPosition; }"
    private val fragmentShaderCode = "precision mediump float; void main() { gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0); }"

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        program = ShaderUtils.createProgram(vertexShaderCode, fragmentShaderCode)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        
        if (DrawingManager.points.size < 3) return

        GLES20.glUseProgram(program)
        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)

        // Veriyi DrawingManager'dan al
        val vertexBuffer = DrawingManager.getVertexBuffer()
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        
        // Çizgileri çiz
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, DrawingManager.points.size / 3)
        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }
}
