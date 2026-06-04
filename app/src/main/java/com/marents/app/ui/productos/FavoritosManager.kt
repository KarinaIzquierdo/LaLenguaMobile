package com.marents.app.ui.productos

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object FavoritosManager {
    private const val PREFS_NAME = "marents_favoritos"
    private const val KEY_FAVORITOS = "lista_favoritos"
    private val gson = Gson()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getFavoritos(context: Context): List<ProductoUI> {
        val json = getPrefs(context).getString(KEY_FAVORITOS, null) ?: return emptyList()
        val type = object : TypeToken<List<ProductoUI>>() {}.type
        return gson.fromJson(json, type)
    }

    fun toggleFavorito(context: Context, producto: ProductoUI): Boolean {
        val favoritos = getFavoritos(context).toMutableList()
        val existe = favoritos.any { it.id == producto.id }
        
        if (existe) {
            favoritos.removeAll { it.id == producto.id }
        } else {
            favoritos.add(producto.copy(esFavorito = true))
        }

        val json = gson.toJson(favoritos)
        getPrefs(context).edit().putString(KEY_FAVORITOS, json).apply()
        
        return !existe // Retorna true si ahora es favorito
    }

    fun esFavorito(context: Context, productoId: Int): Boolean {
        return getFavoritos(context).any { it.id == productoId }
    }
}
