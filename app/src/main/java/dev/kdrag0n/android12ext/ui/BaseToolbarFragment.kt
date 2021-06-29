package dev.kdrag0n.android12ext.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import dev.kdrag0n.android12ext.databinding.ToolbarFragmentBinding

abstract class BaseToolbarFragment(
    @LayoutRes private val layoutId: Int,
) : BaseFragment() {
    // Workaround for Hilt Gradle Plugin's lack of support for default arguments
    constructor() : this(0)

    private var _binding: ToolbarFragmentBinding? = null
    protected val toolbarBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ToolbarFragmentBinding.inflate(inflater, container, false)

        val contentView = onCreateContentView(inflater, toolbarBinding.contentFrame, savedInstanceState)
        toolbarBinding.contentFrame.addView(contentView)

        return toolbarBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected open fun onCreateContentView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return if (layoutId == 0) {
            null
        } else {
            inflater.inflate(layoutId, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val dest = navController.currentDestination!!
        toolbarBinding.collapsingToolbar.title = dest.label
        if (dest.id == navController.graph.startDestination) {
            toolbarBinding.toolbar.navigationIcon = null
        }

        toolbarBinding.toolbar.setNavigationOnClickListener {
            NavigationUI.navigateUp(navController, AppBarConfiguration(navController.graph))
        }
    }
}
