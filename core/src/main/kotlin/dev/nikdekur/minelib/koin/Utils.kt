package dev.nikdekur.minelib.koin

import org.koin.core.Koin
import org.koin.core.module.Module
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.module

/** Wrapper for [org.koin.dsl.module] that immediately loads the module for the current [Koin] instance. **/
fun loadModule(
    createdAtStart: Boolean = false,
    moduleDeclaration: ModuleDeclaration,
): Module {
    val moduleObj = module(createdAtStart, moduleDeclaration)

    MineLibKoinContext.loadKoinModules(moduleObj)

    return moduleObj
}

/** Retrieve the current [Koin] instance. **/
fun getKoin(): Koin = MineLibKoinContext.get()