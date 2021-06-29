package dev.kdrag0n.android12ext.ui.monet.quantizer

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.databinding.ColorSampleBinding
import dev.kdrag0n.android12ext.databinding.FragmentQuantizerBinding
import dev.kdrag0n.android12ext.ui.BaseFragment

@AndroidEntryPoint
class QuantizerFragment : BaseFragment(R.layout.fragment_quantizer) {
    private val viewModel: QuantizerViewModel by viewModels()
    private val binding by viewBinding(FragmentQuantizerBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.wallpaperDrawable.observe(viewLifecycleOwner) {
            binding.wallpaperView.setImageDrawable(it)
        }

        viewModel.wallpaperColors.observe(viewLifecycleOwner) {
            setWallpaperColor(binding.colorSample1, it, 0)
            setWallpaperColor(binding.colorSample2, it, 1)
            setWallpaperColor(binding.colorSample3, it, 2)
            setWallpaperColor(binding.colorSample4, it, 3)
            setWallpaperColor(binding.colorSample5, it, 4)
        }
    }

    private fun setWallpaperColor(binding: ColorSampleBinding, colors: List<Int>, index: Int) {
        val colorView = binding.root
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
