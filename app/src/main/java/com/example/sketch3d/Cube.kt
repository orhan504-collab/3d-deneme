package com.example.sketch3d

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Cube {
    private val vertexBuffer: FloatBuffer
    private var program: Int = 0

    // Küpün 8 köşesinin koordinatları
    private val cubeCoords = floatArrayOf(
        -1.0f,  1.0f,  1.0f,   // ön-üst-sol
        -1.0f, -1.0f,  1.0f,   // ön-alt-sol
         1.0f, -1.0f,  1.0f,   // ön-alt-sağ
         1.0f,  1.0f,  1.0f,   // ön-üst-sağ
        -1.0f,  1.0f, -1.0f,   // arka-üst-sol
        -1.0f, -1.0f, -1.0f,   // arka-alt-sol
         1.0f, -1.0f, -1.0f,   // arka-alt-sağ
         1.0f,  1.0f, -1.0f    // arka-üst-sağ
    )

    // Çizgi sırası (Küpü çizgilerle oluşturmak için)
    private val indices = shortArrayOf(
        0, 1, 1, 2, 2, 3, 3, 0, // Ön yüz
        4, 5, 5, 6, 6, 7, 7, 4, // Arka yüz
        0, 4, 1, 5, 2, 6, 3, 7  // Aralar
    )
    private val indexBuffer = ByteBuffer.allocateDirect(indices.size * 2).run {
        order(ByteOrder.nativeOrder())
        asShortBuffer().apply { put(indices); position(0) }
    }

    init {
        vertexBuffer = ByteBuffer.allocateDirect(cubeCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply { put(cubeCoords); position(0) }
        }
    }

    fun draw(vPMatrix: FloatArray, colorProgram: Int) {
        GLES20.glUseProgram(colorProgram)

        val positionHandle = GLES20.glGetAttribLocation(colorProgram, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        val matrixHandle = GLES20.glGetUniformLocation(colorProgram, "uVPMatrix")
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, vPMatrix, 0)

        // Küpü beyaz çizgilerle çiz
        GLES20.glDrawElements(GLES20.GL_LINES, indices.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer)
        GLES20.glDisableVertexAttribArray(positionHandle)
    }
}
