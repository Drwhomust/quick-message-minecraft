package xyz.drwhomust.quickmessage.network;

import xyz.drwhomust.quickmessage.procedures.HelloOnKeyPressedProcedure;
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
public record HelloMessage(int eventType, int pressedms) implements CustomPacketPayload {
	public static final Type<HelloMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(QuickMessageMod.MODID, "key_hello"));
	public static final StreamCodec<RegistryFriendlyByteBuf, HelloMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, HelloMessage message) -> {
		buffer.writeInt(message.eventType);
		buffer.writeInt(message.pressedms);
	}, (RegistryFriendlyByteBuf buffer) -> new HelloMessage(buffer.readInt(), buffer.readInt()));

	@Override
	public Type<HelloMessage> type() {
		return TYPE;
	}

	public static void handleData(final HelloMessage message, final IPayloadContext context) {
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

			HelloOnKeyPressedProcedure.execute(world, entity);
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		QuickMessageMod.addNetworkMessage(HelloMessage.TYPE, HelloMessage.STREAM_CODEC, HelloMessage::handleData);
	}
}