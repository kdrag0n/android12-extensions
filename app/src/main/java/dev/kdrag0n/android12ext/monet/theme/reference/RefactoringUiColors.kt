package dev.kdrag0n.android12ext.monet.theme.reference

import dev.kdrag0n.android12ext.monet.colors.Srgb
import dev.kdrag0n.android12ext.monet.theme.ColorScheme

object RefactoringUiColors {
    // https://www.refactoringui.com/previews/building-your-color-palette
    object GrayPurple : ColorScheme() {
        override val neutral1 = mapOf(
            0    to Srgb(0xffffff),
            10   to Srgb(0xfbfcfd), /* extrapolated with CIE LCh */
            50   to Srgb(0xfafbfc), /* extrapolated with CIE LCh */
            100  to Srgb(0xf8f9fa),
            200  to Srgb(0xdfe5ea),
            300  to Srgb(0xd4dce4),
            400  to Srgb(0xcad2d9),
            500  to Srgb(0xadbdcb),
            600  to Srgb(0x929fb1),
            700  to Srgb(0x6e7a8a),
            800  to Srgb(0x404b5a),
            900  to Srgb(0x202833),
            1000 to Srgb(0x000000),
        )
        override val neutral2 = neutral1

        override val accent1 = mapOf(
            0    to Srgb(0xffffff),
            10   to Srgb(0xfbfcff), /* extrapolated with CIE LCh */
            50   to Srgb(0xf4f7ff), /* extrapolated with CIE LCh */
            100  to Srgb(0xf0f4fe),
            200  to Srgb(0xd4def8),
            300  to Srgb(0x95aeed),
            400  to Srgb(0x758ce0),
            500  to Srgb(0x6175de),
            600  to Srgb(0x495dc6),
            700  to Srgb(0x3547a4),
            800  to Srgb(0x253585),
            900  to Srgb(0x1f2c6d),
            1000 to Srgb(0x000000),
        )
        override val accent2 = accent1
        override val accent3 = accent1
    }
}
