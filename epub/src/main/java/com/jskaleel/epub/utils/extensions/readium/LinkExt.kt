/*
 * Copyright 2023 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package com.jskaleel.epub.utils.extensions.readium

import org.readium.r2.shared.publication.Link

val Link.outlineTitle: String
    get() = title ?: href.toString()
