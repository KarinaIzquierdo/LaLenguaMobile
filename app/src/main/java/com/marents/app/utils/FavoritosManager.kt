package com.marents.app.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.marents.app.ui.productos.ProductoUI

class FavoritosManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("favoritos_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun toggleFavorito(producto: ProductoUI): Boolean {
        val favoritos = getFavoritos().toMutableList()
        val index = favoritos.indexOfFirst { it.id == producto.id }
        
        val isAdded: Boolean
        if (index != -1) {
            favoritos.removeAt(index)
            isAdded = false
        } else {
            favoritos.add(producto.copy(esFavorito = true))
            isAdded = true
        }
        
        saveFavoritos(favoritos)
        return isAdded
    }

    fun isFavorito(productoId: Int): Boolean {
        return getFavoritos().any { it.id == productoId }
    }

    fun getFavoritos(): List<ProductoUI> {
        val json = prefs.getString("lista_favoritos", null) ?: return emptyList()
        val type = object : TypeToken<List<ProductoUI>>() {}.type
        return gson.fromJson(json, type)
    }

    private fun saveFavoritos(lista: List<ProductoUI>) {
        val json = gson.toJson(lista)
        prefs.edit().putString("lista_favoritos", json).apply()
    }
}
