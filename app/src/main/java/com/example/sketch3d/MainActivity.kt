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
    private lateinit var btnEdgeMode: ImageButton
    private lateinit var btnFaceMode: ImageButton
    private lateinit var btnObjectMode: ImageButton
    
    // Uygulamanın o anki seçim modunu takip eder
    private var currentMainMode: String = "NONE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Görünümleri XML ID'leri ile bağla
        myGLSurfaceView = findViewById(R.id.myGLSurfaceView)
        detailToolbar = findViewById(R.id.detailToolbar)
        btnVertexMode = findViewById(R.id.btnVertexMode)
        btnEdgeMode = findViewById(R.id.btnEdgeMode)
        btnFaceMode = findViewById(R.id.btnFaceMode)
        btnObjectMode = findViewById(R.id.btnObjectMode)

        // --- ANA MENÜ BUTONLARI ---

        btnVertexMode.setOnClickListener {
            toggleMenu("VERTEX", btnVertexMode)
        }

        btnEdgeMode.setOnClickListener {
            toggleMenu("EDGE", btnEdgeMode)
        }

        btnFaceMode.setOnClickListener {
            toggleMenu("FACE", btnFaceMode)
        }

        btnObjectMode.setOnClickListener {
            // Obje modunda detay barını kapat ve seçimleri temizle
            detailToolbar.visibility = View.GONE
            resetButtons()
            currentMainMode = "OBJECT"
            // Renderer'a yeni küp ekleme komutu gönderilebilir
            Toast.makeText(this, "Obje Modu: Nesne Seçimi/Ekleme", Toast.LENGTH_SHORT).show()
        }

        // --- DETAY MENÜ (ÜST BAR) BUTONLARI ---

        findViewById<LinearLayout>(R.id.btnMerge)?.setOnClickListener {
            Toast.makeText(this, "Birleştirme işlemi seçildi", Toast.LENGTH_SHORT).show()
        }

        findViewById<LinearLayout>(R.id.btnTargetWeld)?.setOnClickListener {
            Toast.makeText(this, "Hedef birleştirme seçildi", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Menü geçişlerini yönetir ve seçili butonu vurgular.
     */
    private fun toggleMenu(mode: String, button: ImageButton) {
        if (currentMainMode == mode && detailToolbar.visibility == View.VISIBLE) {
            // Aynı butona basıldıysa menüyü kapat
            detailToolbar.visibility = View.GONE
            button.setBackgroundColor(Color.TRANSPARENT)
            currentMainMode = "NONE"
        } else {
            // Yeni bir mod seçildiyse menüyü aç ve vurgula
            detailToolbar.visibility = View.VISIBLE
            resetButtons()
            button.setBackgroundColor(Color.parseColor("#388E3C")) // Görseldeki yeşil tonu
            currentMainMode = mode
            
            // Renderer'a seçim modunu bildir (Noktaların/Çizgilerin görünürlüğü için)
            // myGLSurfaceView.renderer.updateSelectionMode(mode)
        }
    }

    /**
     * Tüm ana menü butonlarının görsel vurgusunu temizler.
     */
    private fun resetButtons() {
        btnVertexMode.setBackgroundColor(Color.TRANSPARENT)
        btnEdgeMode.setBackgroundColor(Color.TRANSPARENT)
        btnFaceMode.setBackgroundColor(Color.TRANSPARENT)
        btnObjectMode.setBackgroundColor(Color.TRANSPARENT)
    }

    // Uygulama yaşam döngüsü yönetimi
    override fun onResume() {
        super.onResume()
        myGLSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        myGLSurfaceView.onPause()
    }
}
