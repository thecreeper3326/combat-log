package johnseagull.combatLog.client;

import johnseagull.combatLog.Figs;
import net.fabricmc.api.ClientModInitializer;
import johnseagull.figManagerClient.FigManagerClient;

public class CombatLogClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FigManagerClient g = new FigManagerClient();
        g.init(Figs.instance,0.5f);
    }
}
