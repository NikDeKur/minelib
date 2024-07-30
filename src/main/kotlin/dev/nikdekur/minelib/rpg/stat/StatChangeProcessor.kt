package dev.nikdekur.minelib.rpg.stat

import dev.nikdekur.minelib.rpg.event.RPGStatChangeEvent

typealias StatChangeProcessor<T> = RPGStatChangeEvent<T>.() -> Unit