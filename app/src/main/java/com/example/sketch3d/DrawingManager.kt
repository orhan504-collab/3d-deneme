package com.example.sketch3d

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

object DrawingManager {
    // Çizilen noktaları tutan liste
    val points = mutableListOf<Float>()

    fun addPoint(x: Float, y: Float) {
        points.add(x)
        points.add(y)
        points.add(0.0f) // z koordinatı
    }

    // Listeyi OpenGL'in anlayacağı bir Byte buffer'a çevir
    fun getVertexBuffer(): FloatBuffer {
        val bb = ByteBuffer.allocateDirect(points.size * 4)
        bb.order(ByteOrder.nativeOrder())
        val buffer = bb.asFloatBuffer()
        buffer.put(points.toFloatArray())
        buffer.position(0)
        return buffer
    }
}
