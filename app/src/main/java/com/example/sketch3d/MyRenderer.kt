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
    private var program: Int = 0
    private lateinit var gridBuffer: FloatBuffer
    private var gridVertexCount = 0

    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val rotationMatrix = FloatArray(16) // Dönüş için yeni matris

    // Hata veren eksik değişkenleri buraya ekledik
    var angleX: Float = 0f
    var angleY: Float = 0f

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
            gl_FragColor = vec4(0.5, 0.5, 0.6, 1.0); 
        }
    """.trimIndent()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.1f, 0.1f, 0.12f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST) // Derinlik testini etkinleştir
        
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        val gridData = mutableListOf<Float>()
        for (i in -10..10) {
            val f = i.toFloat() * 0.5f
            gridData.addAll(listOf(f, 0f, -5f, f, 0f, 5f))
            gridData.addAll(listOf(-5f, 0f, f, 5f, 0f, f))
        }
        val floatArray = gridData.toFloatArray()
        gridVertexCount = floatArray.size / 3
        gridBuffer = ByteBuffer.allocateDirect(floatArray.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply { put(floatArray); position(0) }
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // 1. Statik Kamera Konumu
        Matrix.setLookAtM(viewMatrix, 0, 0f, 6f, 12f, 0f, 0f, 0f, 0f, 1f, 0f)

        // 2. Kullanıcı etkileşimine göre dönüş matrisini hesapla
        val scratch = FloatArray(16)
        val tempRotation = FloatArray(16)
        Matrix.setRotateM(rotationMatrix, 0, angleX, 0f, 1f, 0f)
        Matrix.rotateM(rotationMatrix, 0, angleY, 1f, 0f, 0f)

        // 3. Matrisleri birleştir (Projection * View * Rotation)
        Matrix.multiplyMM(scratch, 0, viewMatrix, 0, rotationMatrix, 0)
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, scratch, 0)

        GLES20.glUseProgram(program)
        val matrixHandle = GLES20.glGetUniformLocation(program, "uVPMatrix")
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, vPMatrix, 0)

        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, gridBuffer)
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, gridVertexCount)
        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height.toFloat()
        Matrix.perspectiveM(projectionMatrix, 0, 45f, ratio, 0.1f, 100f)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }
}
