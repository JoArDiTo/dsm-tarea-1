package com.fisi.tarea1
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fisi.tarea1.config.SQLiteHelper

class RegisterActivity : AppCompatActivity() {

    private lateinit var database: SQLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        database = SQLiteHelper(this)

        val usernameEditText = findViewById<EditText>(R.id.usernameEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val createUserButton = findViewById<Button>(R.id.createUserButton)
        val loginRedirectButton = findViewById<Button>(R.id.loginRedirectButton)

        createUserButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (database.userExists(email)) {
                Toast.makeText(this, "Usuario ya registrado", Toast.LENGTH_SHORT).show()
            } else {
                if (database.insertUser(username, email, password)) {
                    Toast.makeText(this, "Usuario registrado", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Error en el registro", Toast.LENGTH_SHORT).show()
                }
            }
        }

        loginRedirectButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
