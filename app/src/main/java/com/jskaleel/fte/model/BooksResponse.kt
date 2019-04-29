package com.jskaleel.fte.model

import com.jskaleel.fte.database.entities.LocalBooks

data class BooksResponse(val books: List<LocalBooks>)