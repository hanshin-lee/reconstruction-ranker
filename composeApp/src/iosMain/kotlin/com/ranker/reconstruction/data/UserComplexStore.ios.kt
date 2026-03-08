package com.ranker.reconstruction.data

import com.ranker.reconstruction.data.model.Complex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSUserDomainMask
import platform.Foundation.stringByAppendingPathComponent

private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }

actual object UserComplexStore {

    private val filePath: String by lazy {
        val dirs = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory, NSUserDomainMask, true
        )
        val docDir = dirs.first() as NSString
        docDir.stringByAppendingPathComponent("user_complexes.json")
    }

    private suspend fun read(): MutableList<Complex> = withContext(Dispatchers.Default) {
        val fm = NSFileManager.defaultManager
        if (!fm.fileExistsAtPath(filePath)) return@withContext mutableListOf()
        try {
            val content = fm.contentsAtPath(filePath)
                ?.let { it.decodeToString() } ?: return@withContext mutableListOf()
            json.decodeFromString<List<Complex>>(content).toMutableList()
        } catch (_: Exception) {
            mutableListOf()
        }
    }

    private suspend fun write(list: List<Complex>) = withContext(Dispatchers.Default) {
        val text = json.encodeToString(list)
        (text as NSString).writeToFile(filePath, atomically = true, encoding = 4u, error = null)
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
