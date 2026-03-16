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
    // BU SATIRLAR ÇOK ÖNEMLİ: MyGLSurfaceView bu değişkenlere erişecek
    var angleX: Float = 0f
    var angleY: Float = 0f

    private var program: Int = 0
    private lateinit var gridBuffer: FloatBuffer
    private var gridVertexCount = 0

    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val rotationMatrix = FloatArray(16)

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
            gl_FragColor = vec4(0.4, 0.4, 0.45, 1.0); // Izgara Rengi
        }
    """.trimIndent()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.1f, 0.2f, 0.4f, 1.0f) // Koyu Mavi
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        // Izgara verisini oluştur (createGrid fonksiyonu aşağıda)
        val gridData = createGrid(20, 0.5f)
        gridVertexCount = gridData.size / 3
        gridBuffer = ByteBuffer.allocateDirect(gridData.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply { put(gridData); position(0) }
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Kamera Açısı
        Matrix.setLookAtM(viewMatrix, 0, 8f, 10f, 15f, 0f, 0f, 0f, 0f, 1f, 0f)
        
        // Dokunmatik hareketi uygula
        Matrix.setRotateM(rotationMatrix, 0, angleX, 0f, 1f, 0f)
        Matrix.rotateM(rotationMatrix, 0, angleY, 1f, 0f, 0f)

        val tempMatrix = FloatArray(16)
        val combinedMatrix = FloatArray(16)
        Matrix.multiplyMM(tempMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(combinedMatrix, 0, tempMatrix, 0, rotationMatrix, 0)

        GLES20.glUseProgram(program)
        val matrixHandle = GLES20.glGetUniformLocation(program, "uVPMatrix")
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, combinedMatrix, 0)

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

    private fun loadShader(type: Int, code: String) = GLES20.glCreateShader(type).also {
        GLES20.glShaderSource(it, code); GLES20.glCompileShader(it)
    }

    private fun createGrid(size: Int, step: Float): FloatArray {
        val v = mutableListOf<Float>()
        for (i in -size..size) {
            val p = i * step
            v.addAll(listOf(p, 0f, -size*step, p, 0f, size*step))
            v.addAll(listOf(-size*step, 0f, p, size*step, 0f, p))
        }
        return v.toFloatArray()
    }
}
