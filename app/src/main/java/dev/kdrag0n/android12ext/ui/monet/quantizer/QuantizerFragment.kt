package dev.kdrag0n.android12ext.ui.monet.quantizer

import android.content.res.ColorStateList
import android.graphics.PointF
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import androidx.core.view.forEach
import androidx.fragment.app.viewModels
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
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

        viewModel.wallpaperBitmap.observe(viewLifecycleOwner) {
            binding.wallpaperView.apply {
                setImage(ImageSource.bitmap(it))
                setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
                maxScale = 5.0f

                setOnStateChangedListener(object : SubsamplingScaleImageView.OnStateChangedListener {
                    override fun onCenterChanged(newCenter: PointF, origin: Int) = updateRect()
                    override fun onScaleChanged(newScale: Float, origin: Int) = updateRect()

                    private fun updateRect() {
                        post {
                            val rect = Rect(0, 0, 0, 0)
                            visibleFileRect(rect)
                            viewModel.imageRect.value = rect
                        }
                    }
                })
            }
        }

        viewModel.wallpaperColors.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.loadingProgress.visibility = View.VISIBLE
                binding.colorSamplesRow1.forEach { view ->
                    view.visibility = View.GONE
                }
                binding.colorSamplesRow2.forEach { view ->
                    view.visibility = View.GONE
                }
                return@observe
            }

            binding.loadingProgress.visibility = View.GONE

            setWallpaperColor(binding.colorSample1, it, 0)
            setWallpaperColor(binding.colorSample2, it, 1)
            setWallpaperColor(binding.colorSample3, it, 2)
            setWallpaperColor(binding.colorSample4, it, 3)
            setWallpaperColor(binding.colorSample5, it, 4)
            setWallpaperColor(binding.colorSample6, it, 5)
            setWallpaperColor(binding.colorSample7, it, 6)
            setWallpaperColor(binding.colorSample8, it, 7)
            setWallpaperColor(binding.colorSample9, it, 8)
            setWallpaperColor(binding.colorSample10, it, 9)
            setWallpaperColor(binding.colorSample11, it, 10)
            setWallpaperColor(binding.colorSample12, it, 11)
            setWallpaperColor(binding.colorSample13, it, 12)
            setWallpaperColor(binding.colorSample14, it, 13)
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
