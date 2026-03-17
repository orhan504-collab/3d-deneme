package com.example.sketch3d

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
    
    // Seçili modu takip etmek için
    private var currentMainMode: String = "NONE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Görünümleri Bağla
        myGLSurfaceView = findViewById(R.id.myGLSurfaceView)
        detailToolbar = findViewById(R.id.detailToolbar)
        btnVertexMode = findViewById(R.id.btnVertexMode)
        
        val btnEdgeMode = findViewById<ImageButton>(R.id.btnEdgeMode)
        val btnFaceMode = findViewById<ImageButton>(R.id.btnFaceMode)
        val btnObjectMode = findViewById<ImageButton>(R.id.btnObjectMode)

        // --- ANA MENÜ TIKLAMA İŞLEMLERİ ---

        // Nokta (Vertex) Modu
        btnVertexMode.setOnClickListener {
            toggleDetailMenu("VERTEX", btnVertexMode)
        }

        // Çizgi (Edge) Modu
        btnEdgeMode.setOnClickListener {
            toggleDetailMenu("EDGE", btnEdgeMode)
            Toast.makeText(this, "Çizgi Modu Aktif", Toast.LENGTH_SHORT).show()
        }

        // Yüzey (Face) Modu
        btnFaceMode.setOnClickListener {
            toggleDetailMenu("FACE", btnFaceMode)
        }

        // Obje Modu (Küp Ekleme/Seçme)
        btnObjectMode.setOnClickListener {
            detailToolbar.visibility = View.GONE
            // Yeni küp ekleme fonksiyonunu çağırabilirsin
            myGLSurfaceView.renderer.addCube(isInitial = false)
            Toast.makeText(this, "Yeni Küp Eklendi", Toast.LENGTH_SHORT).show()
        }

        // --- DETAY MENÜ İŞLEMLERİ (2. Görseldeki İşlemler) ---

        findViewById<LinearLayout>(R.id.btnMerge).setOnClickListener {
            Toast.makeText(this, "Birleştirmek için noktaları seçin", Toast.LENGTH_SHORT).show()
            // DrawingManager.startMergeAction() gibi bir tetikleyici eklenebilir
        }

        findViewById<LinearLayout>(R.id.btnTargetWeld).setOnClickListener {
            Toast.makeText(this, "Hedef noktayı seçin", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Alt menüdeki ana modlar arasında geçiş yapar ve detay menüsünü gösterir/gizler.
     */
    private fun toggleDetailMenu(mode: String, clickedButton: ImageButton) {
        if (currentMainMode == mode && detailToolbar.visibility == View.VISIBLE) {
            // Aynı moda tekrar basıldıysa kapat
            detailToolbar.visibility = View.GONE
            clickedButton.setBackgroundResource(0)
            currentMainMode = "NONE"
        } else {
            // Yeni bir mod seçildiyse aç
            detailToolbar.visibility = View.VISIBLE
            resetMainButtons() // Diğer butonların efektini temizle
            clickedButton.setBackgroundColor(android.graphics.Color.parseColor("#388E3C")) // Seçili efekti (Yeşil)
            currentMainMode = mode
            
            // Renderer'a hangi modda olduğumuzu bildir (Köşelerin parlaması için)
            // myGLSurfaceView.renderer.setSelectionMode(mode)
        }
    }

    private fun resetMainButtons() {
        btnVertexMode.setBackgroundResource(0)
        findViewById<ImageButton>(R.id.btnEdgeMode).setBackgroundResource(0)
        findViewById<ImageButton>(R.id.btnFaceMode).setBackgroundResource(0)
        findViewById<ImageButton>(R.id.btnObjectMode).setBackgroundResource(0)
    }

    // Uygulama duraklatıldığında OpenGL'i de duraklat
    override fun onResume() {
        super.onResume()
        myGLSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        myGLSurfaceView.onPause()
    }
}
