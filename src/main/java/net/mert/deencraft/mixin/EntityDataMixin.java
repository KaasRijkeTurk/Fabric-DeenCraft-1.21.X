package net.mert.deencraft.mixin;

import net.mert.deencraft.util.IEntityDataSaver;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityDataMixin implements IEntityDataSaver {
    private NbtCompound persistentData;

    @Override
    public NbtCompound getPersistentData() {
        if(this.persistentData == null) {
            this.persistentData = new NbtCompound();
        }
        return persistentData;
    }

    // Gebruik intermediary namen die gegarandeerd werken tijdens het compileren in 1.21.10
    @Inject(method = "method_5652", at = @At("HEAD"))
    protected void injectWriteMethod(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> info) {
        if(persistentData != null) {
            nbt.put("deencraft.custom_data", persistentData);
        }
    }

    @Inject(method = "method_5749", at = @At("HEAD"))
    protected void injectReadMethod(NbtCompound nbt, CallbackInfo info) {
        if (nbt.contains("deencraft.custom_data")) { // Gebruik contains met één argument
            persistentData = nbt.getCompound("deencraft.custom_data");
        }
    }
}
