package com.github.kenzisiaufandi;

import org.bukkit.plugin.java.JavaPlugin;
import com.github.kenzisiaufandi.commands.AdminCommand;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

public final class ExamplePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        Lamp<BukkitCommandActor> lamp = BukkitLamp.builder(this).dependency(ExamplePlugin.class, this).build();

        lamp.register(new AdminCommand());
    }

    @Override
    public void onDisable() {}
}
