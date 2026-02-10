package net.mert.deencraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.CarpetBlock;

// Extends CarpetBlock zodat het als tapijt gedropt wordt en nonOpaque kan zijn
public class PrayerMatBlock extends CarpetBlock {

    public PrayerMatBlock(AbstractBlock.Settings settings) {
        super(settings);
    }
}