/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package xyz.drwhomust.quickmessage.init;

import xyz.drwhomust.quickmessage.network.WhoAreYouMessage;
import xyz.drwhomust.quickmessage.network.NiceMessage;
import xyz.drwhomust.quickmessage.network.HelloMessage;
import xyz.drwhomust.quickmessage.network.GoodByeMessage;

import org.lwjgl.glfw.GLFW;

import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;

@EventBusSubscriber(Dist.CLIENT)
public class QuickMessageModKeyMappings {
	public static final KeyMapping HELLO = new KeyMapping("key.quick_message.hello", GLFW.GLFW_KEY_U, KeyMapping.Category.MULTIPLAYER) {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				ClientPacketDistributor.sendToServer(new HelloMessage(0, 0));
				HelloMessage.pressAction(Minecraft.getInstance().player, 0, 0);
			}
			isDownOld = isDown;
		}
	};
	public static final KeyMapping WHO_ARE_YOU = new KeyMapping("key.quick_message.who_are_you", GLFW.GLFW_KEY_H, KeyMapping.Category.MULTIPLAYER) {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				ClientPacketDistributor.sendToServer(new WhoAreYouMessage(0, 0));
				WhoAreYouMessage.pressAction(Minecraft.getInstance().player, 0, 0);
			}
			isDownOld = isDown;
		}
	};
	public static final KeyMapping GOOD_BYE = new KeyMapping("key.quick_message.good_bye", GLFW.GLFW_KEY_J, KeyMapping.Category.MULTIPLAYER) {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				ClientPacketDistributor.sendToServer(new GoodByeMessage(0, 0));
				GoodByeMessage.pressAction(Minecraft.getInstance().player, 0, 0);
			}
			isDownOld = isDown;
		}
	};
	public static final KeyMapping NICE = new KeyMapping("key.quick_message.nice", GLFW.GLFW_KEY_K, KeyMapping.Category.MULTIPLAYER) {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				ClientPacketDistributor.sendToServer(new NiceMessage(0, 0));
				NiceMessage.pressAction(Minecraft.getInstance().player, 0, 0);
			}
			isDownOld = isDown;
		}
	};

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(HELLO);
		event.register(WHO_ARE_YOU);
		event.register(GOOD_BYE);
		event.register(NICE);
	}

	@EventBusSubscriber(Dist.CLIENT)
	public static class KeyEventListener {
		@SubscribeEvent
		public static void onClientTick(ClientTickEvent.Post event) {
			if (Minecraft.getInstance().screen == null) {
				HELLO.consumeClick();
				WHO_ARE_YOU.consumeClick();
				GOOD_BYE.consumeClick();
				NICE.consumeClick();
			}
		}
	}
}