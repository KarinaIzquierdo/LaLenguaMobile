package com.marents.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.marents.app.databinding.ActivityMainMenuBinding

class MainMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar el controlador de navegación
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        val navController = navHostFragment.navController

        // Vincular el BottomNavigationView con el NavController
        binding.bottomNavigationMain.setupWithNavController(navController)

        // Verificar si venimos desde una navegación de categoría (ej: tras registro)
        intent.getStringExtra("categoriaNombre")?.let { categoria ->
            val bundle = Bundle().apply { putString("categoriaNombre", categoria) }
            navController.navigate(R.id.productosCategoriaFragment, bundle)
        }

        // Forzar navegación manual para asegurar que funcione siempre
        binding.bottomNavigationMain.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_new_inicio -> {
                    navController.navigate(R.id.nav_new_inicio)
                    true
                }
                R.id.productosCategoriaFragment -> {
                    val bundle = Bundle().apply { putString("categoriaNombre", "Productos") }
                    navController.navigate(R.id.productosCategoriaFragment, bundle)
                    true
                }
                R.id.nav_new_favoritos -> {
                    navController.navigate(R.id.nav_new_favoritos)
                    true
                }
                R.id.nav_new_carrito -> {
                    navController.navigate(R.id.nav_new_carrito)
                    true
                }
                R.id.nav_new_perfil -> {
                    navController.navigate(R.id.nav_new_perfil)
                    true
                }
                else -> false
            }
        }
    }
}
