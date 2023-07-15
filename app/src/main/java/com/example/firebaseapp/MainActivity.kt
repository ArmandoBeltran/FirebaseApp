package com.example.firebaseapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseDatabase

    lateinit var tv_txt : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        database = FirebaseDatabase.getInstance()

        tv_txt = findViewById(R.id.tv_txt)

        val email = "beltran.armando2210@gmail.com"
        val password = "123456789"

        anoynmousLogin()
        setSeasons("8")
        getSeasons()
    }

    override fun onStart(){
        super.onStart()

        val currentUser : FirebaseUser? = auth.currentUser
        if (currentUser != null){
            if (currentUser.email != ""){
                tv_txt.text = "Bienvenido ${currentUser.email}"
            } else {
                tv_txt.text = "Bienvenido anonimo"
            }
        } else {
            login("beltran.armando2210@gmail.com", "123456789")
        }
    }

    fun login(email : String, password : String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                    task ->
                if (task.isSuccessful){
                    val user = auth.currentUser
                    //tv_txt.text = "Exitoso"
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_LONG).show()
                }else{
                    //tv_txt.text = "Error"
                    Toast.makeText(this, "Error en el inicio de sesión: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun anoynmousLogin(){
        auth.signInAnonymously()
            .addOnCompleteListener(this) {
                task ->
                if (task.isSuccessful){
                    val user = auth.currentUser
                    tv_txt.text = "INICIO DE SESION ANONIMO"
                    Toast.makeText(this, "INICIO anónimo", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Error en el inicio de sesión anónimo", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun getSeasons(){
        val reference = database.getReference("seasons")

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (seasonSnapshot in dataSnapshot.children){
                    val season = seasonSnapshot.getValue(Season::class.java)
                    Log.d(season?.name, season?.description.toString())
                    Toast.makeText(this@MainActivity, season?.name + ": " + season?.description, Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(databaseError : DatabaseError){
                println("Error al leer los datos ${databaseError.message}")
            }
        })
    }

    fun setSeasons(seasonID : String){
        val reference = database.getReference("seasons")

        val season = Season("Sesión Prueba", "Descripción Prueba", false)

        reference.child(seasonID).setValue(season).addOnCompleteListener{
            Log.d("Complete", "Se guardó la info")
        }
    }
}

data class Season(
    val name : String = "",
    val description : String = "",
    val status : Boolean = false
)

