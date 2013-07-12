package logisticspipes.network.packets.orderer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import logisticspipes.gui.orderer.GuiOrderer;
import logisticspipes.network.abstractpackets.ModernPacket;
import logisticspipes.utils.ItemMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.client.FMLClientHandler;

@Accessors(chain=true)
public class ComponentList extends ModernPacket {

	@Getter
	@Setter
	private List<ItemMessage> used;
	
	@Getter
	@Setter
	private List<ItemMessage> missing;
	
	public ComponentList(int id) {
		super(id);
	}

	@Override
	public ModernPacket template() {
		return new ComponentList(getId());
	}

	@Override
	public void processPacket(EntityPlayer player) {
		if (FMLClientHandler.instance().getClient().currentScreen instanceof GuiOrderer) {
			((GuiOrderer)FMLClientHandler.instance().getClient().currentScreen).handleSimulateAnswer(getUsed(),getMissing(), (GuiOrderer)FMLClientHandler.instance().getClient().currentScreen,FMLClientHandler.instance().getClient().thePlayer);
		} else {
			for (final ItemMessage items : getUsed()) {
				player.addChatMessage("Used: " + items);
			}
			for (final ItemMessage items : getMissing()) {
				player.addChatMessage("Missing: " + items);
			}
		}
	}
	
	@Override
	public void writeData(DataOutputStream data) throws IOException {
		for(ItemMessage msg:used) {
			data.write(1);
			data.writeInt(msg.id);
			data.writeInt(msg.data);
			data.writeInt(msg.amount);
		}
		data.write(0);
		for(ItemMessage msg:missing) {
			data.write(1);
			data.writeInt(msg.id);
			data.writeInt(msg.data);
			data.writeInt(msg.amount);
		}
		data.write(0);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		while(data.read() != 0) {
			ItemMessage msg = new ItemMessage();
			msg.id = data.readInt();
			msg.data = data.readInt();
			msg.amount = data.readInt();
			used.add(msg);
		}
		while(data.read() != 0) {
			ItemMessage msg = new ItemMessage();
			msg.id = data.readInt();
			msg.data = data.readInt();
			msg.amount = data.readInt();
			missing.add(msg);
		}
	}
}

