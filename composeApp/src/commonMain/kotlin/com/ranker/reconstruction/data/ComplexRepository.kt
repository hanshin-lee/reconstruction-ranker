package com.ranker.reconstruction.data

import com.ranker.reconstruction.data.model.Complex
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import reconstruction_ranker.composeapp.generated.resources.Res

private val json = Json { ignoreUnknownKeys = true }

object ComplexRepository {

    @OptIn(ExperimentalResourceApi::class)
    suspend fun loadAll(): List<Complex> {
        val files = listOf(
            "files/complex_apgujeong3.json",
            "files/complex_sinbanpo2.json"
        )
        return files.map { path ->
            val bytes = Res.readBytes(path)
            json.decodeFromString<Complex>(bytes.decodeToString())
        }.sortedByDescending { it.landValuePerPyeong }
    }
}
