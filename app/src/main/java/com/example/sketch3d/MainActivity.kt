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
    
    // Aktif olan ana modu takip etmek için ("NONE", "VERTEX", "EDGE", "FACE")
    private var currentMainMode: String = "NONE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // XML'deki bileşenleri bağla
        myGLSurfaceView = findViewById(R.id.myGLSurfaceView)
        detailToolbar = findViewById(R.id.detailToolbar)
        btnVertexMode = findViewById(R.id.btnVertexMode)
        
        val btnEdgeMode = findViewById<ImageButton>(R.id.btnEdgeMode)
        val btnFaceMode = findViewById<ImageButton>(R.id.btnFaceMode)
        val btnObjectMode = findViewById<ImageButton>(R.id.btnObjectMode)

        // --- ANA MENÜ TIKLAMA OLAYLARI ---

        // Nokta (Vertex) Modu: Tıklanınca detay menüsünü açar/kapatır
        btnVertexMode.setOnClickListener {
            handleMenuNavigation("VERTEX", btnVertexMode)
        }

        // Çizgi (Edge) Modu
        btnEdgeMode.setOnClickListener {
            handleMenuNavigation("EDGE", btnEdgeMode)
        }

        // Yüzey (Face) Modu
        btnFaceMode.setOnClickListener {
            handleMenuNavigation("FACE", btnFaceMode)
        }

        // Obje Modu: Sahneye yeni bir küp ekler
        btnObjectMode.setOnClickListener {
            detailToolbar.visibility = View.GONE
            resetButtonBackgrounds()
            // Renderer üzerinden yeni küp ekleme işlemini tetikle
            myGLSurfaceView.renderer.addCube()
            Toast.makeText(this, "Yeni nesne eklendi", Toast.LENGTH_SHORT).show()
        }

        // --- DETAY MENÜ (ÜST BAR) İŞLEMLERİ ---

        // Birleştir (Merge)
        findViewById<LinearLayout>(R.id.btnMerge)?.setOnClickListener {
            Toast.makeText(this, "Birleştirme modu aktif: İki nokta seçin", Toast.LENGTH_SHORT).show()
        }

        // Hedef Birleştirme (Target Weld)
        findViewById<LinearLayout>(R.id.btnTargetWeld)?.setOnClickListener {
            Toast.makeText(this, "Hedef birleştirme: Kaynak ve hedef seçin", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Menü geçişlerini yönetir ve seçili olan butonu vurgular.
     */
    private fun handleMenuNavigation(mode: String, clickedButton: ImageButton) {
        if (currentMainMode == mode && detailToolbar.visibility == View.VISIBLE) {
            // Eğer zaten bu moddaysak menüyü kapat
            detailToolbar.visibility = View.GONE
            clickedButton.setBackgroundColor(Color.TRANSPARENT)
            currentMainMode = "NONE"
        } else {
            // Yeni bir moda geçiş yap
            detailToolbar.visibility = View.VISIBLE
            resetButtonBackgrounds()
            
            // Seçili butonu yeşil yap (Görseldeki gibi)
            clickedButton.setBackgroundColor(Color.parseColor("#388E3C")) 
            currentMainMode = mode
            
            // Burada DrawingManager üzerinden seçim modunu değiştirebilirsin
        }
    }

    /**
     * Tüm ana menü butonlarının arka plan rengini temizler.
     */
    private fun resetButtonBackgrounds() {
        btnVertexMode.setBackgroundColor(Color.TRANSPARENT)
        findViewById<ImageButton>(R.id.btnEdgeMode).setBackgroundColor(Color.TRANSPARENT)
        findViewById<ImageButton>(R.id.btnFaceMode).setBackgroundColor(Color.TRANSPARENT)
        findViewById<ImageButton>(R.id.btnObjectMode).setBackgroundColor(Color.TRANSPARENT)
    }

    // Yaşam döngüsü yönetimi
    override fun onResume() {
        super.onResume()
        myGLSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        myGLSurfaceView.onPause()
    }
}
