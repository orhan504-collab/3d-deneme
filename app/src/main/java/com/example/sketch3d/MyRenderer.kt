package com.example.sketch3d

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyRenderer : GLSurfaceView.Renderer {
    private var program: Int = 0
    private lateinit var gridBuffer: FloatBuffer
    private var gridVertexCount = 0

    // Shader Kodları (Artık ShaderUtils'e gerek yok)
    private val vertexShaderCode = """
        attribute vec4 vPosition;
        void main() {
            gl_Position = vPosition;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec4 vColor;
        void main() {
            gl_FragColor = vColor;
        }
    """.trimIndent()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.18f, 0.2f, 0.25f, 1.0f) // Görseldeki gibi koyu mavi/gri

        // Shader'ları derle ve programı oluştur
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        // Grid (Tabla) verilerini oluştur
        val gridData = createGrid(20, 20)
        gridVertexCount = gridData.size / 3
        gridBuffer = ByteBuffer.allocateDirect(gridData.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(gridData)
                position(0)
            }
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glUseProgram(program)

        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, gridBuffer)

        val colorHandle = GLES20.glGetUniformLocation(program, "vColor")
        // Izgara rengi (Açık gri/mavi)
        GLES20.glUniform4f(colorHandle, 0.4f, 0.4f, 0.5f, 1.0f)

        GLES20.glDrawArrays(GLES20.GL_LINES, 0, gridVertexCount)
        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    private fun createGrid(xSize: Int, ySize: Int): FloatArray {
        val vertices = mutableListOf<Float>()
        val step = 0.2f // Çizgi aralığı
        for (i in -xSize..xSize) {
            val pos = i * step
            // Dikey çizgiler
            vertices.add(pos); vertices.add(-1.0f); vertices.add(0f)
            vertices.add(pos); vertices.add(1.0f); vertices.add(0f)
            // Yatay çizgiler
            vertices.add(-1.0f); vertices.add(pos); vertices.add(0f)
            vertices.add(1.0f); vertices.add(pos); vertices.add(0f)
        }
        return vertices.toFloatArray()
    }
}
