package com.pcdefender;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.village.VillageGossipType;
import net.minecraft.village.VillagerGossips;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NoNegativeGossips implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("nonegativegossips");

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("RemoveNegativeGossip")
					.executes(context -> {
						ServerPlayerEntity player = context.getSource().getPlayer();
						if (player == null) {
							context.getSource().sendError(Text.of("Player is not available."));
							return 0;
						}

						ServerWorld world = player.getServerWorld();
						List<VillagerEntity> villagers = world.getEntitiesByClass(VillagerEntity.class, new Box(player.getBlockPos()).expand(50), villager -> true); // Change 50 to your desired radius

						int affectedVillagers = 0;
						for (VillagerEntity villager : villagers) {
							VillagerGossips gossip = villager.getGossip();
							gossip.remove(VillageGossipType.MAJOR_NEGATIVE);
							gossip.remove(VillageGossipType.MINOR_NEGATIVE);
							affectedVillagers++;
						}

						MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal(affectedVillagers + " villagers' negative gossips removed."));
						return 1;
					}));
		});
	}
}