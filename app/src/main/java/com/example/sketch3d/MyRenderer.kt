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

    // Dokunmatik hareketler ve Zoom için değişkenler
    var angleX: Float = 0f
    var angleY: Float = 0f
    var zoomScale: Float = 1.0f // MyGLSurfaceView'dan güncellenecek

    // Küpleri sakladığımız liste
    private val cubes = mutableListOf<Cube>()

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
        uniform vec4 vColor; 
        void main() {
            gl_FragColor = vColor;
        }
    """.trimIndent()

    // Küp ekleme fonksiyonu
    fun addCube() {
        cubes.add(Cube())
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Arka plan: Koyu Mavi
        GLES20.glClearColor(0.1f, 0.2f, 0.4f, 1.0f) 
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        try {
            val vertexShader = ShaderUtils.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
            val fragmentShader = ShaderUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

            program = GLES20.glCreateProgram().also {
                GLES20.glAttachShader(it, vertexShader)
                GLES20.glAttachShader(it, fragmentShader)
                GLES20.glLinkProgram(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 256mm Izgara Oluşturma (128 birim sol, 128 birim sağ = 256mm)
        val gridData = createGrid(128, 1.0f)
        gridVertexCount = gridData.size / 3
        gridBuffer = ByteBuffer.allocateDirect(gridData.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(gridData)
                position(0)
            }
        }

        // Uygulama açıldığında ilk küpü otomatik ekle
        addCube()
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        if (program == 0 || !::gridBuffer.isInitialized) return

        // 1. Kamerayı ayarla (zoomScale ile mesafeyi dinamik yapıyoruz)
        Matrix.setLookAtM(viewMatrix, 0, 30f * zoomScale, 40f * zoomScale, 60f * zoomScale, 0f, 0f, 0f, 0f, 1f, 0f)

        // 2. Rotasyonları hesapla
        Matrix.setRotateM(rotationMatrix, 0, angleX, 0f, 1f, 0f)
        Matrix.rotateM(rotationMatrix, 0, angleY, 1f, 0f, 0f)

        // 3. Matrisleri birleştir
        val tempMatrix = FloatArray(16)
        Matrix.multiplyMM(tempMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(vPMatrix, 0, tempMatrix, 0, rotationMatrix, 0)

        GLES20.glUseProgram(program)
        
        // vColor uniform lokasyonunu al
        val colorHandle = GLES20.glGetUniformLocation(program, "vColor")

        // 4. Izgarayı Çiz (Açık Gri)
        GLES20.glUniform4fv(colorHandle, 1, floatArrayOf(0.8f, 0.8f, 0.8f, 1.0f), 0)
        val matrixHandle = GLES20.glGetUniformLocation(program, "uVPMatrix")
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, vPMatrix, 0)

        val posHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(posHandle)
        GLES20.glVertexAttribPointer(posHandle, 3, GLES20.GL_FLOAT, false, 0, gridBuffer)
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, gridVertexCount)

        // 5. Küpleri Çiz (Cube.kt içinde kendi renklerini/pozisyonlarını ayarlayacaklar)
        for (cube in cubes) {
            cube.draw(vPMatrix, program)
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height.toFloat()
        // Uzaklık sınırını 1000f yaparak 256mm'lik alanın kesilmesini önledik
        Matrix.perspectiveM(projectionMatrix, 0, 45f, ratio, 0.1f, 1000f)
    }

    private fun createGrid(size: Int, step: Float): FloatArray {
        val v = mutableListOf<Float>()
        for (i in -size..size) {
            val p = i * step
            v.addAll(listOf(p, 0f, -size.toFloat(), p, 0f, size.toFloat()))
            v.addAll(listOf(-size.toFloat(), 0f, p, size.toFloat(), 0f, p))
        }
        return v.toFloatArray()
    }
}
