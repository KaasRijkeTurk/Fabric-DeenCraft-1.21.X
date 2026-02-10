package net.mert.deencraft.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.mert.deencraft.util.IEntityDataSaver;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ThirstDataSyncS2CPacket(int thirst) implements CustomPayload {
    public static final Id<ThirstDataSyncS2CPacket> ID = new Id<>(Identifier.of("deencraft", "thirst_sync"));

    // Codec voor 1.21.x (gebruikt RegistryByteBuf)
    public static final PacketCodec<RegistryByteBuf, ThirstDataSyncS2CPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, ThirstDataSyncS2CPacket::thirst,
            ThirstDataSyncS2CPacket::new
    );

    @Override public Id<? extends CustomPayload> getId() { return ID; }

    public static void receive(ThirstDataSyncS2CPacket payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            // We halen de speler op uit de context
            if (context.player() instanceof IEntityDataSaver saver) {
                // We slaan de dorstwaarde op in de NBT van de client-speler
                saver.getPersistentData().putInt("thirst", payload.thirst());

                // OPTIONEEL: Voeg deze regel toe om te debuggen in je console
                // System.out.println("Client ontvangen dorst: " + payload.thirst());
            }
        });
    }
}