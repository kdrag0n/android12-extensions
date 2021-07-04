package dev.kdrag0n.android12ext.ui.settings.appearance

import android.app.*
import android.content.Context
import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.*
import dev.kdrag0n.android12ext.BuildConfig
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.core.CallService
import dev.kdrag0n.android12ext.core.OverlayManager
import dev.kdrag0n.android12ext.core.data.SettingsRepository
import dev.kdrag0n.android12ext.core.data.hasSystemUiGoogle
import dev.kdrag0n.android12ext.monet.theme.ReferenceGenerator
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsViewModel
import dev.kdrag0n.android12ext.ui.utils.featureSwitch
import dev.kdrag0n.android12ext.ui.utils.navPref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AppearanceSettingsViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val settingsRepo: SettingsRepository,
    private val refGen: ReferenceGenerator,
    private val overlayManager: OverlayManager,
) : BaseSettingsViewModel() {
    val openColorPicker = MutableLiveData<Int?>(null)
    private lateinit var colorPref: ColorSwatchPreference

    val openPalette = MutableLiveData<Unit?>(null)
    val renderPalettes = MutableLiveData<Unit?>(null)

    private val prefScreen = settingsRepo.prefScreen {
        val hasSystemUiGoogle = context.hasSystemUiGoogle()
        featureSwitch(
            key = "custom_monet",
            title = R.string.appearance_custom_monet,
            summary = if (hasSystemUiGoogle) R.string.appearance_custom_monet_desc else R.string.appearance_custom_monet_desc_forced,
            icon = R.drawable.ic_fluent_paint_brush_24_regular,
            default = !hasSystemUiGoogle,
            enabled = hasSystemUiGoogle,
        )
        featureSwitch(
            key = "custom_monet_accurate_shades",
            title = R.string.appearance_custom_monet_accurate_shades,
            summary = R.string.appearance_custom_monet_accurate_shades_desc,
            icon = R.drawable.ic_fluent_dark_theme_24_regular,
            dependency = "custom_monet_enabled",
        )
        seekBar("custom_monet_chroma_multiplier") {
            titleRes = R.string.appearance_custom_monet_chroma_multiplier
            iconRes = R.drawable.ic_fluent_color_fill_24_regular
            dependency = "custom_monet_enabled"

            min = 0
            default = 50
            max = 100
            step = 10
            formatter = { value ->
                String.format("%.01fx", value.toFloat() / 50)
            }
        }

        categoryHeader("category_colors") {
            titleRes = R.string.category_colors
        }
        featureSwitch(
            key = "monet_custom_color",
            title = R.string.appearance_monet_custom_color,
            summary = R.string.appearance_monet_custom_color_desc,
            icon = R.drawable.ic_fluent_color_line_24_regular,
            default = false,
        )
        colorPref("monet_custom_color_value") {
            titleRes = R.string.appearance_monet_custom_color_value
            dependency = "monet_custom_color_enabled"

            onClick {
                openColorPicker.value = settingsRepo.prefs.getInt("monet_custom_color_value", -1)
                false
            }
            colorPref = this
        }
        pref("monet_palette") {
            titleRes = R.string.appearance_monet_palette
            summaryRes = R.string.appearance_monet_palette_desc
            iconRes = R.drawable.ic_fluent_color_24_regular
            onClick {
                openPalette.value = Unit
                false
            }
        }

        if (!hasSystemUiGoogle) {
            categoryHeader("category_aosp") {
                titleRes = R.string.category_aosp
            }
            featureSwitch(
                key = "aosp_circle_icons2",
                title = R.string.appearance_aosp_circle_icons,
                summary = R.string.appearance_aosp_circle_icons_desc,
                icon = R.drawable.ic_fluent_circle_24_regular,
                default = false,
            ) {
                onClick {
                    viewModelScope.launch {
                        overlayManager.updateCircleOverlay()
                    }
                    false
                }
            }
        }

        // Debug
        if (BuildConfig.DEBUG) {
            categoryHeader("debug_header") {
                title = "Debug"
            }
            switch("generate_palette_dynamic") {
                title = "Generate dynamic palette"
            }
            pref("render_palettes") {
                title = "Render palettes"
                dependency = "generate_palette_dynamic"
                onClick {
                    renderPalettes.value = Unit
                    false
                }
            }
            pref("gen_ref") {
                title = "Generate reference color table"
                onClick {
                    refGen.generateTable()
                    false
                }
            }
            navPref(
                key = "monet_quantizer",
                title = R.string.appearance_monet_quantizer,
                icon = R.drawable.ic_fluent_wallpaper_24_regular,
                action = R.id.action_settings_appearance_to_quantizer,
                vm = this@AppearanceSettingsViewModel,
            )
            pref("benchmark_monet") {
                title = "Benchmark color generation"
                onClick {
                    AutoPaletteRenderer.runBenchmark()
                    false
                }
            }
            pref("test_ongoing_call") {
                title = "Test ongoing call"
                onClick {
                    CallService.start(context)
                    false
                }
            }
        }
    }
    override val prefAdapter = PreferencesAdapter(prefScreen)

    val selectedColor = MutableLiveData<Int>()
    private val selectedColorObserver = Observer<Int> { color ->
        viewModelScope.launch {
            val valueChanged = withContext(Dispatchers.IO) {
                val oldValue = settingsRepo.prefs.getInt("monet_custom_color_value", Color.BLUE)

                settingsRepo.prefs.edit().run {
                    putInt("monet_custom_color_value", color)
                    commit()
                }

                oldValue != color
            }

            colorPref.requestRebind()

            if (valueChanged && settingsRepo.prefs.getBoolean("generate_palette_dynamic", false)) {
                openPalette.value = Unit
            }
        }
    }

    init {
        selectedColor.observeForever(selectedColorObserver)
    }

    override fun onCleared() {
        selectedColor.removeObserver(selectedColorObserver)
    }
}
