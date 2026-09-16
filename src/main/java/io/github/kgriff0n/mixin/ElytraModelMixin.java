package io.github.kgriff0n.mixin;

import net.minecraft.client.model.object.equipment.ElytraModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ElytraModel.class)
public class ElytraModelMixin {

    @Shadow @Final private ModelPart leftWing;

    @Shadow @Final private ModelPart rightWing;

    @Unique
    double x, y, z;

    @Unique
    double angle = 10;

    @ModifyArgs(
            method = "createLayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/geom/PartPose;offsetAndRotation(FFFFFF)Lnet/minecraft/client/model/geom/PartPose;"
            )
    )
    private static void modifyModelTransformArgs(Args args) {
        float originalX = args.get(0);
        if (originalX == 5.0F) { // left wing
            args.set(0, 5.8F);
        } else if (originalX == -5.0F) { // right wing
            args.set(0, -5.8F);
        }
    }

    @Inject(at = @At("TAIL"), method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V")
    private void modifyElytraRotation(HumanoidRenderState bipedEntityRenderState, CallbackInfo ci) {
        if (!bipedEntityRenderState.isFallFlying && !bipedEntityRenderState.isCrouching) {
            if (x != bipedEntityRenderState.x || y != bipedEntityRenderState.y || z != bipedEntityRenderState.z) {
                if (angle < 50) angle = increase(angle);
            } else {
                if (angle > 10) angle -= 0.5;
            }
            this.leftWing.xRot = this.rightWing.xRot = (float) Math.toRadians(angle);
            this.leftWing.yRot = (float) Math.toRadians(angle - 10) * 0.2f;
            this.rightWing.yRot = -(float) Math.toRadians(angle - 10) * 0.2f;
        }
        x = bipedEntityRenderState.x;
        y = bipedEntityRenderState.y;
        z = bipedEntityRenderState.z;
    }

    @Unique
    private double increase(double value) {
        double step;
        if (value < 15) {
            step = 0.1;
        } else if (value < 40) {
            step = 0.5;
        } else {
            step = 0.1;
        }
        return value + step;
    }
}