package com.ranker.reconstruction.data

import com.ranker.reconstruction.data.model.Complex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }

actual object UserComplexStore {

    private val file: File by lazy {
        val dir = File(System.getProperty("user.home"), ".reconstruction-ranker")
        dir.mkdirs()
        File(dir, "user_complexes.json")
    }

    private suspend fun read(): MutableList<Complex> = withContext(Dispatchers.IO) {
        if (!file.exists()) return@withContext mutableListOf()
        try {
            json.decodeFromString<List<Complex>>(file.readText()).toMutableList()
        } catch (_: Exception) {
            mutableListOf()
        }
    }

    private suspend fun write(list: List<Complex>) = withContext(Dispatchers.IO) {
        file.writeText(json.encodeToString(list))
    }

    actual suspend fun loadAll(): List<Complex> = read()

    actual suspend fun save(complex: Complex) {
        val list = read()
        val idx = list.indexOfFirst { it.id == complex.id }
        if (idx >= 0) list[idx] = complex else list.add(complex)
        write(list)
    }

    actual suspend fun delete(complexId: String) {
        val list = read().filter { it.id != complexId }
        write(list)
    }
}
