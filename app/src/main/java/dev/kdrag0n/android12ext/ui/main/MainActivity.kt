package dev.kdrag0n.android12ext.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import dagger.hilt.android.AndroidEntryPoint
import dev.kdrag0n.android12ext.BuildConfig
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.ui.settings.appearance.ColorDialogViewModel
import dev.kdrag0n.android12ext.ui.utils.NoSwipeBehavior

private const val XPOSED_MANAGER_PACKAGE = "org.lsposed.manager"
private const val MAGISK_MANAGER_PACKAGE = "com.topjohnwu.magisk"

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ColorPickerDialogListener {
    private val viewModel: MainViewModel by viewModels()
    private val colorDialogViewModel: ColorDialogViewModel by viewModels()
    private lateinit var navController: NavController

    private var xposedDialog: AlertDialog? = null
    private var rootDialog: AlertDialog? = null
    private var reloadSnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setDecorFitsSystemWindows(false)

        setContentView(R.layout.content_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        viewModel.isXposedHooked.observe(this) { isHooked ->
            xposedDialog?.dismiss()
            xposedDialog = null

            if (!isHooked) {
                val managerIntent = packageManager.getLaunchIntentForPackage(XPOSED_MANAGER_PACKAGE)

                if (managerIntent == null) {
                    xposedDialog = MaterialAlertDialogBuilder(this).run {
                        setTitle(R.string.error_xposed_manager_not_installed)
                        setMessage(R.string.error_xposed_manager_not_installed_desc)
                        setCancelable(BuildConfig.DEBUG)
                        show()
                    }
                } else {
                    xposedDialog = MaterialAlertDialogBuilder(this).run {
                        setTitle(R.string.error_xposed_module_missing)
                        setMessage(R.string.error_xposed_module_missing_desc)
                        setCancelable(BuildConfig.DEBUG)
                        // Empty callback because we override it later
                        setPositiveButton(R.string.enable) { _, _ -> }
                        show()
                    }.apply {
                        // Override button callback to stop it from dismissing the dialog
                        getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                            startActivity(managerIntent)
                        }
                    }
                }
            }
        }

        viewModel.isRooted.observe(this) { isRooted ->
            rootDialog?.dismiss()
            rootDialog = null

            if (!isRooted) {
                val managerIntent = packageManager.getLaunchIntentForPackage(MAGISK_MANAGER_PACKAGE)!!

                rootDialog = MaterialAlertDialogBuilder(this).run {
                    setTitle(R.string.error_missing_root)
                    setMessage(R.string.error_missing_root_desc)
                    setCancelable(BuildConfig.DEBUG)
                    // Empty callback because we override it later
                    setPositiveButton(R.string.allow) { _, _ -> }
                    show()
                }.apply {
                    // Override button callback to stop it from dismissing the dialog
                    getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        startActivity(managerIntent)
                    }
                }
            }
        }

        viewModel.showReloadWarning.observe(this) { shouldReload ->
            reloadSnackbar?.dismiss()
            reloadSnackbar = null

            if (shouldReload) {
                reloadSnackbar = Snackbar.make(
                    findViewById(android.R.id.content),
                    R.string.applying_changes,
                    // We take care of showing and dismissing it
                    BaseTransientBottomBar.LENGTH_INDEFINITE
                ).apply {
                    setAction(R.string.cancel) {
                        viewModel.showReloadWarning.value = false
                    }
                    behavior = NoSwipeBehavior()
                    show()
                }
            }
        }
    }

    override fun onDialogDismissed(dialogId: Int) = Unit
    override fun onColorSelected(dialogId: Int, color: Int) {
        colorDialogViewModel.selectedColor.value = color
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateHookState()
        viewModel.updateRootState()
    }
}
