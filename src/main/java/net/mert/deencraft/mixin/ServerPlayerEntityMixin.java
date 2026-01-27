package net.mert.deencraft.mixin;

import com.mojang.authlib.GameProfile;
import net.mert.deencraft.util.IEntityDataSaver;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements IEntityDataSaver {

    @Unique
    private NbtCompound persistentData;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(MinecraftServer server, ServerWorld world, GameProfile profile, SyncedClientOptions clientOptions, CallbackInfo ci) {
        this.persistentData = new NbtCompound(); // Maak NBT aan bij speler
    }

    @Override
    public NbtCompound getPersistentData() {
        return this.persistentData;
    }

    @Override
    public void setPersistentData(NbtCompound nbt) {
        this.persistentData = nbt;
    }
}