package net.johnseagull.combatLog.mixin;

import net.johnseagull.combatLog.Figs;
import net.johnseagull.combatLog.accessor.LivingEntityAccessor;
import net.johnseagull.figManagerMC.FigManagerMC;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements LivingEntityAccessor {
    @Shadow
    private float speed;

    @Shadow
    public abstract float getSpeed();

    @Unique
    private int combatTime = 0;

    @Unique
    private int e = 0;

    @Unique
    private Vec3 l = Vec3.ZERO;

    @Inject(method="hurtServer",at=@At("TAIL"))
    public void handleDamageEvent(ServerLevel serverLevel, DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {

        if (damageSource.getEntity() instanceof ServerPlayer) {
            this.combatTime = 0;
        }
        if (damageSource.type().toString().contains("player")) {
            this.combatTime = 0;
        }
    }
    @Inject(method="tick",at=@At("TAIL"))
    public void tick(CallbackInfo ci) {
        e++;
        if (e > 20) {
            if ((LivingEntity)(Object)this instanceof ServerPlayer) {
                Figs f = (Figs) FigManagerMC.FIGS;
                ServerPlayer player = (ServerPlayer)(Object)this;
                Vec3 currentPos = player.position();

                if (f.stationaryCooldown.value) {
                    if (currentPos.distanceTo(this.l) < 0.01) {
                        this.combatTime += 1;
                    }
                } else {
                    this.combatTime += 1;
                }
                this.l = currentPos;
            }
            e=0;
        }

    }

    @Override
    public int getCombatTime() {
        return combatTime;
    }

    @Override
    public void setCombatTime(int time) {
        combatTime = time;
    }
}
