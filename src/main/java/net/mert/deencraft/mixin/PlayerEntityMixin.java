package net.mert.deencraft.mixin;

import net.mert.deencraft.util.IEntityDataSaver;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements IEntityDataSaver {
    @Unique
    private static final String THIRST_KEY = "thirst";

    @Unique
    private NbtCompound persistentData;

    @Override
    public float getThirst() {
        return getPersistentData().getInt(THIRST_KEY).orElse(20);
    }

    @Override
    public void setThirst(float thirst) {
        int clampedThirst = Math.max(0, Math.min(Math.round(thirst), 20));
        getPersistentData().putInt(THIRST_KEY, clampedThirst);
    }

    @Override
    public void addThirst(float amount) {
        setThirst(getThirst() + amount);
    }

    @Override
    public NbtCompound getPersistentData() {
        if (this.persistentData == null) {
            this.persistentData = new NbtCompound();
        }
        if (!this.persistentData.contains(THIRST_KEY)) {
            this.persistentData.putInt(THIRST_KEY, 20);
        }
        return this.persistentData;
    }

    @Override
    public void setPersistentData(NbtCompound nbt) {
        this.persistentData = nbt;
        if (!this.persistentData.contains(THIRST_KEY)) {
            this.persistentData.putInt(THIRST_KEY, 20);
        }
    }
}