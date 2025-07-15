/*
 * Copyright 2021 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package com.jskaleel.epub.reader.tts

import androidx.fragment.app.activityViewModels
import com.jskaleel.epub.reader.ReaderViewModel
import com.jskaleel.epub.reader.preferences.UserPreferencesBottomSheetDialogFragment
import com.jskaleel.epub.reader.preferences.UserPreferencesViewModel
import org.readium.r2.shared.ExperimentalReadiumApi
import kotlin.getValue

@OptIn(ExperimentalReadiumApi::class)
class TtsPreferencesBottomSheetDialogFragment : UserPreferencesBottomSheetDialogFragment(
    "TTS Settings"
) {

    private val viewModel: ReaderViewModel by activityViewModels()

    override val preferencesModel: UserPreferencesViewModel<*, *> by lazy {
        checkNotNull(viewModel.tts!!.preferencesModel)
    }
}
