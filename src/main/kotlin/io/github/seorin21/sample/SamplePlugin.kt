package io.github.seorin21.sample

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class SamplePlugin: JavaPlugin() {
    override fun onEnable() {
        Bukkit.getLogger().info("SamplePlugin enabled!")
    }
}