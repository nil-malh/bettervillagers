package fr.nil.bettervillagers.mixins.client;

import fr.nil.bettervillagers.utils.packet.PacketUtils;
import fr.nil.bettervillagers.utils.packet.PacketsEnum;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Client side VillagerEntityMixin
@Environment(EnvType.CLIENT)
@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin {
    private VillagerProfession oldVillagerProfession;

    /*
     * ---------------------------------------------------------
     *    Request Villager - profession change on client side
     * ---------------------------------------------------------
     */

    @Inject(method = "tick", at = @At(value = "RETURN"))
    private void requestVillagerWhenProfessionChange(CallbackInfo ci) {
        VillagerEntity villager = (VillagerEntity) (Object) this;
        VillagerProfession currentVillagerProfession = villager.getVillagerData().getProfession();
        // Check if the player is in-game
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.player != null && minecraftClient.world != null) {
            if (oldVillagerProfession != currentVillagerProfession) {
                ClientPlayNetworking.send(PacketsEnum.PACKET_REQUEST_ENTITY.getIdentifier(), PacketUtils.requestEntityUpdateFromServer(villager));
                oldVillagerProfession = currentVillagerProfession;

            }
        }
    }

}