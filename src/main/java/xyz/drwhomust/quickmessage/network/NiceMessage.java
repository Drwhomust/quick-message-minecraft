package xyz.drwhomust.quickmessage.network;

import xyz.drwhomust.quickmessage.procedures.NiceOnKeyPressedProcedure;
import xyz.drwhomust.quickmessage.QuickMessageMod;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.Identifier;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.SectionPos;

@EventBusSubscriber
public record NiceMessage(int eventType, int pressedms) implements CustomPacketPayload {
	public static final Type<NiceMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(QuickMessageMod.MODID, "key_nice"));
	public static final StreamCodec<RegistryFriendlyByteBuf, NiceMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, NiceMessage message) -> {
		buffer.writeInt(message.eventType);
		buffer.writeInt(message.pressedms);
	}, (RegistryFriendlyByteBuf buffer) -> new NiceMessage(buffer.readInt(), buffer.readInt()));

	@Override
	public Type<NiceMessage> type() {
		return TYPE;
	}

	public static void handleData(final NiceMessage message, final IPayloadContext context) {
		if (context.flow() == PacketFlow.SERVERBOUND) {
			context.enqueueWork(() -> {
				pressAction(context.player(), message.eventType, message.pressedms);
			}).exceptionally(e -> {
				context.connection().disconnect(Component.literal(e.getMessage()));
				return null;
			});
		}
	}

	public static void pressAction(Player entity, int type, int pressedms) {
		Level world = entity.level();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		// security measure to prevent arbitrary chunk generation
		if (!world.getChunkSource().hasChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z)))
			return;
		if (type == 0) {

			NiceOnKeyPressedProcedure.execute(world, entity);
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		QuickMessageMod.addNetworkMessage(NiceMessage.TYPE, NiceMessage.STREAM_CODEC, NiceMessage::handleData);
	}
}