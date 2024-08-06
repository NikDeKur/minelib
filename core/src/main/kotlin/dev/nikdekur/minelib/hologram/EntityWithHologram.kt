package dev.nikdekur.minelib.hologram

import dev.nikdekur.minelib.pentity.PersonalEntity

interface EntityWithHologram : PersonalEntity {

    val hologram: Hologram
    val entity: PersonalEntity
}