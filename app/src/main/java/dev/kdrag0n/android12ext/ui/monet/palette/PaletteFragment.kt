package dev.kdrag0n.android12ext.ui.monet.palette

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.ui.BaseFragment

@AndroidEntryPoint
class PaletteFragment : BaseFragment() {
    private val viewModel: PaletteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_palette, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.setBackgroundResource(R.color.palette_bg)

        val shadeLabels = view.findViewById<LinearLayout>(R.id.color_swatch_shade_labels)
        val seedTint = ColorStateList.valueOf(viewModel.seedColor)
        shadeLabels.findViewById<View>(R.id.color_sample_seed).backgroundTintList = seedTint

        shadeIds.forEach { (shade, id) ->
            shadeLabels.findViewById<TextView>(id).text = shade.toString()
        }

        viewModel.colors.forEach { (swatchName, shades) ->
            val (swatchId, swatchLabelId) = when (swatchName) {
                "accent1" -> R.id.color_swatch_accent1 to R.string.monet_palette_accent1
                "accent2" -> R.id.color_swatch_accent2 to R.string.monet_palette_accent2
                "accent3" -> R.id.color_swatch_accent3 to R.string.monet_palette_accent3
                "neutral1" -> R.id.color_swatch_neutral1 to R.string.monet_palette_neutral1
                "neutral2" -> R.id.color_swatch_neutral2 to R.string.monet_palette_neutral2
                else -> error("Invalid swatch $swatchName")
            }

            val swatch = view.findViewById<LinearLayout>(swatchId)
            swatch.findViewById<TextView>(R.id.color_swatch_label).text = getString(swatchLabelId)
            shades.forEach { (shade, color) ->
                val tint = ColorStateList.valueOf(color.toLinearSrgb().toSrgb().quantize8())
                swatch.findViewById<View>(shadeIds[shade]!!).backgroundTintList = tint
            }
        }
    }

    companion object {
        private val shadeIds = mapOf(
            0    to R.id.color_shade_0,
            10   to R.id.color_shade_10,
            50   to R.id.color_shade_50,
            100  to R.id.color_shade_100,
            200  to R.id.color_shade_200,
            300  to R.id.color_shade_300,
            400  to R.id.color_shade_400,
            500  to R.id.color_shade_500,
            600  to R.id.color_shade_600,
            700  to R.id.color_shade_700,
            800  to R.id.color_shade_800,
            900  to R.id.color_shade_900,
            1000 to R.id.color_shade_1000,
        )
    }
}
