/*
 * Copyright 2021 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package com.jskhaleel.reader.data

import kotlinx.coroutines.flow.Flow
import com.jskhaleel.reader.data.db.CatalogDao
import com.jskhaleel.reader.data.model.Catalog

class CatalogRepository(private val catalogDao: CatalogDao) {

    suspend fun insertCatalog(catalog: Catalog): Long {
        return catalogDao.insertCatalog(catalog)
    }

    fun getCatalogsFromDatabase(): Flow<List<Catalog>> = catalogDao.getCatalogModels()

    suspend fun deleteCatalog(id: Long) = catalogDao.deleteCatalog(id)
}
