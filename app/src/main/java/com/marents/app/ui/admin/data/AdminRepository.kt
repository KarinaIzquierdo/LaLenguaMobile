package com.marents.app.ui.admin.data

import com.marents.app.ApiService
import com.marents.app.network.dto.AdminStatsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AdminRepository(
    private val apiService: ApiService
) {
    suspend fun obtenerEstadisticas(): Result<AdminStatsResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAdminStats().execute()
                val body = response.body()

                if (response.isSuccessful && body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Error del servidor: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
