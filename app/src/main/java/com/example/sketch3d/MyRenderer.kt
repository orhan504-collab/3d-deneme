package com.example.sketch3d

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyRenderer : GLSurfaceView.Renderer {

    // Dokunmatik hareketler için değişkenler
    var angleX: Float = 0f
    var angleY: Float = 0f

    private var program: Int = 0
    private lateinit var gridBuffer: FloatBuffer
    private var gridVertexCount = 0

    // Matris tanımlamaları
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val rotationMatrix = FloatArray(16)

    // Shader Kodları
    private val vertexShaderCode = """
        uniform mat4 uVPMatrix;
        attribute vec4 vPosition;
        void main() {
            gl_Position = uVPMatrix * vPosition;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        void main() {
            gl_FragColor = vec4(0.5, 0.5, 0.55, 1.0); // Izgara rengi (Açık Gri)
        }
    """.trimIndent()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // 1. ADIM: Arka plan rengini Koyu Mavi yap (Çalıştığını anlamak için)
        GLES20.glClearColor(0.1f, 0.2f, 0.4f, 1.0f) 
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        try {
            // ShaderUtils kullanarak shaderları yükle
            val vertexShader = ShaderUtils.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
            val fragmentShader = ShaderUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

            if (vertexShader != 0 && fragmentShader != 0) {
                program = GLES20.glCreateProgram().also {
                    GLES20.glAttachShader(it, vertexShader)
                    GLES20.glAttachShader(it, fragmentShader)
                    GLES20.glLinkProgram(it)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. ADIM: Izgara (Grid) verisini oluştur
        val gridData = createGrid(20, 1.0f)
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
        // Ekranı temizle
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // ÇÖKMEYİ ÖNLEYEN KONTROL: Eğer program veya buffer hazır değilse çizme
        if (program == 0 || !::gridBuffer.isInitialized) return

        // 3. ADIM: Kamerayı ayarla (Görsel #2'deki gibi perspektif için)
        Matrix.setLookAtM(viewMatrix, 0, 10f, 12f, 20f, 0f, 0f, 0f, 0f, 1f, 0f)

        // Rotasyonları uygula
        Matrix.setRotateM(rotationMatrix, 0, angleX, 0f, 1f, 0f)
        Matrix.rotateM(rotationMatrix, 0, angleY, 1f, 0f, 0f)

        // Matrisleri birleştir
        val tempMatrix = FloatArray(16)
        Matrix.multiplyMM(tempMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(vPMatrix, 0, tempMatrix, 0, rotationMatrix, 0)

        // Çizimi yap
        GLES20.glUseProgram(program)

        val matrixHandle = GLES20.glGetUniformLocation(program, "uVPMatrix")
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, vPMatrix, 0)

        val posHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(posHandle)
        GLES20.glVertexAttribPointer(posHandle, 3, GLES20.GL_FLOAT, false, 0, gridBuffer)

        GLES20.glDrawArrays(GLES20.GL_LINES, 0, gridVertexCount)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height.toFloat()
        Matrix.perspectiveM(projectionMatrix, 0, 45f, ratio, 0.1f, 100f)
    }

    private fun createGrid(size: Int, step: Float): FloatArray {
        val v = mutableListOf<Float>()
        for (i in -size..size) {
            val p = i * step
            // X çizgileri
            v.addAll(listOf(p, 0f, -size * step, p, 0f, size * step))
            // Z çizgileri
            v.addAll(listOf(-size * step, 0f, p, size * step, 0f, p))
        }
        return v.toFloatArray()
    }
}
