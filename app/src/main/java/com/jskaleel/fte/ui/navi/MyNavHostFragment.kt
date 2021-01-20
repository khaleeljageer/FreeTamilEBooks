package com.jskaleel.fte.ui.navi

import androidx.navigation.fragment.NavHostFragment

class MyNavHostFragment : NavHostFragment() {
    override fun createFragmentNavigator() =
        MyFragmentNavigator(requireContext(), childFragmentManager, id)
}