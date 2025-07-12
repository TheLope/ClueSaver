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
package com.cluesaver.ids;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import net.runelite.api.ItemID;

@Getter
public class ImplingJars
{
	@Getter
	public static final List<Integer> itemIds = Arrays.asList(
		ItemID.BABY_IMPLING_JAR,
		ItemID.YOUNG_IMPLING_JAR,
		ItemID.GOURMET_IMPLING_JAR,
		ItemID.EARTH_IMPLING_JAR,
		ItemID.ESSENCE_IMPLING_JAR,
		ItemID.ECLECTIC_IMPLING_JAR,
		ItemID.NATURE_IMPLING_JAR,
		ItemID.MAGPIE_IMPLING_JAR,
		ItemID.NINJA_IMPLING_JAR,
		ItemID.CRYSTAL_IMPLING_JAR,
		ItemID.DRAGON_IMPLING_JAR);

	@Getter
	public static final List<Integer> beginnerIds = Arrays.asList(
		ItemID.BABY_IMPLING_JAR,
		ItemID.YOUNG_IMPLING_JAR);

	@Getter
	public static final List<Integer> easyIds = Arrays.asList(
		ItemID.BABY_IMPLING_JAR,
		ItemID.YOUNG_IMPLING_JAR,
		ItemID.GOURMET_IMPLING_JAR);

	@Getter
	public static final List<Integer> mediumIds = Arrays.asList(
		ItemID.EARTH_IMPLING_JAR,
		ItemID.ESSENCE_IMPLING_JAR,
		ItemID.ECLECTIC_IMPLING_JAR);

	@Getter
	public static final List<Integer> hardIds = Arrays.asList(
		ItemID.NATURE_IMPLING_JAR,
		ItemID.MAGPIE_IMPLING_JAR,
		ItemID.NINJA_IMPLING_JAR);

	@Getter
	public static final List<Integer> eliteIds = Arrays.asList(
		ItemID.CRYSTAL_IMPLING_JAR,
		ItemID.DRAGON_IMPLING_JAR);
}
