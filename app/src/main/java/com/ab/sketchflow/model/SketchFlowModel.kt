package com.ab.sketchflow.model


data class ColorSet(val colors: List<Long>)


val colorSet1 = ColorSet(
    listOf(
        0xFF000000,
        0xFFC70039,
        0xFF900C3F,
        0xFF581845,
        0xFF2C3E50,
        0xFF2980B9,
        0xFF16A085,
        0xFF27AE60
    )
)


val colorsSet2 = ColorSet(
    listOf(
        0xFFD35400,
        0xFFE74C3C,
        0xFF9B59B6,
        0xFF8E44AD,
        0xFF3498DB,
        0xFF1ABC9C,
        0xFF2ECC71,
        0xFF27AE60
    )
)


val colorSet3 = ColorSet(
    listOf(
        0xFFF39C12,
        0xFFF1C40F,
        0xFFE67E22,
        0xFFD35400,
        0xFFE74C3C,
        0xFFC0392B,
        0xFF8E44AD,
        0xFF9B59B6
    )
)


val colorSet4 = ColorSet(
    listOf(
        0xFF34495E,
        0xFF2E4053,
        0xFF283747,
        0xFF212F3C,
        0xFF1B2631,
        0xFFD35400,
        0xFFE74C3C,
        0xFF27AE60
    )
)



val colorSet5 = ColorSet(
    listOf(
        0xFF3498DB,
        0xFF2980B9,
        0xFF2980B9,
        0xFF1ABC9C,
        0xFF16A085,
        0xFF27AE60,
        0xFF2ECC71,
        0xFF3498DB
    )
)


val colorSet6 = ColorSet(
    listOf(
        0xFF9B59B6,
        0xFF8E44AD,
        0xFF2980B9,
        0xFF3498DB,
        0xFFE74C3C,
        0xFFC0392B,
        0xFFD35400,
        0xFFF39C12
    )
)

val colorSet7 = ColorSet(
    listOf(
        0xFF16A085,
        0xFF1ABC9C,
        0xFF27AE60,
        0xFF2ECC71,
        0xFF3498DB,
        0xFF2980B9,
        0xFF8E44AD,
        0xFF9B59B6
    )
)


val colorSet8 = ColorSet(
    listOf(
        0xFFD35400,
        0xFFE74C3C,
        0xFFC0392B,
        0xFF3498DB,
        0xFF34495E,
        0xFF2E4053,
        0xFF283747,
        0xFF212F3C
    )
)


val colorSet9 = ColorSet(
    listOf(
        0xFF2980B9,
        0xFF16A085,
        0xFF1ABC9C,
        0xFF27AE60,
        0xFF2ECC71,
        0xFF8E44AD,
        0xFF9B59B6,
        0xFF34495E
    )
)



val colorSet10 = ColorSet(
    listOf(
        0xFFF1C40F,
        0xFFE67E22,
        0xFFD35400,
        0xFF3498DB,
        0xFF16A085,
        0xFFE74C3C,
        0xFFC0392B,
        0xFF3498DB
    )
)

val colorSets = listOf<ColorSet>(colorSet1, colorsSet2, colorSet3, colorSet4,
                                colorSet5, colorSet6, colorSet7, colorSet8,
                                colorSet9, colorSet10)
