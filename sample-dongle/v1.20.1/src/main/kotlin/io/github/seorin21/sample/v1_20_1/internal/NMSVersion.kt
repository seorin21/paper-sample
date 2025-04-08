package io.github.seorin21.sample.v1_20_1.internal

import org.bukkit.Bukkit
import io.github.seorin21.sample.internal.Version
import net.minecraft.server.MinecraftServer
import org.bukkit.craftbukkit.v1_20_R1.CraftServer

class NMSVersion : Version {
    override val value: String
        get() {
            val server = (Bukkit.getServer() as CraftServer).server
            return server.javaClass.canonicalName + " - " + server.serverVersion + " " + MinecraftServer::class.java.fields[0].name
        }
}