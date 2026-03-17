package com.example.sketch3d

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyRenderer(private val context: Context) : GLSurfaceView.Renderer {

    // View tarafından kontrol edilen değişkenler
    var angleX: Float = 0f
    var angleY: Float = 0f
    var zoomScale: Float = 1.0f 

    // Küp listesi (Internal: View içinden erişim için)
    internal val cubes = mutableListOf<Cube>()

    private var program: Int = 0
    private var gridBuffer: FloatBuffer? = null
    private var gridVertexCount = 0

    // Matris Tanımlamaları
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

    /**
     * Sahneye yeni bir küp ekler.
     * isInitial: Uygulama ilk açıldığında eklenen referans küpü mü?
     */
    fun addCube(isInitial: Boolean = false) {
        val cube = Cube()
        // İlk küp referans amaçlı şeffaf, sonrakiler opak/mavi olabilir
        cube.isTransparent = isInitial
        cubes.add(cube)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Arka plan rengini belirle (Koyu Mavi/Gri tonu)
        GLES20.glClearColor(0.1f, 0.15f, 0.2f, 1.0f) 
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        
        // Şeffaflık (Blending) ayarları
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        // Shader programını oluştur
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        // Izgara (Grid) oluşturma: 256mm genişlik, 10mm aralık
        setupGrid()

        // İlk açılışta sahneye bir küp yerleştir
        if (cubes.isEmpty()) {
            addCube(isInitial = true)
        }
    }

    private fun setupGrid() {
        val gridData = createGrid(128, 10.0f) 
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
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        
        if (program == 0 || gridBuffer == null) return

        // Kamera Pozisyonu (Zoom ile etkileşimli)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 200f * zoomScale, 400f * zoomScale, 0f, 0f, 0f, 0f, 1f, 0f)
        
        // Rotasyon Matrisini Oluştur
        Matrix.setRotateM(rotationMatrix, 0, angleX, 0f, 1f, 0f)
        Matrix.rotateM(rotationMatrix, 0, angleY, 1f, 0f, 0f)

        // Matris Çarpmaları: Projection * View * Rotation
        val tempMatrix = FloatArray(16)
        Matrix.multiplyMM(tempMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(vPMatrix, 0, tempMatrix, 0, rotationMatrix, 0)

        GLES20.glUseProgram(program)
        
        val colorHandle = GLES20.glGetUniformLocation(program, "vColor")
        val matrixHandle = GLES20.glGetUniformLocation(program, "uVPMatrix")
        val posHandle = GLES20.glGetAttribLocation(program, "vPosition")

        // 1. Izgarayı (Zemini) Çiz
        GLES20.glUniform4fv(colorHandle, 1, floatArrayOf(0.5f, 0.5f, 0.5f, 0.3f), 0)
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, vPMatrix, 0)
        GLES20.glEnableVertexAttribArray(posHandle)
        GLES20.glVertexAttribPointer(posHandle, 3, GLES20.GL_FLOAT, false, 0, gridBuffer)
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, gridVertexCount)

        // 2. Listedeki Tüm Küpleri Çiz
        for (cube in cubes) {
            cube.draw(vPMatrix, program)
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height.toFloat()
        // Titremeyi önlemek için Near Plane 1.0f olarak ayarlandı
        Matrix.perspectiveM(projectionMatrix, 0, 45f, ratio, 1.0f, 5000f)
    }

    private fun createGrid(size: Int, step: Float): FloatArray {
        val v = mutableListOf<Float>()
        for (i in -size..size step step.toInt()) {
            val p = i.toFloat()
            // X çizgileri
            v.addAll(listOf(p, 0f, -size.toFloat(), p, 0f, size.toFloat()))
            // Z çizgileri
            v.addAll(listOf(-size.toFloat(), 0f, p, size.toFloat(), 0f, p))
        }
        return v.toFloatArray()
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }
}
