/*
 * Copyright (c) 2025, TheLope <https://github.com/TheLope>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.cluesaver;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;

@Slf4j
@Singleton
public class TierStateSaveManager
{
	private final ClueSaverPlugin clueSaverPlugin;
	private final ConfigManager configManager;
	private final Gson gson;

	@Inject
	public TierStateSaveManager(ClueSaverPlugin clueSaverPlugin, ConfigManager configManager, Gson gson)
	{
		this.clueSaverPlugin = clueSaverPlugin;
		this.configManager = configManager;
		this.gson = gson;
	}

	private void saveTierToConfig(String profileKey, ClueTier tier, String configKey)
	{
		// Serialize tier state and save to config
		TierState state = getTierData(tier);
		String stateData = gson.toJson(state);
		configManager.setConfiguration(ClueSaverConfig.GROUP, profileKey, configKey, stateData);
	}

	public void saveStateToConfig(String profileKey)
	{
		if (profileKey == null)
		{
			return;
		}

		saveTierToConfig(profileKey, ClueTier.BEGINNER, ClueSaverConfig.BEGINNER_STATE);
		saveTierToConfig(profileKey, ClueTier.EASY, ClueSaverConfig.EASY_STATE);
		saveTierToConfig(profileKey, ClueTier.MEDIUM, ClueSaverConfig.MEDIUM_STATE);
		saveTierToConfig(profileKey, ClueTier.HARD, ClueSaverConfig.HARD_STATE);
		saveTierToConfig(profileKey, ClueTier.ELITE, ClueSaverConfig.ELITE_STATE);
		saveTierToConfig(profileKey, ClueTier.MASTER, ClueSaverConfig.MASTER_STATE);
	}

	private TierState getTierData(ClueTier tier)
	{
		TierState newData = new TierState();
		ClueScrollState clueState = clueSaverPlugin.getClueStates().getClueStateFromTier(tier);
		newData.setClueScrollLocation(clueState.getLocation());

		ScrollBoxState boxState = clueSaverPlugin.getClueStates().getBoxStateFromTier(tier);
		newData.setScrollBoxInventoryCount(boxState.getInventoryCount());
		newData.setScrollBoxBankCount(boxState.getBankCount());

		return newData;
	}

	public void loadStateFromConfig()
	{
		loadTierFromConfig(ClueSaverConfig.BEGINNER_STATE, ClueTier.BEGINNER);
		loadTierFromConfig(ClueSaverConfig.EASY_STATE, ClueTier.EASY);
		loadTierFromConfig(ClueSaverConfig.MEDIUM_STATE, ClueTier.MEDIUM);
		loadTierFromConfig(ClueSaverConfig.HARD_STATE, ClueTier.HARD);
		loadTierFromConfig(ClueSaverConfig.ELITE_STATE, ClueTier.ELITE);
		loadTierFromConfig(ClueSaverConfig.MASTER_STATE, ClueTier.MASTER);
	}

	public void loadTierFromConfig(String key, ClueTier tier)
	{
		String tierStateJson = configManager.getRSProfileConfiguration(ClueSaverConfig.GROUP, key);

		if (tierStateJson != null)
		{
			try
			{
				Type tierDataType = new TypeToken<TierState>()
				{
				}.getType();

				TierState loadedTierData = gson.fromJson(tierStateJson, tierDataType);

				// Sanitize TierState for UIM if invalid setting
				if (clueSaverPlugin.isUltimateIronman())
				{
					if (loadedTierData.getClueScrollLocation() == ClueLocation.BANK)
					{
						loadedTierData.setClueScrollLocation(ClueLocation.UNKNOWN);
					}

					if (loadedTierData.getScrollBoxBankCount() > 0)
					{
						loadedTierData.setScrollBoxBankCount(0);
					}
				}

				// Convert TierState back to ClueScrollState/ScrollBoxState
				clueSaverPlugin.getClueStates().setFromTierState(loadedTierData, tier);

			} catch (Exception err)
			{
				log.error("e: ", err);
			}
		}
		// We have no data for this tier. Reset to unknown state
		else
		{
			clueSaverPlugin.getClueStates().resetTierState(tier);
		}
	}
}
