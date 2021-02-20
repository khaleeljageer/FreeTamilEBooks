package com.jskaleel.fte.ui.main.download

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jskaleel.fte.R
import com.jskaleel.fte.data.entities.DownloadResult
import com.jskaleel.fte.data.entities.LocalBooks
import com.jskaleel.fte.databinding.FragmentDownloadsBinding
import com.jskaleel.fte.ui.base.BookListAdapter
import com.jskaleel.fte.utils.FileUtils
import com.jskaleel.fte.utils.hide
import com.jskaleel.fte.utils.openBook
import com.jskaleel.fte.utils.show
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class DownloadsFragment : Fragment(), CoroutineScope, (Int, LocalBooks) -> Unit {

    private val downloadAdapter: BookListAdapter by lazy {
        BookListAdapter(mutableListOf(), this)
    }

    private val downloadsViewModel: DownloadsViewModel by inject()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentDownloadsBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emptyLayout = view.findViewById<LinearLayout>(R.id.emptyLayout)
        val rvDownloadList = view.findViewById<RecyclerView>(R.id.rvDownloadList)

        with(rvDownloadList) {
            this.setHasFixedSize(true)
            this.layoutManager = LinearLayoutManager(
                requireContext(),
                RecyclerView.VERTICAL,
                false
            )
            this.adapter = downloadAdapter
        }

        downloadsViewModel.loadSavedBooks().observe(viewLifecycleOwner, {
            val localBooks = mutableListOf<LocalBooks>()
            it?.let {
                for (book in it) {
                    localBooks.add(
                        LocalBooks(
                            book.title,
                            book.bookid,
                            book.author,
                            book.image,
                            book.epub,
                            "",
                            true,
                            book.savedPath
                        )
                    )
                }
            }

            if (localBooks.isNullOrEmpty()) {
                emptyLayout.show()
                rvDownloadList.hide()
            } else {
                emptyLayout.hide()
                rvDownloadList.show()
                downloadAdapter.loadBooks(localBooks.reversed())
            }
        })

//        downloadsViewModel.savedBooks.observe(viewLifecycleOwner, {
//            if (it.isNullOrEmpty()) {
//                emptyLayout.show()
//                rvDownloadList.hide()
//            } else {
//                emptyLayout.hide()
//                rvDownloadList.show()
//                downloadAdapter.loadBooks(it)
//            }
//        })
    }

    override fun invoke(position: Int, book: LocalBooks) {
        if (position == -1) {
            book.openBook(requireContext())
        } else {
            downloadBook(position, book)
        }
    }

    private fun downloadBook(position: Int, book: LocalBooks) {
        launch {
            val result = withContext(Dispatchers.IO) {
                FileUtils.downloadBook(requireContext(), book)
            }
            when (result) {
                is DownloadResult.Success -> {
                    book.isDownloaded = true
                    book.savedPath = result.filePath.absolutePath
                    downloadAdapter.successUiUpdate(position, book)
                    downloadsViewModel.updateDatabase(book)
                }
                is DownloadResult.Error -> {
                    downloadAdapter.errorUiUpdate(position)
                    //Show Snack bar. if needed
                }
            }
        }
    }

    val job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

}