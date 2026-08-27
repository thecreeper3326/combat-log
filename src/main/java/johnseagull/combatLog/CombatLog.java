package johnseagull.combatLog;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import johnseagull.combatLog.accessor.LivingEntityAccessor;
import johnseagull.figManagerMC.FigManagerMC;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class CombatLog implements ModInitializer {
    Logger logger = LoggerFactory.getLogger("No combat log");
    private static HashMap<UUID, Integer> size = new HashMap<>();
    @Override
    public void onInitialize() {
        FigManagerMC fm = new FigManagerMC();
        fm.init("combat_log","1.2.1",Figs.instance);

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
        ServerTickEvents.END_SERVER_TICK.register(server -> {

            Iterator<Map.Entry<UUID, Integer>> iterator = size.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<UUID,Integer> entry = iterator.next();
                UUID s = entry.getKey();
                int i = entry.getValue();

                if (i <= 1) {
                    ServerPlayer player = server.getPlayerList().getPlayer(s);
                    player.getAttribute(Attributes.SCALE).setBaseValue(1);
                }
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
                        if (Math.random() < f.gravityChance.value) player.getAttribute(Attributes.GRAVITY).setBaseValue(-5);
                    }
                    if (f.nausea.value) {
                        if (Math.random() < f.nauseaChance.value)
                            player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, f.effectDuration.value, 255));
                    }
                    if (f.fire.value) {
                        if (Math.random() < f.fireChance.value) player.setRemainingFireTicks(100);
                    }
                    if (f.big.value) {
                        AttributeInstance s = player.getAttribute(Attributes.SCALE);
                        s.setBaseValue(f.bigScale.value);
                        size.put(player.getUUID(),f.effectDuration.value);
                    }

                }
            }
        });
    }
}
