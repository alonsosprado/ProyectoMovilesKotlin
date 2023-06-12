package com.example.proyecto2

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    //Vistas
    var et_nombre: EditText? = null
    var tv_bestScore: TextView? = null
    var iv_personaje: ImageView? = null
    var btn_jugar: Button? = null

    //Sonido
    var mp: MediaPlayer? = null
    var imageId = 0
    @SuppressLint("DiscouragedApi", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Para el toolbar
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setIcon(R.mipmap.ic_launcher)

        //Vistas
        et_nombre = findViewById(R.id.nombre)
        tv_bestScore = findViewById(R.id.bestScore)
        iv_personaje = findViewById(R.id.personaje)
        btn_jugar = findViewById(R.id.jugar)
        mp = MediaPlayer.create(this, R.raw.alphabet_song)
        mp.start()
        mp.setLooping(true)
        val admin = AdminSQLiteOpenHelper(this, "db", null, 1)
        val db = admin.writableDatabase
        @SuppressLint("Recycle") val consulta = db.rawQuery(
            "select * from puntaje where score = (select max(score) from puntaje)",
            null
        )
        if (consulta.moveToFirst()) {
            val temp_nombre = consulta.getString(0)
            val temp_score = consulta.getString(1)
            tv_bestScore.setText("Record:" + temp_score + "de " + temp_nombre)
            db.close()
        }
    }

    fun jugar(view: View?) {
        val nombre = et_nombre!!.text.toString()
        if (nombre != "") {
            mp!!.stop()
            mp!!.release()
            val intent = Intent(this, Nivel1::class.java)
            intent.putExtra("jugador", nombre)
            intent.putExtra("selectedImageId", imageId)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Debe escribir su nombre", Toast.LENGTH_SHORT).show()
            et_nombre!!.requestFocus()
            val imm = getSystemService(this.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(et_nombre, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onBackPressed() {}
    private val imageIds = intArrayOf(
        R.drawable.mango,
        R.drawable.fresa,
        R.drawable.sandia,
        R.drawable.naranja,
        R.drawable.manzana,
        R.drawable.uva
    )
    private var currentImageIndex = 0
    fun onCharacterImageClicked(view: View) {
        imageId = imageIds[currentImageIndex]
        (view as ImageView).setImageResource(imageId)
        currentImageIndex++
        if (currentImageIndex >= imageIds.size) {
            currentImageIndex = 0
        }
    }
}