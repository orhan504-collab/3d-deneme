package com.example.sketch3d

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyRenderer : GLSurfaceView.Renderer {
    private var isInitialize = false
    private var program: Int = 0
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var gridBuffer: FloatBuffer

    // Basit bir küp koordinatları (8 köşe)
    private val cubeCoords = floatArrayOf(
        -0.5f, -0.5f, -0.5f, // 0
         0.5f, -0.5f, -0.5f, // 1
         0.5f,  0.5f, -0.5f, // 2
        -0.5f,  0.5f, -0.5f, // 3
        -0.5f, -0.5f,  0.5f, // 4
         0.5f, -0.5f,  0.5f, // 5
         0.5f,  0.5f,  0.5f, // 6
        -0.5f,  0.5f,  0.5f  // 7
    )

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        if (!isInitialize) {
            GLES20.glClearColor(0.15f, 0.15f, 0.2f, 1.0f) // Koyu Gri Arka Plan

            // Shader programını oluştur
            program = ShaderUtils.createProgram(vertexShaderCode, fragmentShaderCode)
            
            // Küp verilerini hazırla
            vertexBuffer = prepareFloatBuffer(cubeCoords)
            
            // Grid verilerini hazırla (Sadece X/Y düzleminde çizgiler)
            gridBuffer = prepareFloatBuffer(createGrid(10, 10))

            isInitialize = true
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glUseProgram(program)

        // 1. Grid (Tabla) Çizimi (Yeşilimsi Çizgiler)
        drawGrid()

        // 2. Küp Çizimi (Sembolik olarak, gridin ortasında)
        drawCube()
    }

    private fun drawGrid() {
        // Grid çizimi için koordinatları shader'a gönder
        val pos = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(pos)
        GLES20.glVertexAttribPointer(pos, 3, GLES20.GL_FLOAT, false, 0, gridBuffer)
        
        // Çizgi rengini ayarla (Açık Yeşil)
        val color = GLES20.glGetUniformLocation(program, "vColor")
        GLES20.glUniform4f(color, 0.5f, 0.8f, 0.5f, 0.5f) 
        
        // Grid çizgilerini çiz (Her çizgi 2 köşe gerektirir)
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, gridBuffer.limit() / 3)
    }

    private fun drawCube() {
        // Küp çizimi için koordinatları shader'a gönder
        val pos = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glVertexAttribPointer(pos, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        
        // Küp rengini ayarla (Gri)
        val color = GLES20.glGetUniformLocation(program, "vColor")
        GLES20.glUniform4f(color, 0.7f, 0.7f, 0.7f, 1.0f)
        
        // Küpü çiz (Tel kafes olarak, GL_LINES kullanarak)
        // (Şimdilik sembolik, tam küp için indisler lazım)
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertexBuffer.limit() / 3)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    private fun prepareFloatBuffer(data: FloatArray): FloatBuffer {
        return ByteBuffer.allocateDirect(data.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(data)
                position(0)
            }
        }
    }

    // Basit bir Grid koordinat dizisi oluşturur (X/Y düzleminde)
    private fun createGrid(xSize: Int, ySize: Int): FloatArray {
        val vertices = mutableListOf<Float>()
        for (i in -xSize..xSize) {
            vertices.add(i.toFloat()); vertices.add(-ySize.toFloat()); vertices.add(0f) // Line start
            vertices.add(i.toFloat()); vertices.add(ySize.toFloat()); vertices.add(0f)  // Line end
        }
        for (i in -ySize..ySize) {
            vertices.add(-xSize.toFloat()); vertices.add(i.toFloat()); vertices.add(0f) // Line start
            vertices.add(xSize.toFloat()); vertices.add(i.toFloat()); vertices.add(0f)  // Line end
        }
        return vertices.toFloatArray()
    }

    // Basit Shader Kodları
    private val vertexShaderCode = "attribute vec4 vPosition;" +
            "void main() { gl_Position = vPosition; }"
    private val fragmentShaderCode = "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() { gl_FragColor = vColor; }"
}
