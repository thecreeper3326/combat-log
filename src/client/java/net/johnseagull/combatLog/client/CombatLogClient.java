package net.johnseagull.combatLog.client;

import net.johnseagull.combatLog.Figs;
import net.fabricmc.api.ClientModInitializer;
import net.johnseagull.figManagerClient.FigManagerClient;

public class CombatLogClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FigManagerClient g = new FigManagerClient();
        g.init(Figs.instance,0.5f);
    }
}
