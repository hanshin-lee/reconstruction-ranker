package com.ranker.reconstruction.data

import com.ranker.reconstruction.data.model.Complex

/**
 * Platform-specific persistent store for user-created complexes.
 * Each platform writes a JSON file (user_complexes.json) to its appropriate data directory.
 */
expect object UserComplexStore {
    suspend fun loadAll(): List<Complex>
    suspend fun save(complex: Complex)
    suspend fun delete(complexId: String)
}
