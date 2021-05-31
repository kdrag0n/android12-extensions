package dev.kdrag0n.android12ext.ui.monet.quantizer

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.annotation.IdRes
import coil.load
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.ui.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class QuantizerFragment : BaseFragment() {
    private val viewModel: QuantizerViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_quantizer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.wallpaperDrawable.observe(viewLifecycleOwner) {
            view.findViewById<ImageView>(R.id.wallpaper_view).load(it)
        }

        viewModel.wallpaperColors.observe(viewLifecycleOwner) {
            setWallpaperColor(view, R.id.color_sample1, it, 0)
            setWallpaperColor(view, R.id.color_sample2, it, 1)
            setWallpaperColor(view, R.id.color_sample3, it, 2)
            setWallpaperColor(view, R.id.color_sample4, it, 3)
            setWallpaperColor(view, R.id.color_sample5, it, 4)
        }
    }

    private fun setWallpaperColor(view: View, @IdRes sampleId: Int, colors: List<Int>, index: Int) {
        val colorView = view.findViewById<View>(sampleId)
        if (colors.size < index + 1) {
            colorView.visibility = View.GONE
            return
        } else {
            colorView.visibility = View.VISIBLE
        }

        val color = ColorStateList.valueOf(colors[index])
        colorView.backgroundTintList = color
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_settings, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item) || when (item.itemId) {
            else -> false
        }
    }
}
