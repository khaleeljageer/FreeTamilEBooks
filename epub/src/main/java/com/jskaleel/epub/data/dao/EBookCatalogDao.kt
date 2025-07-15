/*
 * Copyright 2021 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package com.jskaleel.epub.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jskaleel.epub.data.model.EBookCatalog
import kotlinx.coroutines.flow.Flow

@Dao
interface EBookCatalogDao {

    /**
     * Inserts an Catalog
     * @param catalog The Catalog model to insert
     * @return ID of the Catalog model that was added (primary key)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatalog(catalog: EBookCatalog): Long

    /**
     * Retrieve list of Catalog models based on Catalog model
     * @return List of Catalog models as Flow
     */
    @Query(
        "SELECT * FROM " + EBookCatalog.TABLE_NAME + " WHERE " + EBookCatalog.TITLE + " = :title AND " + EBookCatalog.HREF + " = :href AND " + EBookCatalog.TYPE + " = :type"
    )
    fun getCatalogModels(title: String, href: String, type: Int): Flow<List<EBookCatalog>>

    /**
     * Retrieve list of all Catalog models
     * @return List of Catalog models as Flow
     */
    @Query("SELECT * FROM " + EBookCatalog.TABLE_NAME)
    fun getCatalogModels(): Flow<List<EBookCatalog>>

    /**
     * Deletes an Catalog model
     * @param id The id of the Catalog model to delete
     */
    @Query("DELETE FROM " + EBookCatalog.TABLE_NAME + " WHERE " + EBookCatalog.ID + " = :id")
    suspend fun deleteCatalog(id: Long)
}
