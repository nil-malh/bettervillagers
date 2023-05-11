package fr.nil.bettervillagers.mixins.server;

import fr.nil.bettervillagers.Core;
import fr.nil.bettervillagers.server.CoreServer;
import fr.nil.bettervillagers.utils.packet.PacketUtils;
import fr.nil.bettervillagers.utils.packet.PacketsEnum;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.village.VillagerProfession;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


// Server side VillagerEntityMixin
@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin {
    @Shadow
    protected abstract void clearSpecialPrices();

    @Shadow
    protected abstract void beginTradeWith(PlayerEntity customer);

    /*
     * -----------------------------------
     *    Sync Villager - offers change
     * -----------------------------------
     */
    @Inject(method = "fillRecipes", at = @At("TAIL"))
    private void syncVillagerWhenFillRecipes(CallbackInfo ci) {
        Core.log(Level.DEBUG, "A villager has refilled his recipes.");
        VillagerEntity villager = (VillagerEntity) (Object) this;
        CoreServer.moddedPlayers.forEach(playerEntity -> {
            PacketByteBuf packet = PacketByteBufs.create();
            packet.writeInt(villager.getId());
            packet.writeNbt(villager.writeNbt(new NbtCompound()));
            ServerPlayNetworking.send(playerEntity, PacketsEnum.PACKET_SEND_ENTITY.getIdentifier(), packet);
            Core.log(Level.DEBUG, playerEntity.getEntityName() + " should've received an entity update packet");
        });

    }


    @Redirect(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/VillagerEntity;beginTradeWith(Lnet/minecraft/entity/player/PlayerEntity;)V"))
    private void interactMob$bettervillager(VillagerEntity instance, PlayerEntity player) {
        VillagerEntity villager = (VillagerEntity) (Object) this;

        ServerPlayNetworking.send((ServerPlayerEntity) player, PacketsEnum.PACKET_SEND_ENTITY.getIdentifier(), PacketUtils.sendEntityUpdateToClient(villager));

        //Rotate the villager to look at you when trading
        if (villager.isAiDisabled()) {
            villager.lookAtEntity(player, 0, 0);
            villager.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, player.getEyePos());
        }

        // Villager wand
        ItemStack item = player.getStackInHand(player.getActiveHand());
        if (!item.hasCustomName()) {
            beginTradeWith(player);
            return;
        }

        if (item.getItem().equals(Items.STICK) && item.hasCustomName()) {
            NbtCompound nbtCompound = item.getSubNbt("display");
            String itemDisplayName = Text.Serializer.fromJson(nbtCompound.getString("Name")).getString();

            if (player.getStackInHand(player.getActiveHand()).getItem().equals(Items.STICK) && itemDisplayName.equalsIgnoreCase("villager wand")) {
                if (villager.isAiDisabled()) {
                    player.sendMessage(Text.of(Core.PREFIX + "AI is now §2ON§r on this villager"));
                    villager.setAiDisabled(false);

                } else {
                    player.sendMessage(Text.of(Core.PREFIX + "AI is now §4OFF§r on this villager"));
                    villager.setAiDisabled(true);
                }

            } else {
                beginTradeWith(player);
            }
        } else {
            beginTradeWith(player);
        }
    }
     private VillagerProfession oldVillagerProfession;
    @Inject(method = "tick", at = @At(value = "RETURN"))
    private void requestVillagerWhenProfessionChange(CallbackInfo ci) {
        VillagerEntity villager = (VillagerEntity) (Object) this;
        VillagerProfession currentVillagerProfession = villager.getVillagerData().getProfession();
        if (oldVillagerProfession != currentVillagerProfession) {
            CoreServer.moddedPlayers.forEach(serverPlayerEntity -> {
                ServerPlayNetworking.send(serverPlayerEntity,PacketsEnum.PACKET_SEND_ENTITY.getIdentifier(), PacketUtils.sendEntityUpdateToClient(villager));
            });
            oldVillagerProfession = currentVillagerProfession;
        }
    }
}