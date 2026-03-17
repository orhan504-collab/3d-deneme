package com.example.sketch3d

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Cube {
    var posX = 0f
    var posZ = 0f
    var isTransparent = false // İlk küp için true set edilecek

    // 64mm boyutunda başlangıç koordinatları (Y ekseni 0'dan başlar)
    // 8 Köşe * 3 Eksen (x, y, z)
    private val corners = floatArrayOf(
        -32f, 64f,  32f,  // 0: Üst-Ön-Sol
        -32f,  0f,  32f,  // 1: Alt-Ön-Sol
         32f,  0f,  32f,  // 2: Alt-Ön-Sağ
         32f, 64f,  32f,  // 3: Üst-Ön-Sağ
        -32f, 64f, -32f,  // 4: Üst-Arka-Sol
        -32f,  0f, -32f,  // 5: Alt-Arka-Sol
         32f,  0f, -32f,  // 6: Alt-Arka-Sağ
         32f, 64f, -32f   // 7: Üst-Arka-Sağ
    )

    private val drawOrder = byteArrayOf(
        0, 1, 2, 0, 2, 3, // Ön
        4, 5, 6, 4, 6, 7, // Arka
        4, 0, 3, 4, 3, 7, // Üst
        5, 1, 2, 5, 2, 6, // Alt
        4, 5, 1, 4, 1, 0, // Sol
        3, 2, 6, 3, 6, 7  // Sağ
    )

    private var vertexBuffer: FloatBuffer = createVertexBuffer()
    private val drawOrderBuffer: ByteBuffer = ByteBuffer.allocateDirect(drawOrder.size).apply {
        put(drawOrder); position(0)
    }

    private fun createVertexBuffer(): FloatBuffer {
        return ByteBuffer.allocateDirect(corners.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply { put(corners); position(0) }
        }
    }

    // Tek bir köşeyi (0-7 arası index) uzatmak için
    fun updateCorner(index: Int, dx: Float, dy: Float, dz: Float) {
        corners[index * 3] += dx
        corners[index * 3 + 1] += dy
        corners[index * 3 + 2] += dz
        // Buffer'ı yeni koordinatlarla güncelle
        vertexBuffer.put(corners)
        vertexBuffer.position(0)
    }

    fun draw(vPMatrix: FloatArray, program: Int) {
        GLES20.glUseProgram(program)

        val colorHandle = GLES20.glGetUniformLocation(program, "vColor")
        
        if (isTransparent) {
            // İlk açılıştaki küp: Şeffaf Beyaz/Gri
            GLES20.glUniform4fv(colorHandle, 1, floatArrayOf(1.0f, 1.0f, 1.0f, 0.3f), 0)
        } else {
            // Kullanıcının eklediği küp: Koyu Mavi
            GLES20.glUniform4fv(colorHandle, 1, floatArrayOf(0.0f, 0.1f, 0.4f, 1.0f), 0)
        }

        val modelMatrix = FloatArray(16)
        val scratch = FloatArray(16)
        Matrix.setIdentityM(modelMatrix, 0)
        // Pozisyonu sadece X ve Z'de kaydırıyoruz, Y ızgara üstünde sabit
        Matrix.translateM(modelMatrix, 0, posX, 0f, posZ)
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, modelMatrix, 0)

        val matrixHandle = GLES20.glGetUniformLocation(program, "uVPMatrix")
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, scratch, 0)

        val posHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(posHandle)
        GLES20.glVertexAttribPointer(posHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.size, GLES20.GL_UNSIGNED_BYTE, drawOrderBuffer)
        GLES20.glDisableVertexAttribArray(posHandle)
    }
}
