package dev.kdrag0n.android12ext.monet.overlay

import dev.kdrag0n.android12ext.utils.call

class FabricatedOverlay {
    // Replicate framework structure
    class Builder(
        owningPackage: String,
        name: String,
        targetPackage: String,
    ) {
        private val clazz = Class.forName("android.content.om.FabricatedOverlay\$Builder")
        private val obj = clazz.getDeclaredConstructor(
            String::class.java,
            String::class.java,
            String::class.java,
        ).newInstance(owningPackage, name, targetPackage)

        fun setResourceValue(resourceName: String, dataType: Int, value: Int) = apply {
            obj.call("setResourceValue", resourceName, dataType, value)
        }

        fun build(): Any {
            return obj.call("build")!!
        }
    }

    companion object {
        const val DATA_TYPE_COLOR = 28
    }
}
