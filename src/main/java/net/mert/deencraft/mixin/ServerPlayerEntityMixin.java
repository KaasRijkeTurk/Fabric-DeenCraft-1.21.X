package net.mert.deencraft.mixin;

import net.mert.deencraft.util.IEntityDataSaver;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void deencraft$copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        if ((Object) this instanceof IEntityDataSaver newSaver && oldPlayer instanceof IEntityDataSaver oldSaver) {
            NbtCompound copied = oldSaver.getPersistentData().copy();
            newSaver.setPersistentData(copied);
        }
    }
}