package com.marents.app

import com.google.gson.annotations.SerializedName

data class Producto(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName(value = "modelo", alternate = ["model", "name"])
    val modelo: Modelo? = null,

    @SerializedName(value = "imagen", alternate = ["image", "url"])
    val imagen: String? = null,

    @SerializedName(value = "estado", alternate = ["status"])
    val estado: String? = null,

    @SerializedName(value = "variaciones", alternate = ["variations"])
    val variaciones: List<Variacion>? = emptyList()
)

data class Modelo(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName(value = "nombre", alternate = ["name"])
    val nombre: String? = null,

    @SerializedName(value = "categoria_id", alternate = ["category_id"])
    val categoriaId: Int? = null,

    @SerializedName("categoria")
    val categoria: Categoria? = null
)

data class Categoria(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName(value = "nombre", alternate = ["name"])
    val nombre: String? = null
)

data class Variacion(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName(value = "producto_id", alternate = ["product_id"])
    val productoId: Int? = null,

    @SerializedName(value = "talla_id", alternate = ["size_id"])
    val tallaId: Int? = null,

    @SerializedName(value = "color_id", alternate = ["colorId"])
    val colorId: Int? = null,

    // Precio y costo como String para mantener la forma exacta recibida
    @SerializedName(value = "precio", alternate = ["price"])
    val precio: String? = null,

    @SerializedName(value = "costo", alternate = ["cost"])
    val costo: String? = null,

    @SerializedName("stock")
    val stock: Int? = null,

    // 0/1 ó boolean dependiendo de API
    @SerializedName(value = "tiene_descuento", alternate = ["has_discount"])
    val tieneDescuento: Int? = null,

    @SerializedName(value = "valor_descuento", alternate = ["discount_value"])
    val valorDescuento: String? = null,

    @SerializedName("talla")
    val talla: Talla? = null,

    @SerializedName("color_primario", alternate = ["primary_color"])
    val colorPrimario: ColorItem? = null,

    @SerializedName("color_secundario", alternate = ["secondary_color"])
    val colorSecundario: ColorItem? = null
)

data class Talla(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName(value = "numero", alternate = ["number"])
    val numero: Int? = null
)

data class ColorItem(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName(value = "nombre", alternate = ["name"])
    val nombre: String? = null,

    @SerializedName(value = "hex")
    val hex: String? = null
)