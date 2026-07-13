package net.johnseagull.combatLog;

import net.johnseagull.figManager.Fig.*;
import net.johnseagull.figManager.FigGroup;
import net.johnseagull.figManagerMC.DividerFig;
import net.minecraft.ChatFormatting;

import java.util.List;


public class Figs {
    public static Figs instance = new Figs();
    public FigGroup cd = new FigGroup(List.of("enable","","cooldown","stationaryCooldown"),2,true,0.5f);

    public BooleanFig enable = new BooleanFig("Enable mod","enables the fucntions of this mod", true);
    public IntFig cooldown = new IntFig("Cooldown","How long a user must wait before they can log off and not receive punishment",200,0,Integer.MAX_VALUE);
    public BooleanFig stationaryCooldown = new BooleanFig("Stationary Cooldown","Only progresses the cooldown when the player is not moving. This can help prevent player from just running and then logging off.",false);
    public DividerFig punishments = new DividerFig("Punishments", ChatFormatting.WHITE,false,false,false);
    public FigGroup effects = new FigGroup(List.of("modifyHealth","logonHealth","modifyHunger","logonHunger","drainSaturation","poisonEffect","hungerEffect","slownessEffect","effectDuration"),2,true,0.5f);
    public BooleanFig modifyHealth = new BooleanFig("Modify health","Set the player's health to a certain amount after re-joining",true);
    public IntFig logonHealth = new IntFig("Health","How much health the player should have on re-join after combat-logging",5,0,20);
    public BooleanFig modifyHunger = new BooleanFig("Modify hunger","Set the player's hunger to a certain amount after re-joining",true);
    public IntFig logonHunger = new IntFig("Hunger","How much hunger the player should have upon re-joining",6,0,20);
    public BooleanFig drainSaturation = new BooleanFig("Drain Saturation","Remove all saturation so the player cannot naturally regenerate",true);
    public BooleanFig poisonEffect = new BooleanFig("Poison Effect","Give the player poison",true);
    public BooleanFig hungerEffect = new BooleanFig("Hunger Effect","Give the player hunger",true);
    public BooleanFig slownessEffect = new BooleanFig("Slowness Effect","Give the player slowness",false);
    public IntFig effectDuration = new IntFig("Effect duration","How long poision/hunger/slowness should last if enabled" ,200,0,5000);
    public DividerFig fun = new DividerFig("Extra punishments",ChatFormatting.WHITE,false,false,false);
    public FigGroup f = new  FigGroup(List.of("noGravity","gravityChance","nausea","nauseaChance","fire","fireChance"),2,true,0.5f);
    public BooleanFig noGravity = new BooleanFig("No gravity","Make the player zoom up into the stratosphere and come flying back down",false);
    public FloatFig gravityChance = new FloatFig("Chance","Chance (0.0 - 1.0) for the player to receive levitation",0.5f,0.0f,1.0f);
    public BooleanFig nausea = new BooleanFig("Nausea","Give the player nausea at a very high potency",false);
    public FloatFig nauseaChance = new FloatFig("Chance","Chance (0.0 - 1.0) for the player to receive nausea",0.5f,0.0f,1.0f);
    public BooleanFig fire = new BooleanFig("Fire","Set the player ablaze without warning",false);
    public FloatFig fireChance = new FloatFig("Chance","Chance (0.0 - 1.0) for the player to be set on fire",0.5f,0.0f,1.0f);
    public MapFig $_badPlayers = new MapFig("bad_players","environment value - do no change",64,2,"int","playerName","flag");
}
