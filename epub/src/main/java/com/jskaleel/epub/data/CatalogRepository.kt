/*
 * Copyright 2021 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package com.jskaleel.epub.data

import kotlinx.coroutines.flow.Flow
import com.jskaleel.epub.data.dao.EBookCatalogDao
import com.jskaleel.epub.data.model.EBookCatalog

class CatalogRepository(private val catalogDao: EBookCatalogDao) {

    suspend fun insertCatalog(catalog: EBookCatalog): Long {
        return catalogDao.insertCatalog(catalog)
    }

    fun getCatalogsFromDatabase(): Flow<List<EBookCatalog>> = catalogDao.getCatalogModels()

    suspend fun deleteCatalog(id: Long) = catalogDao.deleteCatalog(id)
}
