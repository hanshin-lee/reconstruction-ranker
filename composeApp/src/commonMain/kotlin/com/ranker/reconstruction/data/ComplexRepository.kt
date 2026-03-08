package com.ranker.reconstruction.data

import com.ranker.reconstruction.data.model.Complex
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import reconstruction_ranker.composeapp.generated.resources.Res

private val json = Json { ignoreUnknownKeys = true }

object ComplexRepository {

    @OptIn(ExperimentalResourceApi::class)
    private suspend fun loadBundled(): List<Complex> {
        val files = listOf(
            "files/complex_apgujeong3.json",
            "files/complex_sinbanpo2.json"
        )
        return files.map { path ->
            val bytes = Res.readBytes(path)
            json.decodeFromString<Complex>(bytes.decodeToString())
        }
    }

    suspend fun loadAll(): List<Complex> {
        val bundled = loadBundled()
        val userCreated = UserComplexStore.loadAll()
        return (bundled + userCreated).sortedByDescending { it.landValuePerPyeong }
    }

    suspend fun addComplex(complex: Complex) = UserComplexStore.save(complex)

    suspend fun deleteComplex(complexId: String) = UserComplexStore.delete(complexId)
}
