package dev.kdrag0n.android12ext.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import dev.kdrag0n.android12ext.R

abstract class BaseToolbarFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.toolbar_fragment, container, false)
        val frame = rootView.findViewById<FrameLayout>(R.id.content_frame)

        val contentView = onCreateContentView(inflater, frame, savedInstanceState)
        frame.addView(contentView)

        return rootView
    }

    protected abstract fun onCreateContentView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        val collapsingToolbar = view.findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        val navController = findNavController()

        val dest = navController.currentDestination!!
        collapsingToolbar.title = dest.label
        if (dest.id == navController.graph.startDestination) {
            toolbar.navigationIcon = null
        }

        toolbar.setNavigationOnClickListener {
            NavigationUI.navigateUp(navController, AppBarConfiguration(navController.graph))
        }
    }
}
