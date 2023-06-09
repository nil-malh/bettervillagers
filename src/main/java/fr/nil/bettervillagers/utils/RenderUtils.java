package fr.nil.bettervillagers.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;


public class RenderUtils {
    /**
     * Reference: {@link net.minecraft.client.render.entity.EntityRenderer}
     */


    public static void renderLabelIfPresent(Entity entity, Text text, MatrixStack matrices, EntityRenderDispatcher dispatcher,VertexConsumerProvider vertexConsumers, int light) {
        double d = dispatcher.getSquaredDistanceToCamera(entity);
        if (!(d > 4096.0)) {
            boolean bl = !entity.isSneaky();
            float f = entity.getHeight() + 0.5F;
            int i = "deadmau5".equals(text.getString()) ? -10 : 0;
            matrices.push();
            matrices.translate(0.0, (double)f, 0.0);
            matrices.multiply(dispatcher.getRotation());
            matrices.scale(-0.025F, -0.025F, 0.025F);
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
            int j = (int)(g * 255.0F) << 24;
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            float h = (float)(-textRenderer.getWidth(text) / 2);
            textRenderer.draw(text, h, (float)i, 553648127, false, matrix4f, vertexConsumers, bl, j, light);
            if (bl) {
                textRenderer.draw(text, h, (float)i, -1, false, matrix4f, vertexConsumers, false, 0, light);
            }

            matrices.pop();
        }
    }
    public static void renderTextOnEntity(Entity entity, Text text, MatrixStack matrices, EntityRenderDispatcher renderManager, VertexConsumerProvider vertexConsumerProvider) {
        MinecraftClient client = MinecraftClient.getInstance();

        double distance = renderManager.getSquaredDistanceToCamera(entity);
        System.out.println("distance to entity : " + distance);

        //#if MC >= 11500
        matrices.push();
        matrices.translate(0.0, entity.getHeight(), 0.0);
        matrices.multiply(renderManager.getRotation());
        matrices.scale(-0.025F, -0.025F, 0.025F);

        TextRenderer textRenderer = client.textRenderer;

        //#if MC >= 11600
        //$$ float renderX = (float) (-textRenderer.getWidth(text) / 2);
        //#else
        float renderX = (float) (textRenderer.getWidth(text.getString()) / 2);
        //#endif

        //#if MC >= 11500
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        textRenderer.draw(text.getString(), renderX, 0, 16777215, false, matrix4f, vertexConsumerProvider, true, 0, 15728880
        );
        matrices.pop();
    }



    /**
     * Reference: {@link net.minecraft.client.render.debug.DebugRenderer}
     */
    public static void drawBox(BlockPos pos, float red, float green, float blue, float alpha) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        if (camera.isReady()) {
            Vec3d vec3d = camera.getPos().negate();
            Box box = (new Box(pos)).offset(vec3d);
            drawBox(box, red, green, blue, alpha);
        }
    }

    public static void drawBox(Box box, float red, float green, float blue, float alpha) {
        drawBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha);

    }

    public static void drawBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        WorldRenderer.drawBox(bufferBuilder, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
        tessellator.draw();
    }

    public static void drawLine(Vec3d start, Vec3d end, float red, float green, float blue, float alpha) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        if (camera.isReady()) {
            Vec3d vec3d = camera.getPos().negate();

            drawLine(
                    start.add(vec3d).getX(),
                    start.add(vec3d).getY(),
                    start.add(vec3d).getZ(),
                    end.add(vec3d).getX(),
                    end.add(vec3d).getY(),
                    end.add(vec3d).getZ(),
                    red, green, blue, alpha
            );
        }
    }

    public static void drawLine(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha) {

        RenderSystem.lineWidth(3.0F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        bufferBuilder.vertex(minX, minY, minZ).color(red, green, blue, alpha).next();
        bufferBuilder.vertex(maxX, maxY, maxZ).color(red, green, blue, alpha).next();
        tessellator.draw();

        RenderSystem.lineWidth(1.0F);

    }
}