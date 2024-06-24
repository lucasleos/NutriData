package unpsjb.ing.tntpm2024

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)

        // Obtener el usuario actual
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Encontrar LottieAnimationView
        val lottieAnimationView = findViewById<LottieAnimationView>(R.id.lottieAnimationView)
        // Configurar el modo de escala en el código (opcional)
        lottieAnimationView.scaleType = ImageView.ScaleType.FIT_XY

        // Reproducir animación en bucle
        lottieAnimationView.playAnimation()

        // Configurar el botón para cambiar a MainActivity
        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        btnIngresar.setOnClickListener {
         /*   if (currentUser != null) {
                // Usuario autenticado, navegar a MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                // Usuario no autenticado, navegar a LoginActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }*/
            if(currentUser != null)
                FirebaseAuth.getInstance().signOut()
            
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}