/*
 * Copyright 2022 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package com.epubreader.android.reader.preferences

import android.app.Dialog
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.fragment.app.activityViewModels
import com.epubreader.android.reader.ReaderViewModel
import com.epubreader.android.utils.compose.ComposeBottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.readium.r2.shared.ExperimentalReadiumApi

@OptIn(ExperimentalReadiumApi::class)
class UserPreferencesBottomSheetDialogFragment : ComposeBottomSheetDialogFragment(
    isScrollable = true
) {
    private val model: ReaderViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            // Reduce the dim to see the impact of the settings on the page.
            window?.setDimAmount(0.1f)

            behavior.apply {
                peekHeight = 1000
                maxHeight = 1000
            }
        }

    @Composable
    override fun Content() {
        val settingsModel = checkNotNull(model.settings)
        UserPreferences(settingsModel)
    }
}
