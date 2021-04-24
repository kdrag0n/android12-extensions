package dev.kdrag0n.android12ext.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.chrisbanes.insetter.applyInsetter
import dev.kdrag0n.android12ext.R

private const val XPOSED_MANAGER_PACKAGE = "org.lsposed.manager"

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    private lateinit var navController: NavController
    private var xposedDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setDecorFitsSystemWindows(false)

        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.applyInsetter {
            type(statusBars = true) {
                margin()
            }
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

        viewModel.isXposedHooked.observe(this) { isHooked ->
            xposedDialog?.dismiss()
            xposedDialog = null

            if (!isHooked) {
                val managerIntent = packageManager.getLaunchIntentForPackage(
                    XPOSED_MANAGER_PACKAGE
                )
                if (managerIntent == null) {
                    xposedDialog = MaterialAlertDialogBuilder(this).run {
                        setTitle(R.string.error_xposed_manager_not_installed)
                        setMessage(R.string.error_xposed_manager_not_installed_desc)
                        setCancelable(false)
                        show()
                    }
                } else {
                    xposedDialog = MaterialAlertDialogBuilder(this).run {
                        setTitle(R.string.error_xposed_module_missing)
                        setMessage(R.string.error_xposed_module_missing_desc)
                        setCancelable(false)
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
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateHookState()
    }
}