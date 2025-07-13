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

	public void saveStateToConfig()
	{
		// Serialize tier states save to config
		TierState beginnerState = getTierData(ClueTier.BEGINNER);
		String beginnerStateData = gson.toJson(beginnerState);
		configManager.setConfiguration(ClueSaverConfig.GROUP, ClueSaverConfig.BEGINNER_STATE, beginnerStateData);

		TierState easyState = getTierData(ClueTier.EASY);
		String easyStateData = gson.toJson(easyState);
		configManager.setConfiguration(ClueSaverConfig.GROUP, ClueSaverConfig.EASY_STATE, easyStateData);

		TierState mediumState = getTierData(ClueTier.MEDIUM);
		String mediumStateData = gson.toJson(mediumState);
		configManager.setConfiguration(ClueSaverConfig.GROUP, ClueSaverConfig.MEDIUM_STATE, mediumStateData);

		TierState hardState = getTierData(ClueTier.HARD);
		String hardStateData = gson.toJson(hardState);
		configManager.setConfiguration(ClueSaverConfig.GROUP, ClueSaverConfig.HARD_STATE, hardStateData);

		TierState eliteState = getTierData(ClueTier.ELITE);
		String eliteStateData = gson.toJson(eliteState);
		configManager.setConfiguration(ClueSaverConfig.GROUP, ClueSaverConfig.ELITE_STATE, eliteStateData);

		TierState masterState = getTierData(ClueTier.MASTER);
		String masterStateData = gson.toJson(masterState);
		configManager.setConfiguration(ClueSaverConfig.GROUP, ClueSaverConfig.MASTER_STATE, masterStateData);
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
		String tierStateJson = configManager.getConfiguration(ClueSaverConfig.GROUP, key);

		if (tierStateJson != null)
		{
			try
			{
				Type tierDataType = new TypeToken<TierState>()
				{
				}.getType();

				TierState loadedTierData = gson.fromJson(tierStateJson, tierDataType);

				// Convert TierState back to ClueScrollState/ScrollBoxState
				clueSaverPlugin.getClueStates().setFromTierState(loadedTierData, tier);

			} catch (Exception err)
			{
				log.error("e: ", err);
			}
		}
	}
}
