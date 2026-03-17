package com.example.sketch3d

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Cube {
    var posX = 0f
    var posY = 0.5f // Izgaranın tam üstünde durması için başlangıç yüksekliği
    var posZ = 0f
    var scale = 1.0f // Büyütme/Küçültme katsayısı

    private val vertexBuffer: FloatBuffer
    private val drawOrderBuffer: ByteBuffer

    // 1 birimlik standart küp köşeleri
    private val cubeCoords = floatArrayOf(
        -0.5f,  0.5f,  0.5f,   // 0: Ön-Üst-Sol
        -0.5f, -0.5f,  0.5f,   // 1: Ön-Alt-Sol
         0.5f, -0.5f,  0.5f,   // 2: Ön-Alt-Sağ
         0.5f,  0.5f,  0.5f,   // 3: Ön-Üst-Sağ
        -0.5f,  0.5f, -0.5f,   // 4: Arka-Üst-Sol
        -0.5f, -0.5f, -0.5f,   // 5: Arka-Alt-Sol
         0.5f, -0.5f, -0.5f,   // 6: Arka-Alt-Sağ
         0.5f,  0.5f, -0.5f    // 7: Arka-Üst-Sağ
    )

    // Yüzeyleri oluşturmak için üçgen sıralaması
    private val drawOrder = byteArrayOf(
        0, 1, 2, 0, 2, 3, // Ön
        4, 5, 6, 4, 6, 7, // Arka
        4, 0, 3, 4, 3, 7, // Üst
        5, 1, 2, 5, 2, 6, // Alt
        4, 5, 1, 4, 1, 0, // Sol
        3, 2, 6, 3, 6, 7  // Sağ
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

        // Koyu Mavi Renk Ataması
        val colorHandle = GLES20.glGetUniformLocation(program, "vColor")
        GLES20.glUniform4fv(colorHandle, 1, floatArrayOf(0.0f, 0.0f, 0.5f, 1.0f), 0)

        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        // Model Matrisi: Hareket, Boyut ve Konum hesaplama
        val modelMatrix = FloatArray(16)
        val scratch = FloatArray(16)
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, posX, posY * scale, posZ)
        Matrix.scaleM(modelMatrix, 0, scale, scale, scale)
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, modelMatrix, 0)

        val matrixHandle = GLES20.glGetUniformLocation(program, "uVPMatrix")
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, scratch, 0)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.size, GLES20.GL_UNSIGNED_BYTE, drawOrderBuffer)
        GLES20.glDisableVertexAttribArray(positionHandle)
    }
}
