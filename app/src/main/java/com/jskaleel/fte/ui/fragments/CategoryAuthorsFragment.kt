package com.jskaleel.fte.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.chip.Chip
import com.jskaleel.fte.R
import com.jskaleel.fte.utils.CommonAppData
import com.jskaleel.fte.utils.DeviceUtils
import kotlinx.android.synthetic.main.fragment_author_category.*

class CategoryAuthorsFragment : Fragment() {

    private var viewType: Int? = 1
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolBar.setNavigationOnClickListener {
            DeviceUtils.hideSoftKeyboard(activity!!)
            activity!!.findNavController(R.id.navHostFragment).navigateUp()
        }

        viewType = arguments?.let {
            val safeArgs = CategoryAuthorsFragmentArgs.fromBundle(it)
            safeArgs.TYPE
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_author_category, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val nameList = if (viewType == 1) {
            CommonAppData.getCategoryListWithCount(mContext)
        } else {
            CommonAppData.getAuthorsListWithCount(mContext)
        }

        toolBar.setTitle(
            if (viewType == 1) {
                R.string.category_list
            } else {
                R.string.authors_list
            }
        )

        for (item in nameList) {
            val chip = Chip(mContext)
            val paddingDp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10f,
                resources.displayMetrics
            ).toInt()
            chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
            chip.text = item
            chip.isCloseIconVisible = false
            chip.setOnClickListener {
                Toast.makeText(mContext, item, Toast.LENGTH_SHORT).show()
            }
            chipGroup.addView(chip)
        }
    }
}