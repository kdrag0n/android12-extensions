package dev.kdrag0n.android12ext.ui.monet.palette

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.databinding.ColorSampleLargeBinding
import dev.kdrag0n.android12ext.databinding.ColorShadeLabelBinding
import dev.kdrag0n.android12ext.databinding.FragmentPaletteBinding
import dev.kdrag0n.android12ext.ui.BaseFragment
import dev.kdrag0n.colorkt.conversion.ConversionGraph.convert
import dev.kdrag0n.colorkt.rgb.Srgb
import dev.kdrag0n.monet.theme.ColorSwatch

@AndroidEntryPoint
class PaletteFragment : BaseFragment(R.layout.fragment_palette) {
    private val viewModel: PaletteViewModel by viewModels()
    private val binding by viewBinding(FragmentPaletteBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.setBackgroundResource(R.color.palette_bg)

        if (viewModel.seedColor != 0) {
            val seedTint = ColorStateList.valueOf(viewModel.seedColor)
            binding.shadeLabels.colorSampleSeed.root.backgroundTintList = seedTint
        } else {
            binding.shadeLabels.colorSampleSeed.root.visibility = View.INVISIBLE
        }

        binding.shadeLabels.apply {
            bind(colorShade0,       0)
            bind(colorShade10,     10)
            bind(colorShade50,     50)
            bind(colorShade100,   100)
            bind(colorShade200,   200)
            bind(colorShade300,   300)
            bind(colorShade400,   400)
            bind(colorShade500,   500)
            bind(colorShade600,   600)
            bind(colorShade700,   700)
            bind(colorShade800,   800)
            bind(colorShade900,   900)
            bind(colorShade1000, 1000)
        }

        viewModel.colors.forEach { (swatchName, shades) ->
            val (swatch, swatchLabelId) = when (swatchName) {
                "accent1" -> binding.accent1 to R.string.monet_palette_accent1
                "accent2" -> binding.accent2 to R.string.monet_palette_accent2
                "accent3" -> binding.accent3 to R.string.monet_palette_accent3
                "neutral1" -> binding.neutral1 to R.string.monet_palette_neutral1
                "neutral2" -> binding.neutral2 to R.string.monet_palette_neutral2
                else -> error("Invalid swatch $swatchName")
            }

            swatch.colorSwatchLabel.text = getString(swatchLabelId)
            shades.apply {
                swatch.apply {
                    bind(colorShade0,       0)
                    bind(colorShade10,     10)
                    bind(colorShade50,     50)
                    bind(colorShade100,   100)
                    bind(colorShade200,   200)
                    bind(colorShade300,   300)
                    bind(colorShade400,   400)
                    bind(colorShade500,   500)
                    bind(colorShade600,   600)
                    bind(colorShade700,   700)
                    bind(colorShade800,   800)
                    bind(colorShade900,   900)
                    bind(colorShade1000, 1000)
                }
            }
        }
    }

    companion object {
        private fun bind(binding: ColorShadeLabelBinding, shade: Int) {
            binding.root.text = shade.toString()
        }

        private fun ColorSwatch.bind(binding: ColorSampleLargeBinding, shade: Int) {
            val color = this[shade]!!
            val tint = ColorStateList.valueOf(color.convert<Srgb>().toRgb8())
            binding.root.backgroundTintList = tint
        }
    }
}
