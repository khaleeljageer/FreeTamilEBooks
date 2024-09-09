/*
 * Copyright 2021 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package com.epubreader.android.reader

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import com.epubreader.android.R
import com.epubreader.android.databinding.ActivityReaderBinding
import com.epubreader.android.drm.DrmManagementContract
import com.epubreader.android.drm.DrmManagementFragment
import com.epubreader.android.outline.OutlineContract
import com.epubreader.android.outline.OutlineFragment
import dagger.hilt.android.AndroidEntryPoint
import org.readium.navigator.media2.ExperimentalMedia2
import org.readium.r2.shared.UserException
import org.readium.r2.shared.publication.Locator
import timber.log.Timber

/*
 * An activity to read a publication
 *
 * This class can be used as it is or be inherited from.
 */
@AndroidEntryPoint
open class ReaderActivity : AppCompatActivity() {
    private val readerViewModel: ReaderViewModel by viewModels()

    private lateinit var binding: ActivityReaderBinding
    private lateinit var readerFragment: BaseReaderFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get the bookId from the Intent
        val bookId: Long = intent.getLongExtra("bookId", -1L)
        readerViewModel.setUpArgs(bookId)

        val binding = ActivityReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
        * We provide dummy publications if the [ReaderActivity] is restored after the app process
        * was killed because the [ReaderRepository] is empty.
        * In that case, finish the activity as soon as possible and go back to the previous one.
        */
        if (readerViewModel.publication.readingOrder.isEmpty()) {
            Timber.tag("Khaleel").d("Finishing Activity")
            Timber.tag("Khaleel").d("publication : ${readerViewModel.publication.readingOrder}")
            finish()
        }

        this.binding = binding

        val readerFragment = supportFragmentManager.findFragmentByTag(READER_FRAGMENT_TAG)
            ?.let { it as BaseReaderFragment }
            ?: run {
                createReaderFragment(readerViewModel.readerInitData)
            }


        Timber.tag("Khaleel").d("isVisualReader: ${readerFragment is VisualReaderFragment}")
        if (readerFragment is VisualReaderFragment) {
            val fullscreenDelegate = FullscreenReaderActivityDelegate(this, readerFragment, binding)
            lifecycle.addObserver(fullscreenDelegate)
        }

        readerFragment?.let { this.readerFragment = it }

        readerViewModel.activityChannel.receive(this) { handleReaderFragmentEvent(it) }

        reconfigureActionBar()

        supportFragmentManager.setFragmentResultListener(
            OutlineContract.REQUEST_KEY,
            this
        ) { _, result ->
            val locator = OutlineContract.parseResult(result).destination
            closeOutlineFragment(locator)
        }

        supportFragmentManager.setFragmentResultListener(
            DrmManagementContract.REQUEST_KEY,
            this
        ) { _, result ->
            if (DrmManagementContract.parseResult(result).hasReturned)
                finish()
        }

        supportFragmentManager.addOnBackStackChangedListener {
            reconfigureActionBar()
        }

        // Add support for display cutout.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
    }

    @OptIn(ExperimentalMedia2::class)
    private fun createReaderFragment(readerData: ReaderInitData): BaseReaderFragment? {
        val readerClass: Class<out Fragment>? = when (readerData) {
            is EpubReaderInitData -> EpubReaderFragment::class.java
            is DummyReaderInitData -> null
        }

        readerClass?.let { it ->
            supportFragmentManager.commitNow {
                replace(R.id.activity_container, it, Bundle(), READER_FRAGMENT_TAG)
            }
        }

        return supportFragmentManager.findFragmentByTag(READER_FRAGMENT_TAG) as BaseReaderFragment?
    }

    override fun onStart() {
        super.onStart()
        reconfigureActionBar()
    }

    private fun reconfigureActionBar() {
        val currentFragment = supportFragmentManager.fragments.lastOrNull()

        title = when (currentFragment) {
            is OutlineFragment -> readerViewModel.publication.metadata.title
            is DrmManagementFragment -> getString(R.string.title_fragment_drm_management)
            else -> null
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(
            when (currentFragment) {
                is OutlineFragment, is DrmManagementFragment -> true
                else -> false
            }
        )
    }

    override fun finish() {
        setResult(Activity.RESULT_OK, Intent().putExtras(intent))
        super.finish()
    }

    private fun handleReaderFragmentEvent(event: ReaderViewModel.Event) {
        when (event) {
            is ReaderViewModel.Event.OpenOutlineRequested -> showOutlineFragment()
            is ReaderViewModel.Event.OpenDrmManagementRequested -> showDrmManagementFragment()
            is ReaderViewModel.Event.Failure -> showError(event.error)
            else -> {}
        }
    }

    private fun showError(error: UserException) {
        Toast.makeText(this, error.getUserMessage(this), Toast.LENGTH_LONG).show()
    }

    private fun showOutlineFragment() {
        supportFragmentManager.commit {
            add(
                R.id.activity_container,
                OutlineFragment::class.java,
                Bundle(),
                OUTLINE_FRAGMENT_TAG
            )
            hide(readerFragment)
            addToBackStack(null)
        }
    }

    private fun closeOutlineFragment(locator: Locator) {
        readerFragment.go(locator, true)
        supportFragmentManager.popBackStack()
    }

    private fun showDrmManagementFragment() {
        supportFragmentManager.commit {
            add(
                R.id.activity_container,
                DrmManagementFragment::class.java,
                Bundle(),
                DRM_FRAGMENT_TAG
            )
            hide(readerFragment)
            addToBackStack(null)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                supportFragmentManager.popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val READER_FRAGMENT_TAG = "reader"
        const val OUTLINE_FRAGMENT_TAG = "outline"
        const val DRM_FRAGMENT_TAG = "drm"
    }
}
