package com.elmakers.mine.bukkit.effect.builtin;

import com.elmakers.mine.bukkit.effect.EffectPlayer;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EffectVariable extends EffectPlayer {

    private Map<Double, Collection<EffectPlayer>> brightnessMap = new TreeMap<Double, Collection<EffectPlayer>>();

    public EffectVariable() {

    }

    @Override
    public void load(Plugin plugin, ConfigurationSection configuration) {
        super.load(plugin, configuration);

        // ... This seems kinda broken. I get a Map here if it's embedded in another section?
        Object testObject = configuration.get("brightness");
        if (testObject != null) {
            ConfigurationSection brightness = null;
            if (testObject instanceof Map) {
                Map<String, List<ConfigurationSection>> dataMap = (Map<String, List<ConfigurationSection>>)testObject;
                brightness = new MemoryConfiguration();
                for (Map.Entry<String, List<ConfigurationSection>> entry : dataMap.entrySet()) {
                    brightness.set(entry.getKey(), entry.getValue());
                }
            } else {
                brightness = configuration.getConfigurationSection("brightness");
            }
            if (brightness != null) {
                Collection<String> keys = brightness.getKeys(false);
                for (String key : keys) {
                    try {
                        double level = Double.parseDouble(key);
                        brightnessMap.put(level, EffectPlayer.loadEffects(plugin, brightness, key));
                    } catch (Exception ex) {

                    }
                }
            }
        }
    }

    public void play() {
        if (brightnessMap.size() > 0) {
            Color color = getColor1();
            double brightness = (double)color.getRed() / 255.0 + (double)color.getBlue() / 255.0 + (double)color.getGreen() / 255.0;
            for (Map.Entry<Double, Collection<EffectPlayer>> entry : brightnessMap.entrySet()) {
                if (brightness < entry.getKey()) {
                    for (EffectPlayer player : entry.getValue()) {
                        // Set scale
                        player.setScale(scale);

                        // Set material and color
                        player.setMaterial(getWorkingMaterial());
                        player.setColor(getColor1());
                        player.start(origin, originEntity == null ? null : originEntity.get(),
                                target, targetEntity == null ? null : targetEntity.get());
                    }
                    break;
                }
            }
        }
    }
}
