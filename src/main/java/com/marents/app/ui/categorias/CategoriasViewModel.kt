package com.marents.app.ui.categorias

import androidx.lifecycle.ViewModel
import com.marents.app.CategoriaItem
import com.marents.app.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel para la pantalla de selección de categorías
 * Maneja la lista de categorías y la selección actual
 */
class CategoriasViewModel : ViewModel() {

    // Lista de categorías (datos estáticos por ahora)
    private val _categorias = MutableStateFlow<List<CategoriaItem>>(emptyList())
    val categorias: StateFlow<List<CategoriaItem>> = _categorias

    // Categoría seleccionada
    private val _categoriaSeleccionada = MutableStateFlow<CategoriaItem?>(null)
    val categoriaSeleccionada: StateFlow<CategoriaItem?> = _categoriaSeleccionada

    init {
        cargarCategorias()
    }

    /**
     * Carga las 4 categorías predefinidas
     * Usa iconos por defecto del sistema (temporalmente)
     */
    private fun cargarCategorias() {
        val listaCategorias = listOf(
            CategoriaItem(
                id = 1,
                nombre = "Dama",
                iconoResId = R.mipmap.ic_launcher, // TODO: Reemplazar con icono real
                colorFondo = "#E8F5E9" // Verde muy claro
            ),
            CategoriaItem(
                id = 2,
                nombre = "Caballero",
                iconoResId = R.mipmap.ic_launcher,
                colorFondo = "#E3F2FD" // Azul muy claro
            ),
            CategoriaItem(
                id = 3,
                nombre = "Niña",
                iconoResId = R.mipmap.ic_launcher,
                colorFondo = "#FCE4EC" // Rosa muy claro
            ),
            CategoriaItem(
                id = 4,
                nombre = "Niño",
                iconoResId = R.mipmap.ic_launcher,
                colorFondo = "#FFF3E0" // Naranja muy claro
            )
        )
        _categorias.value = listaCategorias
    }

    /**
     * Selecciona una categoría
     */
    fun seleccionarCategoria(categoria: CategoriaItem) {
        _categoriaSeleccionada.value = categoria
    }

    /**
     * Limpia la selección (usar al navegar)
     */
    fun limpiarSeleccion() {
        _categoriaSeleccionada.value = null
    }
}
