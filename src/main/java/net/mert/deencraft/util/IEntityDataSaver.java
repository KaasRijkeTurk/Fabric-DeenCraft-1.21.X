package net.mert.deencraft.util;

import net.minecraft.nbt.NbtCompound;

public interface IEntityDataSaver {
    float getThirst();
    void setThirst(float thirst);
    void addThirst(float amount);
    NbtCompound getPersistentData();
    void setPersistentData(NbtCompound nbt);
}