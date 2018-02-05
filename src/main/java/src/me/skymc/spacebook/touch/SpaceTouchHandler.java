package me.skymc.spacebook.touch;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.TouchHandler;

import lombok.Getter;
import me.skymc.spacebook.SpaceBook;
import me.skymc.spacebook.api.SpaceBookAPI;
import me.skymc.spacebook.inventory.SpaceBookDeleteHolder;
import me.skymc.spacebook.utils.PlayerUtils;

/**
 * TouchHandler
 * 
 * @author sky
 * @since 2018��1��31��21:45:45
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
