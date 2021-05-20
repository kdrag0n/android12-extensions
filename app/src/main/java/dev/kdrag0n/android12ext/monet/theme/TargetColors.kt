package dev.kdrag0n.android12ext.monet.theme

import dev.kdrag0n.android12ext.monet.colors.Oklch

// Custom target color tables
object TargetColors {
    object Default : ColorScheme() {
        // Luminance decreases in ~2% steps until 90%
        // After 90%, it jumps in steps of ~4-5% until ending at 70%
        override val neutral1 = listOf(
            Oklch(1.00, 0.000, 0.0),
            Oklch(0.98, 0.005, 0.0),
            Oklch(0.96, 0.005, 0.0),
            Oklch(0.94, 0.005, 0.0),
            Oklch(0.92, 0.005, 0.0),
            Oklch(0.90, 0.005, 0.0),
            Oklch(0.86, 0.005, 0.0),
            Oklch(0.82, 0.005, 0.0),
            Oklch(0.77, 0.005, 0.0),
            Oklch(0.70, 0.005, 0.0),
            Oklch(0.00, 0.000, 0.0),
        )

        override val neutral2 = listOf(
            Oklch(1.00, 0.000, 0.0),
            Oklch(0.98, 0.003, 0.0),
            Oklch(0.96, 0.003, 0.0),
            Oklch(0.94, 0.003, 0.0),
            Oklch(0.92, 0.003, 0.0),
            Oklch(0.90, 0.003, 0.0),
            Oklch(0.86, 0.003, 0.0),
            Oklch(0.82, 0.003, 0.0),
            Oklch(0.77, 0.003, 0.0),
            Oklch(0.70, 0.003, 0.0),
            Oklch(0.00, 0.000, 0.0),
        )

        override val accent1 = listOf(
            Oklch(1.00, 0.0, 0.0),
            Oklch(0.98, 0.007982548755430906, 0.0),
            Oklch(0.96, 0.017034928185669117, 0.0),
            Oklch(0.94, 0.028443494145546925, 0.0),
            Oklch(0.92, 0.045598951314904040, 0.0),
            Oklch(0.90, 0.075893775147700580, 0.0),
            Oklch(0.86, 0.092180343550134080, 0.0),
            Oklch(0.82, 0.090829306833127860, 0.0),
            Oklch(0.77, 0.085831962418407940, 0.0),
            Oklch(0.70, 0.082183010482919910, 0.0),
            Oklch(0.00, 0.0, 0.0),
        )

        override val accent2 = listOf(
            Oklch(1.00, 0.0, 0.0),
            Oklch(0.98, 0.008370689307590670, 0.0),
            Oklch(0.96, 0.009059491335610874, 0.0),
            Oklch(0.94, 0.009909348782867190, 0.0),
            Oklch(0.92, 0.011010892700061553, 0.0),
            Oklch(0.90, 0.013094915429582050, 0.0),
            Oklch(0.86, 0.015158985285369884, 0.0),
            Oklch(0.82, 0.017719219960555296, 0.0),
            Oklch(0.77, 0.022205251914217298, 0.0),
            Oklch(0.70, 0.033225645422094850, 0.0),
            Oklch(0.00, 0.0, 0.0),
        )

        override val accent3 = listOf(
            Oklch(1.00, 0.0, 0.0),
            Oklch(0.98, 0.012038466082258451, 0.0),
            Oklch(0.96, 0.014080682795396000, 0.0),
            Oklch(0.94, 0.015527627146733855, 0.0),
            Oklch(0.92, 0.017620903781428650, 0.0),
            Oklch(0.90, 0.019885130697187350, 0.0),
            Oklch(0.86, 0.023033803858636220, 0.0),
            Oklch(0.82, 0.028825286054485977, 0.0),
            Oklch(0.77, 0.037187494544717540, 0.0),
            Oklch(0.70, 0.057187980784448180, 0.0),
            Oklch(0.00, 0.0, 0.0),
        )
    }
}