/*
 * Copyright 2024 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package com.jskaleel.epub.utils.extensions

import android.text.Html
import android.text.Spanned

fun String.toHtml(): Spanned =
    Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT)
