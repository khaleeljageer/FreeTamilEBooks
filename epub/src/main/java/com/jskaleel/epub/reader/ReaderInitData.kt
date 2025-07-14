/*
 * Copyright 2022 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

@file:OptIn(ExperimentalReadiumApi::class)

package com.jskaleel.epub.reader

import com.jskaleel.epub.reader.preferences.PreferencesManager
import org.readium.navigator.media.tts.AndroidTtsNavigatorFactory
import org.readium.navigator.media.tts.android.AndroidTtsPreferences
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Manifest
import org.readium.r2.shared.publication.Metadata
import org.readium.r2.shared.publication.Publication

sealed class ReaderInitData {
    abstract val bookId: Long
    abstract val publication: Publication
}

sealed class VisualReaderInitData(
    override val bookId: Long,
    override val publication: Publication,
    val initialLocation: Locator?,
    val ttsInitData: TtsInitData?,
) : ReaderInitData()

class EpubReaderInitData(
    bookId: Long,
    publication: Publication,
    initialLocation: Locator?,
    val preferencesManager: PreferencesManager<EpubPreferences>,
    val navigatorFactory: EpubNavigatorFactory,
    ttsInitData: TtsInitData?,
) : VisualReaderInitData(bookId, publication, initialLocation, ttsInitData)

class TtsInitData(
    val mediaServiceFacade: MediaServiceFacade,
    val navigatorFactory: AndroidTtsNavigatorFactory,
    val preferencesManager: PreferencesManager<AndroidTtsPreferences>,
)

class DummyReaderInitData(
    override val bookId: Long,
) : ReaderInitData() {
    override val publication: Publication = Publication(
        Manifest(
            metadata = Metadata(identifier = "dummy")
        )
    )
}
