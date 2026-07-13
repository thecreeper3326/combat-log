package net.johnseagull.combatLog.mixin;

import net.johnseagull.combatLog.Figs;
import net.johnseagull.combatLog.accessor.LivingEntityAccessor;
import net.johnseagull.figManagerMC.FigManagerMC;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ServerPlayer.class)
public abstract class PlayerMixin extends LivingEntity implements LivingEntityAccessor {
    
    @Unique
    public int combatTime = 0;

    @Unique
    private int e = 0;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method="handleDamageEvent",at=@At("TAIL"))
    public void handleDamageEvent(DamageSource damageSource, CallbackInfo ci) {
        if (damageSource.getEntity() instanceof ServerPlayer me) {
            this.combatTime = 0;
        }
    }
    @Inject(method="tick",at=@At("TAIL"))
    public void tick(CallbackInfo ci) {
        Figs f = (Figs) FigManagerMC.FIGS;
        if (f.stationaryCooldown.value) {
            if (this.getSpeed() == 0.0f) {
                this.combatTime += 1;
            }
        } else {
            this.combatTime += 1;
        }
        e ++;
        if (e%10 == 0) {
            System.out.println("Combat Time: " + this.combatTime);
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
