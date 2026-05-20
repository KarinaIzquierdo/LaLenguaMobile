    package com.marents.app

import retrofit2.Call
import retrofit2.http.*

import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ApiService {

    @Multipart
    @POST("productos")
    fun crearProductoFinal(
        @Part("modelo_nombre") nombre: RequestBody,
        @Part("categoria_nombre") categoriaNombre: RequestBody,
        @Part("color_primario") color: RequestBody,
        @Part("talla_numero") talla: RequestBody,
        @Part("precio") precio: RequestBody,
        @Part("costo") costo: RequestBody,
        @Part("stock") stock: RequestBody,
        @Part imagen: MultipartBody.Part?
    ): Call<Void>

    @Multipart
    @POST("productos")
    fun crearProductoConImagen(
        @Part("modelo_nombre") nombre: RequestBody,
        @Part("categoria_id") categoriaId: RequestBody,
        @Part("color_primario") color: RequestBody,
        @Part("talla_numero") talla: RequestBody,
        @Part("precio") precio: RequestBody,
        @Part("costo") costo: RequestBody,
        @Part("stock") stock: RequestBody,
        @Part imagen: MultipartBody.Part?
    ): Call<Void>

    @FormUrlEncoded
    @POST("productos")
    fun crearProductoSimple(@FieldMap params: Map<String, String>): Call<Producto>

    @GET("productos")
    fun getProductos(): Call<List<Producto>>

    @GET("productos/categoria/{categoriaNombre}")
    fun getProductosPorCategoria(@Path("categoriaNombre") categoriaNombre: String): Call<List<Producto>>

    @GET("productos/{id}")
    fun getProducto(@Path("id") id: Int): Call<Producto>

    @FormUrlEncoded
    @POST("productos")
    fun crearProducto(
        @Field("modelo_nombre") nombre: String,
        @Field("categoria_id") categoriaId: Int,
        @Field("color_primario") color: String,
        @Field("talla_numero") talla: Double,
        @Field("precio") precio: Double,
        @Field("costo") costo: Double,
        @Field("stock") stock: Int
    ): Call<Void>

    @PUT("productos/{id}")
    fun actualizarProducto(@Path("id") id: Int, @FieldMap params: Map<String, String>): Call<Map<String, Any>>

    @DELETE("productos/{id}")
    fun eliminarProducto(@Path("id") id: Int): Call<Void>

    @GET("categorias")
    fun getCategorias(): Call<List<Categoria>>

    @GET("modelos")
    fun getModelos(): Call<List<Modelo>>

    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @GET("users")
    fun getUsuarios(): Call<List<User>>

    @FormUrlEncoded
    @PUT("users/{id}")
    fun actualizarUsuario(
        @Path("id") id: Int,
        @Field("nombres") nombres: String,
        @Field("apellidos") apellidos: String,
        @Field("email") email: String,
        @Field("documento") documento: String?,
        @Field("celular") celular: String?,
        @Field("rol") rol: String,
        @Field("password") password: String?
    ): Call<Map<String, Any>>

    @DELETE("users/{id}")
    fun eliminarUsuario(@Path("id") id: Int): Call<Void>

    @GET("cart")
    fun getCart(@Query("user_id") userId: Int): Call<List<CartItem>>

    @POST("cart/add")
    fun addToCart(@Body request: AddToCartRequest): Call<CartResponse>

    @DELETE("cart/item/{itemId}")
    fun removeFromCart(@Path("itemId") itemId: Int): Call<CartResponse>

    @GET("admin/stats")
    fun getAdminStats(): Call<AdminStatsResponse>
}

data class AdminStatsResponse(
    val pendientes: Int,
    val ventas: Int,
    val stock_bajo: Int,
    val completados: Int,
    val pedidos_recientes: List<PedidoResumenResponse>? = null
)

data class PedidoResumenResponse(
    val id: Int,
    val cliente: String,
    val total: Double,
    val estado: String
)