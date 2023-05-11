package fr.nil.bettervillagers.mixins.server;


import fr.nil.bettervillagers.Core;
import fr.nil.bettervillagers.server.CoreServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "remove", at = @At("HEAD"))
    private void onPlayerLogout$bettervillagers(ServerPlayerEntity player, CallbackInfo ci) {
        Core.log(Level.DEBUG, "Player " + player.getName().getString() + " has logged out.");
        CoreServer.moddedPlayers.remove(player);
    }

}
