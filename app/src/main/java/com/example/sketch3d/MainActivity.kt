package com.example.sketch3d

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var myGLSurfaceView: MyGLSurfaceView
    private lateinit var detailToolbar: HorizontalScrollView
    private lateinit var btnVertexMode: ImageButton
    private var currentMainMode: String = "NONE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myGLSurfaceView = findViewById(R.id.myGLSurfaceView)
        detailToolbar = findViewById(R.id.detailToolbar)
        btnVertexMode = findViewById(R.id.btnVertexMode)
        
        val btnEdgeMode = findViewById<ImageButton>(R.id.btnEdgeMode)
        val btnFaceMode = findViewById<ImageButton>(R.id.btnFaceMode)
        val btnObjectMode = findViewById<ImageButton>(R.id.btnObjectMode)

        btnVertexMode.setOnClickListener { toggleMenu("VERTEX", btnVertexMode) }
        btnEdgeMode.setOnClickListener { toggleMenu("EDGE", btnEdgeMode) }
        btnFaceMode.setOnClickListener { toggleMenu("FACE", btnFaceMode) }
        
        btnObjectMode.setOnClickListener {
            detailToolbar.visibility = View.GONE
            resetButtons()
            Toast.makeText(this, "Obje Modu", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleMenu(mode: String, button: ImageButton) {
        if (currentMainMode == mode && detailToolbar.visibility == View.VISIBLE) {
            detailToolbar.visibility = View.GONE
            button.setBackgroundColor(Color.TRANSPARENT)
            currentMainMode = "NONE"
        } else {
            detailToolbar.visibility = View.VISIBLE
            resetButtons()
            button.setBackgroundColor(Color.parseColor("#388E3C"))
            currentMainMode = mode
        }
    }

    private fun resetButtons() {
        btnVertexMode.setBackgroundColor(Color.TRANSPARENT)
        findViewById<ImageButton>(R.id.btnEdgeMode).setBackgroundColor(Color.TRANSPARENT)
        findViewById<ImageButton>(R.id.btnFaceMode).setBackgroundColor(Color.TRANSPARENT)
        findViewById<ImageButton>(R.id.btnObjectMode).setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onResume() { super.onResume(); myGLSurfaceView.onResume() }
    override fun onPause() { super.onPause(); myGLSurfaceView.onPause() }
}
