package me.skymc.spacebook.inventory;

import java.util.HashMap;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import lombok.Getter;

/**
 * InventoryHolder
 * 
 * @author sky
 * @since 2018年2月3日 下午1:30:14
 */
public class SpaceBookHolder implements InventoryHolder {

	@Getter
	private String bookID;
	
	@Getter
	private int page;
	
	@Getter
	private HashMap<Integer, String> replyData = new HashMap<>();
	
	public SpaceBookHolder(String bookID, int page) {
		this.bookID = bookID;
		this.page = page;
	}
	
	@Override
	public Inventory getInventory() {
		return null;
	}

}
