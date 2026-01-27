package net.mert.deencraft.util;

import net.minecraft.nbt.NbtCompound;

public interface IEntityDataSaver {
    NbtCompound getPersistentData();
    void setPersistentData(NbtCompound nbt);
}