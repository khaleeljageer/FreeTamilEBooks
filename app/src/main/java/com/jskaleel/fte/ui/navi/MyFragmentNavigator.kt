package com.jskaleel.fte.ui.navi

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator

@Navigator.Name("fragment")
class MyFragmentNavigator(
    context: Context,
    fm: FragmentManager,
    containerId: Int
) : FragmentNavigator(context, fm, containerId) {

    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ): NavDestination? {
        val shouldSkip = navOptions?.run {
            popUpTo == destination.id && !isPopUpToInclusive
        } ?: false

        return if (shouldSkip) null
        else super.navigate(destination, args, navOptions, navigatorExtras)
    }
}