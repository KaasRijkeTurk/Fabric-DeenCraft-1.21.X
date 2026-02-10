package net.mert.deencraft.mixin;

import net.mert.deencraft.util.IEntityDataSaver;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements IEntityDataSaver {

    @Unique
    private float thirstLevel = 20.0f;

    @Unique
    private NbtCompound persistentData;

    @Override
    public float getThirst() {
        return thirstLevel;
    }

    @Override
    public void setThirst(float thirst) {
        this.thirstLevel = Math.max(0.0f, Math.min(thirst, 20.0f));
    }

    @Override
    public void addThirst(float amount) {
        setThirst(this.thirstLevel + amount);
    }

    @Override
    public NbtCompound getPersistentData() {
        if (this.persistentData == null) {
            this.persistentData = new NbtCompound();
        }
        return this.persistentData;
    }

    @Override
    public void setPersistentData(NbtCompound nbt) {
        this.persistentData = nbt;
    }
    /*
    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void writeThirstNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putFloat("thirstLevel", this.thirstLevel);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readThirstNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("thirstLevel")) {
            this.thirstLevel = nbt.getFloat("thirstLevel");
        }
    }
*/
}