package com.marents.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.marents.app.databinding.ActivityMainBinding
import com.marents.app.ui.admin.AdminFragment
import com.marents.app.ui.admin.UsersFragment
import com.marents.app.ui.categorias.CategoriasFragment
import com.marents.app.ui.productos.ProductosAdminFragment
import com.marents.app.ui.home.HomeFragment
import com.marents.app.ui.login.LoginFragment
import com.marents.app.ui.register.RegisterFragment
import com.marents.app.ui.welcome.WelcomeFragment

class MainActivity : AppCompatActivity(), Navigator.Provider {

    private lateinit var binding: ActivityMainBinding
    private val navigator = Navigator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mostrar WelcomeFragment al inicio
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, WelcomeFragment())
                .commit()
        }
    }

    fun navigateToFragment(route: String) {
        when (route) {
            AppRoutes.HOME -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment())
                    .addToBackStack(null)
                    .commit()
            }
            AppRoutes.LOGIN -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, LoginFragment())
                    .addToBackStack(null)
                    .commit()
            }
            AppRoutes.REGISTER -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, RegisterFragment())
                    .addToBackStack(null)
                    .commit()
            }
            AppRoutes.CATEGORIES -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, CategoriasFragment())
                    .addToBackStack(null)
                    .commit()
            }
            AppRoutes.ADMIN -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AdminFragment())
                    .addToBackStack(null)
                    .commit()
            }
            AppRoutes.USERS -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, UsersFragment())
                    .addToBackStack(null)
                    .commit()
            }
            AppRoutes.PRODUCTOS -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ProductosAdminFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun getNavigator(): Navigator = navigator
}
