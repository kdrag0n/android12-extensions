package dev.kdrag0n.android12ext.core.monet.overlay

import de.robv.android.xposed.XposedHelpers

class FabricatedOverlay {
    // Replicate framework structure
    class Builder(
        owningPackage: String,
        name: String,
        targetPackage: String,
    ) {
        private val clazz = XposedHelpers.findClass("android.content.om.FabricatedOverlay\$Builder", null)
        private val obj = XposedHelpers.findConstructorExact(
            clazz,
            String::class.java,
            String::class.java,
            String::class.java,
        ).newInstance(owningPackage, name, targetPackage)

        fun setResourceValue(resourceName: String, dataType: Int, value: Int) = apply {
            XposedHelpers.callMethod(
                obj,
                "setResourceValue",
                resourceName,
                dataType,
                value,
            )
        }

        fun build(): Any {
            return XposedHelpers.callMethod(obj, "build")
        }
    }

    companion object {
        const val DATA_TYPE_COLOR = 28
    }
}