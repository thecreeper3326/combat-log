package johnseagull.combatLog;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import johnseagull.combatLog.accessor.LivingEntityAccessor;
import johnseagull.figManagerMC.FigManagerMC;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class CombatLog implements ModInitializer {
    Logger logger = LoggerFactory.getLogger("No combat log");
    private static HashMap<UUID, Integer> size = new HashMap<>();
    private static HashMap<UUID, Integer> gravity = new HashMap<>();

    private void cd(MinecraftServer server, HashMap<UUID, Integer> m, Holder<Attribute> a, double b) {
        Iterator<Map.Entry<UUID, Integer>> it_g = m.entrySet().iterator();
        while (it_g.hasNext()) {
            Map.Entry<UUID, Integer> entry = it_g.next();
            int r = entry.getValue() - 1;
            if (r <= 0) {
                ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
                if (player != null) {
                    AttributeInstance s = player.getAttribute(a);
                    if (s != null) s.setBaseValue(b);
                }
                it_g.remove();
            } else {
                entry.setValue(r);
            }
        }
    }
    @Override
    public void onInitialize() {
        FigManagerMC fm = new FigManagerMC();
        fm.init("combat_log","1.2.2",Figs.instance);

        Figs fg = (Figs) FigManagerMC.FIGS;
        if (fg.fire.value && fg.logonHealth.value<5) {
            logger.warn("Fire is enabled with a logon health of less than 5; the player will probably die");
        }
        //kind of funny how the core of the mod isnt even here, its in a mixin...
        ServerPlayerEvents.LEAVE.register(player -> {
            Figs f = (Figs) FigManagerMC.FIGS;
            if (f.enable.value) {
                if (((LivingEntityAccessor) player).getCombatTime() < f.cooldown.value) {
                    for (ServerPlayer p : player.level().getServer().getPlayerList().getPlayers()) {
                            String msg = f.leaveMessage.value.replace("%N", player.getPlainTextName());
                            p.sendSystemMessage(Component.literal(msg), false);
                    }
                    //someone did something bad...
                    f.$_badPlayers.value.put(player.getPlainTextName()+"_"+player.getUUID(), String.valueOf(((LivingEntityAccessor) player).getCombatTime()));
                } else {
                    f.$_badPlayers.value.remove(player.getPlainTextName()+"_"+player.getUUID());
                }
                FigManagerMC.save("combat_log");
            }
        });
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            cd(server, size, Attributes.SCALE,1);
            cd(server, gravity, Attributes.GRAVITY,0.08d);
            Figs f = (Figs) FigManagerMC.FIGS;

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                int time  = ((LivingEntityAccessor) player).getCombatTime();
                if (time < f.cooldown.value) {
                    String msg = f.message.value.replace("%T", String.valueOf(f.cooldown.value - time)).replace("%S", String.valueOf((Math.round((float) (f.cooldown.value - time) / 20))));
                    player.sendSystemMessage(Component.literal(msg), true);
                }
            }
        });

        ServerPlayerEvents.JOIN.register(player -> {

            Figs f = (Figs) FigManagerMC.FIGS;
            player.getAttribute(Attributes.GRAVITY).setBaseValue(0.08F);
            player.getAttribute(Attributes.SCALE).setBaseValue(1);
            if (f.enable.value) {
                ((LivingEntityAccessor) player).setCombatTime(f.cooldown.value);
                if (f.$_badPlayers.value.containsKey(player.getPlainTextName()+"_"+player.getUUID())) {
                    for (ServerPlayer p : player.level().getServer().getPlayerList().getPlayers()) {
                        String msg = f.humiliation.value.replace("%N", player.getPlainTextName());
                        p.sendSystemMessage(Component.literal(msg), false);
                    }
                    player.sendSystemMessage(Component.literal(f.returnMessage.value), false);
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
                        if (Math.random() < f.gravityChance.value) player.getAttribute(Attributes.GRAVITY).setBaseValue(-2);
                        gravity.put(player.getUUID(),f.effectDuration.value);
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

                    f.$_badPlayers.value.remove(player.getPlainTextName()+"_"+player.getUUID());
                }
            }
        });
    }

}
