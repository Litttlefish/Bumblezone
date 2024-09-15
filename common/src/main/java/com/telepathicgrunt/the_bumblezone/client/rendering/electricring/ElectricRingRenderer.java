package com.telepathicgrunt.the_bumblezone.client.rendering.electricring;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.entities.nonliving.ElectricRingEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ElectricRingRenderer<M extends EntityModel<ElectricRingEntity>>
        extends EntityRenderer<ElectricRingEntity>
        implements RenderLayerParent<ElectricRingEntity, M>
{
    static final ResourceLocation SKIN_1 = ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "textures/entity/electric_ring/electric_ring_1.png");
    static final ResourceLocation SKIN_2 = ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "textures/entity/electric_ring/electric_ring_2.png");
    static final ResourceLocation SKIN_3 = ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "textures/entity/electric_ring/electric_ring_3.png");
    protected final ElectricRingModel<ElectricRingEntity> model;
    protected final List<RenderLayer<ElectricRingEntity, M>> layers = Lists.newArrayList();

    public ElectricRingRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ElectricRingModel<>(context.bakeLayer(ElectricRingModel.LAYER_LOCATION));
    }

    protected final boolean addLayer(RenderLayer<ElectricRingEntity, M> renderLayer) {
        return this.layers.add(renderLayer);
    }

    @Override
    public M getModel() {
        return (M) this.model;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void render(ElectricRingEntity ringEntity, float yRot, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        packedLight = LightTexture.FULL_BRIGHT;

        float angleExtra = -180f;
        int interval = 3;
        int state = ringEntity.tickCount / interval;

        float spinSpeed = 15f;
        float angle = (ringEntity.tickCount + partialTicks) * spinSpeed;

        angle += (state * angleExtra);

        poseStack.pushPose();
        float rotationLerp = Mth.lerp(partialTicks, ringEntity.xRotO, ringEntity.getXRot());
        float scale;
        if (ringEntity.disappearingTime >= 0) {
            scale = Math.min((ringEntity.disappearingTime - partialTicks) / ElectricRingEntity.DISAPPERING_TIMESPAN, 1.0f);
        }
        else {
            scale = Math.min((ringEntity.tickCount + partialTicks) / ElectricRingEntity.APPEARING_TIMESPAN, 1.0f);
        }

        poseStack.scale(-scale, -scale, scale);
        poseStack.translate(0.0f, -(ringEntity.getEyeHeight()) - (1.5f - (scale * 1.5f)), 0.0f);
        poseStack.mulPose(Axis.YN.rotationDegrees(180.0f - ringEntity.getYRot()));
        poseStack.mulPose(Axis.XN.rotationDegrees(180.0f - ringEntity.getXRot()));
        poseStack.mulPose(Axis.ZN.rotationDegrees(angle % 360));
        this.model.prepareMobModel(ringEntity, 0, 0, partialTicks);
        ((EntityModel)this.model).setupAnim(ringEntity, 0, 0, 0, 0, rotationLerp);
        Minecraft minecraft = Minecraft.getInstance();
        boolean bodyVisible = this.isBodyVisible(ringEntity);
        boolean hidden = !bodyVisible && !ringEntity.isInvisibleTo(minecraft.player);
        boolean glowing = minecraft.shouldEntityAppearGlowing(ringEntity);
        RenderType renderType = this.getRenderType(ringEntity, bodyVisible, hidden, glowing);
        if (renderType != null) {
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(renderType);
            this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, 0, 0xFFFFFFFF);
        }
        if (!ringEntity.isSpectator()) {
            for (RenderLayer<ElectricRingEntity, M> renderLayer : this.layers) {
                renderLayer.render(poseStack, multiBufferSource, packedLight, ringEntity, 0, 0, partialTicks, 0, 0, rotationLerp);
            }
        }
        poseStack.popPose();
        super.render(ringEntity, yRot, partialTicks, poseStack, multiBufferSource, packedLight);
    }

    @Nullable
    protected RenderType getRenderType(ElectricRingEntity ringEntity, boolean bodyVisible, boolean hidden, boolean glowing) {
        ResourceLocation resourceLocation = this.getTextureLocation(ringEntity);
        if (bodyVisible) {
            return this.model.renderType(resourceLocation);
        }
        if (glowing) {
            return RenderType.outline(resourceLocation);
        }
        return null;
    }

    protected boolean isBodyVisible(ElectricRingEntity ringEntity) {
        return !ringEntity.isInvisible();
    }

    @Override
    public ResourceLocation getTextureLocation(ElectricRingEntity ringEntity) {
        int interval = 3;
        int state = ringEntity.tickCount % (interval * 3);

        if (state < interval) {
            return SKIN_1;
        }
        else if (state < interval * 2) {
            return SKIN_2;
        }
        else {
            return SKIN_3;
        }
    }
}