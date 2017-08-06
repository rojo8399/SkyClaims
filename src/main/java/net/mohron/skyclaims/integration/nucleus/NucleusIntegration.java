/*
 * SkyClaims - A Skyblock plugin made for Sponge
 * Copyright (C) 2017 Mohron
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SkyClaims is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SkyClaims.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.mohron.skyclaims.integration.nucleus;

import io.github.nucleuspowered.nucleus.api.service.NucleusAFKService;
import io.github.nucleuspowered.nucleus.api.service.NucleusAPIMetaService;
import io.github.nucleuspowered.nucleus.api.service.NucleusHomeService;
import io.github.nucleuspowered.nucleus.api.service.NucleusKitService;
import net.mohron.skyclaims.PluginInfo;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.config.type.integration.NucleusConfig;
import net.mohron.skyclaims.integration.Version;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;

import java.time.Instant;

public class NucleusIntegration {

    private static final SkyClaims PLUGIN = SkyClaims.getInstance();
    private static NucleusAPIMetaService metaService;
    private static NucleusHomeService homeService;
    private static NucleusKitService kitService;
    private static NucleusAFKService afkService;

    @Listener
    public void onPostInitialization(GamePostInitializationEvent event) {
        metaService = PLUGIN.getGame().getServiceManager().provideUnchecked(NucleusAPIMetaService.class);
        if (Version.of(metaService.semanticVersion()).compareTo(Version.of(PluginInfo.NUCLEUS_VERSION)) >= 0) {
            PLUGIN.getLogger().info(String.format("Successfully integrated with Nucleus %s!", metaService.semanticVersion()));
        } else {
            PLUGIN.getLogger().info(String.format("Found Nucleus %s. SkyClaims requires Nucleus %s+... disabling integration",
                metaService.semanticVersion(), PluginInfo.NUCLEUS_VERSION));
            Sponge.getEventManager().unregisterListeners(this);
        }
    }

    @Listener
    public void onGameAboutToStart(GameAboutToStartServerEvent event) {
        NucleusConfig config = PLUGIN.getConfig().getIntegrationConfig().getNucleus();

        if (config.isHomesEnabled()) {
            initHomeSupport();
        }

        //kitService = PLUGIN.getGame().getServiceManager().provideUnchecked(NucleusKitService.class);
        //afkService = PLUGIN.getGame().getServiceManager().provideUnchecked(NucleusAFKService.class);
    }

    @Listener
    public void onReload(GameReloadEvent event) {
        CommandHome.register();
        CommandSetHome.register();
    }

    private void initHomeSupport() {
        homeService = PLUGIN.getGame().getServiceManager().provideUnchecked(NucleusHomeService.class);
        CommandHome.register();
        CommandSetHome.register();
    }

    public static NucleusHomeService getHomeService() {
        return homeService;
    }

    public static NucleusKitService getKitService() {
        return kitService;
    }

    public static boolean isAFK(Player player) {
        return afkService.isAFK(player);
    }

    public static Instant lastActivity(Player player) {
        return afkService.lastActivity(player);
    }
}
