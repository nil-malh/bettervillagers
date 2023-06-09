package fr.nil.bettervillagers.mixins.render;


import fr.nil.bettervillagers.Core;
import fr.nil.bettervillagers.utils.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.village.TradeOffer;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;


@Mixin(LivingEntityRenderer.class)
public abstract class VillagerEntityRendererMixin<T extends LivingEntity> extends EntityRenderer<T> {

    protected VillagerEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }
    //Todo text Coloring on Entity.
    private Formatting getFormattingForTrade(TradeOffer offer, Map.Entry<Enchantment, Integer> entry) {
        int level = entry.getValue();
        int cost = offer.getAdjustedFirstBuyItem().getCount();
        int minCost = 2 + 3 * level;
        int maxCost = minCost + 4 + level * 10;

        if (entry.getKey().isTreasure()) {
            minCost *= 2;
            maxCost *= 2;
        }

        if (cost == minCost) {
            return Formatting.GOLD;
        } else if (cost <= (maxCost - minCost) / 3 + minCost) {
            return Formatting.GREEN;
        } else if (cost <= (maxCost - minCost) / 3 * 2 + minCost) {
            return Formatting.WHITE;
        } else {
            return Formatting.RED;
        }
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("RETURN"))

    private void render$bettervillagers(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (!(livingEntity instanceof VillagerEntity))
            return;
        int renderDistance = MinecraftClient.getInstance().options.getViewDistance().getValue();
        if (livingEntity.squaredDistanceTo(MinecraftClient.getInstance().player) > renderDistance * renderDistance) return;

        VillagerEntity villager = (VillagerEntity) livingEntity;

        Text text = null;

        for (TradeOffer offer : villager.getOffers()) {
            ItemStack sellItem = offer.getSellItem();
            if (sellItem.getItem() instanceof EnchantedBookItem) {

                int price = offer.getOriginalFirstBuyItem().getCount();
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(sellItem);
                Map.Entry<Enchantment, Integer> enchantment = enchantments.entrySet().iterator().next();
                text = enchantment
                        .getKey()
                        .getName(enchantment.getValue());
                text = text.copy().append(" | " + price);

            }
        }
        if (text != null) {
            Core.log(Level.DEBUG, "Rendering villager trade onto it (Villager UUID : " + villager.getUuidAsString()+")");
            RenderUtils.renderLabelIfPresent(villager, text, matrixStack, this.dispatcher, vertexConsumerProvider, 16777215);

        }
    }


}
