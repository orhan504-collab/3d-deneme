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
    private val rotationMatrix = FloatArray(16)

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
        uniform vec4 vColor;
        void main() {
            gl_FragColor = vColor;
        }
    """.trimIndent()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.15f, 0.17f, 0.2f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        val gridData = createGrid(20, 20)
        gridVertexCount = gridData.size / 3
        gridBuffer = ByteBuffer.allocateDirect(gridData.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply { put(gridData); position(0) }
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 4f, 10f, 0f, 0f, 0f, 0f, 1f, 0f)
        Matrix.setRotateM(rotationMatrix, 0, angleX, 0f, 1f, 0f)
        Matrix.rotateM(rotationMatrix, 0, angleY, 1f, 0f, 0f)
        
        val tempMatrix = FloatArray(16)
        Matrix.multiplyMM(tempMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(vPMatrix, 0, tempMatrix, 0, rotationMatrix, 0)

        GLES20.glUseProgram(program)
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(program, "uVPMatrix"), 1, false, vPMatrix, 0)
        
        val posHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(posHandle)
        GLES20.glVertexAttribPointer(posHandle, 3, GLES20.GL_FLOAT, false, 0, gridBuffer)
        GLES20.glUniform4f(GLES20.glGetUniformLocation(program, "vColor"), 0.4f, 0.4f, 0.5f, 1.0f)
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, gridVertexCount)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 50f)
    }

    private fun loadShader(type: Int, shaderCode: String) = GLES20.glCreateShader(type).also {
        GLES20.glShaderSource(it, shaderCode)
        GLES20.glCompileShader(it)
    }

    private fun createGrid(xSize: Int, ySize: Int): FloatArray {
        val vertices = mutableListOf<Float>()
        val step = 0.5f
        for (i in -xSize..xSize) {
            val pos = i * step
            vertices.add(pos); vertices.add(0f); vertices.add(-ySize * step)
            vertices.add(pos); vertices.add(0f); vertices.add(ySize * step)
            vertices.add(-xSize * step); vertices.add(0f); vertices.add(pos)
            vertices.add(xSize * step); vertices.add(0f); vertices.add(pos)
        }
        return vertices.toFloatArray()
    }
}
