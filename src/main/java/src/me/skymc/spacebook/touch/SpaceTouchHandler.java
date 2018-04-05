package me.skymc.spacebook.touch;

import org.bukkit.entity.Player;

import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.TouchHandler;

import lombok.Getter;
import me.skymc.spacebook.api.SpaceBookAPI;

/**
 * TouchHandler
 * 
 * @author sky
 * @since 2018Äê1ÔÂ31ÈÕ21:45:45
 */
@SuppressWarnings("deprecation")
public class SpaceTouchHandler implements TouchHandler {
	
	@Getter
	private String bookUID;
	
	public SpaceTouchHandler(String bookUID) {
		this.bookUID = bookUID;
	}

	@Override
	public void onTouch(Hologram holo, Player who) {
		SpaceBookAPI.openInventory(who, bookUID, 1);
	}

}
