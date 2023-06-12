package com.example.proyecto2

import android.content.ContentValues
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Nivel1 : AppCompatActivity() {
    var tv_nombre: TextView? = null
    var tv_score: TextView? = null
    var iv_Auno: ImageView? = null
    var iv_Ados: ImageView? = null
    var iv_Vidas: ImageView? = null
    var iv_Personaje: ImageView? = null
    var et_Respuesta: EditText? = null
    var mp: MediaPlayer? = null
    var mpGreat: MediaPlayer? = null
    var mpBad: MediaPlayer? = null
    var score = 0
    var numAleatorio_uno = 0
    var numAleatorio_dos = 0
    var resultado = 0
    var vidas = 3
    var nombre_jugador: String? = null
    var string_score: String? = null
    var string_vidas: String? = null
    var numero =
        arrayOf("cero", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nivel1)
        Toast.makeText(this, "Nivel 1- Sumas basicas", Toast.LENGTH_SHORT).show()
        tv_nombre = findViewById(R.id.textView_Nombre)
        tv_score = findViewById(R.id.textView_Score)
        iv_Vidas = findViewById(R.id.imageView_Manzanas)
        iv_Auno = findViewById(R.id.imageView_NumeroUno)
        iv_Ados = findViewById(R.id.imageView_NumeroDos)
        iv_Personaje = findViewById(R.id.imageView2)
        et_Respuesta = findViewById(R.id.et_Resultado)
        nombre_jugador = intent.getStringExtra("jugador")
        tv_nombre.setText("Jugador: $nombre_jugador")
        val selectedImageId = intent.getIntExtra("selectedImageId", -1)
        if (selectedImageId != -1) {
            iv_Personaje.setImageResource(selectedImageId)
        }
        setSupportActionBar(findViewById(R.id.myToolbar2))
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setIcon(R.mipmap.ic_launcher)
        mp = MediaPlayer.create(this, R.raw.alphabet_song)
        mp.start()
        mp.setLooping(true)
        mpGreat = MediaPlayer.create(this, R.raw.wonderful)
        mpBad = MediaPlayer.create(this, R.raw.bad)
        numeroAleatorio()
    }

    fun comparar(vista: View?) {
        val respuesta = et_Respuesta!!.text.toString()
        if (respuesta != "") {
            val respuestaJugador = respuesta.toInt()
            if (resultado == respuestaJugador) {
                mpGreat!!.start()
                score++
                tv_score!!.text = "Score:$score"
            } else {
                mpBad!!.start()
                vidas--
                when (vidas) {
                    3 -> iv_Vidas!!.setImageResource(R.drawable.tresvidas)
                    2 -> {
                        iv_Vidas!!.setImageResource(R.drawable.dosvidas)
                        Toast.makeText(this, "Quedan 2 manzanas", Toast.LENGTH_SHORT).show()
                    }

                    1 -> {
                        iv_Vidas!!.setImageResource(R.drawable.unavida)
                        Toast.makeText(this, "Queda 1 manzana", Toast.LENGTH_SHORT).show()
                    }

                    0 -> {
                        Toast.makeText(this, "Has perdido todas tus manzanas", Toast.LENGTH_SHORT)
                            .show()
                        mp!!.stop()
                        mp!!.release()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
            baseDeDatos()
            et_Respuesta!!.setText("")
            numeroAleatorio()
        } else {
            Toast.makeText(this, "Debes dar una respuesta", Toast.LENGTH_SHORT).show()
        }
    }

    private fun numeroAleatorio() {
        if (score <= 9) {
            numAleatorio_uno = (Math.random() * 10).toInt()
            numAleatorio_dos = (Math.random() * 10).toInt()
            resultado = numAleatorio_uno + numAleatorio_dos
            if (resultado <= 10) {
                for (i in numero.indices) {
                    val id = resources.getIdentifier(numero[i], "drawable", packageName)
                    if (numAleatorio_uno == i) {
                        iv_Auno!!.setImageResource(id)
                    }
                    if (numAleatorio_dos == i) {
                        iv_Ados!!.setImageResource(id)
                    }
                }
            } else {
                numeroAleatorio()
            }
        } else {
            val intent = Intent(this, MainActivity2_Nivel2::class.java)
            string_score = score.toString()
            string_vidas = vidas.toString()
            intent.putExtra("jugador", nombre_jugador)
            intent.putExtra("score", string_score)
            intent.putExtra("vidas", string_vidas)
            mp!!.stop()
            mp!!.release()
            startActivity(intent)
            finish()
        }
    }

    fun baseDeDatos() {
        val admin = AdminSQLiteOpenHelper(this, "db", null, 1)
        val db = admin.writableDatabase
        val consulta = db.rawQuery(
            "select * from puntaje where score = (select max(score) from puntaje)", null
        )
        if (consulta.moveToFirst()) {
            val temp_nombre = consulta.getString(0)
            val temp_score = consulta.getString(1)
            val bestScore = temp_score.toInt()
            if (score > bestScore) {
                val modificacion = ContentValues()
                modificacion.put("nombre", nombre_jugador)
                modificacion.put("score", score)
                db.update("puntaje", modificacion, "score=$bestScore", null)
            } else {
                val insertar = ContentValues()
                insertar.put("nombre", nombre_jugador)
                insertar.put("score", score)
                db.insert("puntaje", null, insertar)
            }
            db.close()
        }
    }
}