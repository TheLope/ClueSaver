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
	String GROUP = "cluesaver";

	String BEGINNER_STATE = "beginnerState";
	String EASY_STATE = "easyState";
	String MEDIUM_STATE = "mediumState";
	String HARD_STATE = "hardState";
	String ELITE_STATE = "eliteState";
	String MASTER_STATE = "masterState";

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

	@ConfigSection(name = "Debug", description = "Options that provide additional information", position = 7)
	String debugSection = "Debug";

	@ConfigItem(
		keyName = "separateBoxCounts",
		name = "Separate scroll box counts",
		description = "Show scroll box count explicitly for inventory and bank",
		section = debugSection,
		position = 0
	)
	default boolean separateBoxCounts()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showBeginnerInfo",
		name = "Show beginner info",
		description = "Show saving state for beginner clues regardless of if clue saver if active",
		section = debugSection,
		position = 1
	)
	default boolean showBeginnerInfo()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showEasyInfo",
		name = "Show easy info",
		description = "Show saving state for easy clues regardless of if clue saver if active",
		section = debugSection,
		position = 2
	)
	default boolean showEasyInfo()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showMediumInfo",
		name = "Show medium info",
		description = "Show saving state for medium clues regardless of if clue saver if active",
		section = debugSection,
		position = 3
	)
	default boolean showMediumInfo()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showHardInfo",
		name = "Show hard info",
		description = "Show saving state for hard clues regardless of if clue saver if active",
		section = debugSection,
		position = 4
	)
	default boolean showHardInfo()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showEliteInfo",
		name = "Show elite info",
		description = "Show saving state for elite clues regardless of if clue saver if active",
		section = debugSection,
		position = 5
	)
	default boolean showEliteInfo()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showMasterInfo",
		name = "Show master info",
		description = "Show saving state for master clues regardless of if clue saver if active",
		section = debugSection,
		position = 5
	)
	default boolean showMasterInfo()
	{
		return false;
	}

	@ConfigSection(name = "Beginner Clues", description = "Methods that reward easy clues", position = 6)
	String beginnerSection = "Beginner Clues";

	@ConfigItem(
		keyName = "saveBeginnerImplings",
		name = "Save Beginner Implings",
		description = "Looting baby and young impling jars is blocked while player is ineligible to receive a beginner scroll box",
		section = beginnerSection,
		position = 0
	)
	default boolean saveBeginnerImplings()
	{
		return true;
	}

	@ConfigSection(name = "Easy Clues", description = "Methods that reward easy clues", position = 5)
	String easySection = "Easy Clues";

	@ConfigItem(
		keyName = "saveEasyImplings",
		name = "Save Easy Implings",
		description = "Looting baby, young, and gourmet impling jars is blocked while player is ineligible to receive an easy scroll box",
		section = easySection,
		position = 0
	)
	default boolean saveEasyImplings()
	{
		return true;
	}

	@ConfigSection(name = "Medium Clues", description = "Methods that reward medium clues", position = 4)
	String mediumSection = "Medium Clues";

	@ConfigItem(
		keyName = "saveMediumImplings",
		name = "Save Medium Implings",
		description = "Looting earth, eclectic, and essence impling jars is blocked while player is ineligible to receive a medium scroll box",
		section = mediumSection,
		position = 0
	)
	default boolean saveMediumImplings()
	{
		return true;
	}

	@ConfigSection(name = "Hard Clues", description = "Methods that reward hard clues", position = 3)
	String hardSection = "Hard Clues";

	@ConfigItem(
		keyName = "saveHardImplings",
		name = "Save Hard Implings",
		description = "Looting nature, magpie, and ninja impling jars is blocked while player is ineligible to receive a hard scroll box",
		section = hardSection,
		position = 0
	)
	default boolean saveHardImplings()
	{
		return true;
	}

	@ConfigSection(name = "Elite Clues", description = "Methods that reward elite clues", position = 2)
	String eliteSection = "Elite Clues";

	@ConfigItem(
		keyName = "saveEliteImplings",
		name = "Save Elite Implings",
		description = "Looting crystal and dragon impling jars is blocked while player is ineligible to receive an elite scroll box",
		section = eliteSection,
		position = 0
	)
	default boolean saveEliteImplings()
	{
		return true;
	}

	@ConfigItem(
		keyName = "saveDarkTotems",
		name = "Save Dark Totems",
		description = "Blocks dark totem usage while player is ineligible to receive an elite scroll box",
		section = eliteSection,
		position = 1
	)
	default boolean saveDarkTotems()
	{
		return true;
	}

	@ConfigItem(
		keyName = "saveGoldKeys",
		name = "Save Gold keys",
		description = "Blocks gold key usage while player is ineligible to receive an elite scroll box",
		section = eliteSection,
		position = 2
	)
	default boolean saveGoldKeys()
	{
		return true;
	}

	@ConfigItem(
		keyName = "saveGauntletRewardChests",
		name = "Save Gauntlet Reward Chests",
		description = "Blocks opening Gauntlet reward chest while player is ineligible to receive an elite scroll box",
		section = eliteSection,
		position = 3
	)
	default boolean saveGauntletRewardChests()
	{
		return true;
	}

	@ConfigItem(
		keyName = "saveTobRewardsChests",
		name = "Save ToB Rewards Chests",
		description = "Blocks opening ToB reward chest (by bank) while player is ineligible to receive an elite scroll box",
		section = eliteSection,
		position = 4
	)
	default boolean saveTobRewardsChests()
	{
		return true;
	}

	@ConfigSection(name = "Master Clues", description = "Methods that reward master clues", position = 1)
	String masterSection = "Master Clues";

	@ConfigItem(
		keyName = "casketCooldown",
		name = "Spamming cooldown",
		description = "Add a cooldown to the open option to help the plugin not miss master clues when spamming",
		section = masterSection,
		position = 0
	)
	default boolean casketCooldown()
	{
		return false;
	}

	@ConfigItem(
		keyName = "saveEasyCaskets",
		name = "Save Easy caskets",
		description = "Easy caskets cannot be opened while player is ineligible to receive a master scroll box",
		section = masterSection,
		position = 1
	)
	default boolean saveEasyCaskets()
	{
		return true;
	}

	@ConfigItem(
		keyName = "saveMediumCaskets",
		name = "Save Medium caskets",
		description = "Medium caskets cannot be opened while player is ineligible to receive a master scroll box",
		section = masterSection,
		position = 2
	)
	default boolean saveMediumCaskets()
	{
		return true;
	}

	@ConfigItem(
		keyName = "saveHardCaskets",
		name = "Save Hard caskets",
		description = "Hard caskets cannot be opened while player is ineligible to receive a master scroll box",
		section = masterSection,
		position = 3
	)
	default boolean saveHardCaskets()
	{
		return true;
	}

	@ConfigItem(
		keyName = "saveEliteCaskets",
		name = "Save Elite caskets",
		description = "Elite caskets cannot be opened while player is ineligible to receive a master scroll box",
		section = masterSection,
		position = 4
	)
	default boolean saveEliteCaskets()
	{
		return true;
	}
}
