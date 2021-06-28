package dev.kdrag0n.android12ext.ui.about

import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.LibsConfiguration
import com.mikepenz.aboutlibraries.LibsFragmentCompat
import com.mikepenz.aboutlibraries.entity.Library
import dagger.hilt.android.AndroidEntryPoint
import dev.kdrag0n.android12ext.BuildConfig
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.ui.BaseToolbarFragment
import dev.kdrag0n.android12ext.ui.applyTransitions
import dev.kdrag0n.android12ext.ui.applyTransitionsViewCreated
import dev.kdrag0n.android12ext.ui.utils.openUri

// Basic attempt to deter automated code scanners and scrapers
private const val EMAIL_ENC_1 = "YldGcGJIUnZPZw" // intentionally missing padding
private const val EMAIL_ENC_2 = "YXBwcys"
private val EMAIL_URI = Base64.decode(Base64.decode(EMAIL_ENC_1, Base64.DEFAULT), Base64.DEFAULT).decodeToString() +
        Base64.decode(EMAIL_ENC_2, Base64.DEFAULT).decodeToString() +
        BuildConfig.APPLICATION_ID.split(".")[2] +
        "@" +
        BuildConfig.APPLICATION_ID.split(".").slice(0 until 2)
            .reversed()
            .joinToString(".")

@AndroidEntryPoint
class AboutFragment : BaseToolbarFragment() {
    private val libsFragment = LibsFragmentCompat()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyTransitions()
    }

    override fun onCreateContentView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val libsBuilder = LibsBuilder().apply {
            withAboutIconShown(true)
            withAboutAppName(getString(R.string.app_name))
            withAboutDescription(getString(R.string.about_app_description))
            withAboutSpecial1(getString(R.string.donate))
            withAboutSpecial2(getString(R.string.contact))
            withAboutSpecial3(getString(R.string.source_code))

            // Hide version code
            withAboutVersionShown(false)
            withAboutVersionShownName(true)

            // Handle button presses
            LibsConfiguration.listener = object : LibsConfiguration.LibsListener {
                override fun onExtraClicked(v: View, specialButton: Libs.SpecialButton): Boolean {
                    return when (specialButton) {
                        Libs.SpecialButton.SPECIAL1 -> {
                            openUri(getString(R.string.about_uri_donate))
                            true
                        }
                        Libs.SpecialButton.SPECIAL2 -> {
                            openUri(EMAIL_URI)
                            true
                        }
                        Libs.SpecialButton.SPECIAL3 -> {
                            openUri(getString(R.string.about_uri_source_code))
                            true
                        }
                        else -> false
                    }
                }

                override fun onIconClicked(v: View) = Unit
                override fun onIconLongClicked(v: View) = false
                override fun onLibraryAuthorClicked(v: View, library: Library) = false
                override fun onLibraryAuthorLongClicked(v: View, library: Library) = false
                override fun onLibraryBottomClicked(v: View, library: Library) = false
                override fun onLibraryBottomLongClicked(v: View, library: Library) = false
                override fun onLibraryContentClicked(v: View, library: Library) = false
                override fun onLibraryContentLongClicked(v: View, library: Library) = false
            }
        }
        arguments = Bundle().apply {
            putSerializable("data", libsBuilder)
        }

        return libsFragment.onCreateView(inflater.context, inflater, container, savedInstanceState, arguments)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyTransitionsViewCreated()
        libsFragment.onViewCreated(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        libsFragment.onDestroyView()
    }
}
