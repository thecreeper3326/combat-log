package net.johnseagull.combatLog;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.johnseagull.combatLog.accessor.LivingEntityAccessor;
import net.johnseagull.figManagerMC.FigManagerMC;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CombatLog implements ModInitializer {
    Logger logger = LoggerFactory.getLogger("No combat log");
    @Override
    public void onInitialize() {
        FigManagerMC fm = new FigManagerMC();
        fm.init("combat_log","1.1",Figs.instance);

        Figs fg = (Figs) FigManagerMC.FIGS;
        if (fg.fire.value && fg.logonHealth.value<5) {
            logger.warn("Fire is enabled with a logon health of less than 5; the player will probably die");
        }
        //kind of funny how the core of the mod isnt even here, its in a mixin...
        ServerPlayerEvents.LEAVE.register(player -> {
            Figs f = (Figs) FigManagerMC.FIGS;
            if (f.enable.value) {
                if (((LivingEntityAccessor) player).getCombatTime() < f.cooldown.value) {
                    //someone did something bad...
                    f.$_badPlayers.value.put(player.getPlainTextName(), "1");
                } else {
                    f.$_badPlayers.value.remove(player.getPlainTextName());
                }
                FigManagerMC.save("combat_log");
            }
        });

        ServerPlayerEvents.JOIN.register(player -> {
            Figs f = (Figs) FigManagerMC.FIGS;
            if (f.enable.value) {
                ((LivingEntityAccessor) player).setCombatTime(f.cooldown.value);
                if (f.$_badPlayers.value.containsKey(player.getPlainTextName())) {
                    if (f.modifyHealth.value) {
                        player.setHealth(f.logonHealth.value);
                    }
                    if (f.poisonEffect.value) {
                        player.addEffect(new MobEffectInstance(MobEffects.POISON, f.effectDuration.value, 5));
                    }
                    if (f.hungerEffect.value) {
                        player.addEffect(new MobEffectInstance(MobEffects.HUNGER, f.effectDuration.value, 5));
                    }
                    if (f.slownessEffect.value) {
                        player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, f.effectDuration.value, 2));
                    }
                    if (f.modifyHunger.value) {
                        player.getFoodData().setFoodLevel(f.logonHunger.value);
                    }
                    if (f.drainSaturation.value) {
                        player.getFoodData().setSaturation(0f);
                    }
                    if (f.blindnessEffect.value) {
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, f.effectDuration.value, 5));
                    }
                    if (f.noGravity.value) {
                        if (Math.random() < f.gravityChance.value)
                            player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, f.effectDuration.value, 255));
                    }
                    if (f.nausea.value) {
                        if (Math.random() < f.nauseaChance.value)
                            player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, f.effectDuration.value, 255));
                    }
                    if (f.fire.value) {
                        if (Math.random() < f.fireChance.value) player.setRemainingFireTicks(100);
                    }

                }
            }
        });
    }
}
