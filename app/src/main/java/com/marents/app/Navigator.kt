package com.marents.app

import android.os.Bundle


sealed class NavigationCommand {
    data class To(val route: String, val args: Bundle? = null) : NavigationCommand()
    data object Back : NavigationCommand()
}

object AppRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val CATEGORIES = "categories"
    const val HOME = "home"
    const val ADMIN = "admin"
    const val USERS = "users"
    const val PRODUCTOS = "productos"
    const val PERSONALIZADOS = "personalizados"
}

class Navigator {
    private var _command: NavigationCommand? = null
    
    fun getCommand(): NavigationCommand? {
        val cmd = _command
        _command = null
        return cmd
    }
    
    private fun navigate(command: NavigationCommand) {
        _command = command
    }

    fun navigateToLogin() = navigate(NavigationCommand.To(AppRoutes.LOGIN))
    fun navigateToCategories() = navigate(NavigationCommand.To(AppRoutes.CATEGORIES))
    fun navigateToHome() = navigate(NavigationCommand.To(AppRoutes.HOME))
    fun navigateToProductosCategoria(categoriaNombre: String) {
        val route = "productos_categoria/$categoriaNombre"
        navigate(NavigationCommand.To(route))
    }
    fun navigateToPersonalizados() = navigate(NavigationCommand.To(AppRoutes.PERSONALIZADOS))

    fun back() = navigate(NavigationCommand.Back)

    interface Provider {
        fun getNavigator(): Navigator
    }
}
