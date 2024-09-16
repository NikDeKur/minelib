package dev.nikdekur.minelib.inventory

enum class InventorySlot(val index: Int) {
    // ARMOR
    HELMET(103),
    CHESTPLATE(102),
    LEGGINGS(101),
    BOOTS(100),


    // Inventory
    R4_1(9),
    R4_2(10),
    R4_3(11),
    R4_4(12),
    R4_5(13),
    R4_6(14),
    R4_7(15),
    R4_8(16),
    R4_9(17),

    R3_1(18),
    R3_2(19),
    R3_3(20),
    R3_4(21),
    R3_5(22),
    R3_6(23),
    R3_7(24),
    R3_8(25),
    R3_9(26),

    R2_1(27),
    R2_2(28),
    R2_3(29),
    R2_4(30),
    R2_5(31),
    R2_6(32),
    R2_7(33),
    R2_8(34),
    R2_9(35),

    R1_1(0),
    R1_2(1),
    R1_3(2),
    R1_4(3),
    R1_5(4),
    R1_6(5),
    R1_7(6),
    R1_8(7),
    R1_9(8),


    // HotBat
    HB_1(0),
    HB_2(1),
    HB_3(2),
    HB_4(3),
    HB_5(4),
    HB_6(5),
    HB_7(6),
    HB_8(7),
    HB_9(8),


    // Hands
    MAIN_HAND(-1),
    OFF_HAND(-106)

    ;

    val isHand
        get() = this == MAIN_HAND || this == OFF_HAND

    val isArmor
        get() = this == HELMET || this == CHESTPLATE || this == LEGGINGS || this == BOOTS

    val isHotBar
        get() = this == HB_1 || this == HB_2 || this == HB_3 || this == HB_4 || this == HB_5 || this == HB_6 || this == HB_7 || this == HB_8 || this == HB_9
}
