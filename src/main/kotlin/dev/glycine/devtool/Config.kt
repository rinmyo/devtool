package dev.glycine.devtool

import hazae41.minecraft.kutils.bukkit.PluginConfigFile

object Config: PluginConfigFile("config"){
    var developers by stringList("developers")
}