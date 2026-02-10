package net.mert.deencraft.mixin;

import net.mert.deencraft.util.IEntityDataSaver;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements IEntityDataSaver {

    @Unique
    private float thirstLevel = 20.0f;

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