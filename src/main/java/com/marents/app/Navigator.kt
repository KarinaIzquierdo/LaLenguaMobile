package com.marents.app

import android.os.Bundle
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


sealed class NavigationCommand {
    data class To(val route: String, val args: Bundle? = null) : NavigationCommand()
    data class ToAction(val actionId: Int, val args: Bundle? = null) : NavigationCommand()
    object Back : NavigationCommand()
}

object AppRoutes {
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val CATEGORIES = "categories"
    const val HOME = "home"
    const val ADMIN = "admin"
    const val USERS = "users"
    const val PRODUCTOS = "productos"
    const val DETALLE_PRODUCTO = "detalle_producto/{productoId}"
}

class Navigator {
    private var _command: NavigationCommand? = null
    
    fun getCommand(): NavigationCommand? {
        val cmd = _command
        _command = null
        return cmd
    }
    
    fun navigate(command: NavigationCommand) {
        _command = command
    }

    fun navigateToWelcome() = navigate(NavigationCommand.To(AppRoutes.WELCOME))
    fun navigateToLogin() = navigate(NavigationCommand.To(AppRoutes.LOGIN))
    fun navigateToRegister() = navigate(NavigationCommand.To(AppRoutes.REGISTER))
    fun navigateToCategories() = navigate(NavigationCommand.To(AppRoutes.CATEGORIES))
    fun navigateToHome() = navigate(NavigationCommand.To(AppRoutes.HOME))
    fun navigateToAdmin() = navigate(NavigationCommand.To(AppRoutes.ADMIN))
    fun navigateToUsers() = navigate(NavigationCommand.To(AppRoutes.USERS))
    fun navigateToProductos() = navigate(NavigationCommand.To(AppRoutes.PRODUCTOS))
    fun navigateToDetalleProducto(productoId: Int) {
        val route = "detalle_producto/$productoId"
        navigate(NavigationCommand.To(route))
    }

    fun back() = navigate(NavigationCommand.Back)

    interface Provider {
        fun getNavigator(): Navigator
    }
}
