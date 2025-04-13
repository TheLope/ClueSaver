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

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("ClueSaver")
public interface ClueSaverConfig extends Config
{
	@ConfigSection(name = "Overlays", description = "Options that effect overlays", position = 0)
	String overlaysSection = "Overlays";

	@ConfigItem(
		keyName = "showChatMessage",
		name = "Show chat message",
		description = "Show chat message indicating when clues are being saved",
		section = overlaysSection,
		position = 0
	)
	default boolean showChatMessage()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showInfobox",
		name = "Show infobox",
		description = "Show infobox indicating when clues are being saved",
		section = overlaysSection,
		position = 1
	)
	default boolean showInfobox()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showTooltip",
		name = "Show tooltip",
		description = "Show tooltip on clue \"Open\" hover when clues are being saved",
		section = overlaysSection,
		position = 2
	)
	default boolean showTooltip()
	{
		return true;
	}

	@ConfigSection(name = "Elite Clues", description = "Methods that reward elite clues", position = 1)
	String eliteSection = "Elite Clues";

	@ConfigItem(
		keyName = "saveDarkTotems",
		name = "Save Dark Totems",
		description = "Blocks dark totem usage if an elite clue has been found in your possession",
		section = eliteSection,
		position = 0
	)
	default boolean saveDarkTotems()
	{
		return true;
	}

	@ConfigItem(
		keyName = "saveGoldKeys",
		name = "Save Gold keys",
		description = "Blocks gold key usage if an elite clue has been found in your possession",
		section = eliteSection,
		position = 1
	)
	default boolean saveGoldKeys()
	{
		return true;
	}

	@ConfigItem(
		keyName = "saveGauntletRewardChests",
		name = "Save Gauntlet Reward Chests",
		description = "Blocks opening Gauntlet reward chest if an elite clue has been found in your possession",
		section = eliteSection,
		position = 2
	)
	default boolean saveGauntletRewardChests()
	{
		return true;
	}

	@ConfigItem(
		keyName = "saveTobRewardsChests",
		name = "Save ToB Rewards Chests",
		description = "Blocks opening ToB reward chest (by bank) if an elite clue has been found in your possession",
		section = eliteSection,
		position = 3
	)
	default boolean saveTobRewardsChests()
	{
		return true;
	}
}
