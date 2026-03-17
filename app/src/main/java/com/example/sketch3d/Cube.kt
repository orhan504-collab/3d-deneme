package com.example.sketch3d

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Cube {
    var posX = 0f
    var posZ = 0f
    // 256mm'nin 1/4'ü = 64mm. OpenGL birimi olarak 64.0f kullanıyoruz.
    var width = 64.0f 
    var height = 64.0f
    var depth = 64.0f

    private val vertexBuffer: FloatBuffer
    private val drawOrderBuffer: ByteBuffer

    private val cubeCoords = floatArrayOf(
        -0.5f,  1.0f,  0.5f,   // 0: Üst-Ön-Sol
        -0.5f,  0.0f,  0.5f,   // 1: Alt-Ön-Sol (Taban ızgarada kalsın diye 0.0f)
         0.5f,  0.0f,  0.5f,   // 2: Alt-Ön-Sağ
         0.5f,  1.0f,  0.5f,   // 3: Üst-Ön-Sağ
        -0.5f,  1.0f, -0.5f,   // 4: Üst-Arka-Sol
        -0.5f,  0.0f, -0.5f,   // 5: Alt-Arka-Sol
         0.5f,  0.0f, -0.5f,   // 6: Alt-Arka-Sağ
         0.5f,  1.0f, -0.5f    // 7: Üst-Arka-Sağ
    )

    private val drawOrder = byteArrayOf(
        0, 1, 2, 0, 2, 3, 4, 5, 6, 4, 6, 7, // Ön ve Arka
        4, 0, 3, 4, 3, 7, 5, 1, 2, 5, 2, 6, // Üst ve Alt
        4, 5, 1, 4, 1, 0, 3, 2, 6, 3, 6, 7  // Sol ve Sağ
    )

    init {
        vertexBuffer = ByteBuffer.allocateDirect(cubeCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply { put(cubeCoords); position(0) }
        }
        drawOrderBuffer = ByteBuffer.allocateDirect(drawOrder.size).apply {
            put(drawOrder); position(0)
        }
    }

    fun draw(vPMatrix: FloatArray, program: Int) {
        GLES20.glUseProgram(program)

        // Koyu Mavi ve Dolgulu (Alpha = 1.0)
        val colorHandle = GLES20.glGetUniformLocation(program, "vColor")
        GLES20.glUniform4fv(colorHandle, 1, floatArrayOf(0.0f, 0.1f, 0.4f, 1.0f), 0)

        val modelMatrix = FloatArray(16)
        val scratch = FloatArray(16)
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, posX, 0f, posZ)
        Matrix.scaleM(modelMatrix, 0, width, height, depth)
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, modelMatrix, 0)

        val matrixHandle = GLES20.glGetUniformLocation(program, "uVPMatrix")
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, scratch, 0)

        val posHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(posHandle)
        GLES20.glVertexAttribPointer(posHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        // GL_TRIANGLES kullanarak dolgulu çizim yapıyoruz
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.size, GLES20.GL_UNSIGNED_BYTE, drawOrderBuffer)
        GLES20.glDisableVertexAttribArray(posHandle)
    }
}
