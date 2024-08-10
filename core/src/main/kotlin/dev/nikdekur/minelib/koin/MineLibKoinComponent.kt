package dev.nikdekur.minelib.koin

import org.koin.core.component.KoinComponent


interface MineLibKoinComponent : KoinComponent {
    override fun getKoin() = MineLibKoinContext.get()
}