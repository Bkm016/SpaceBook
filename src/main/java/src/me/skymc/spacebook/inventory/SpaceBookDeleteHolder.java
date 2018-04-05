package me.skymc.spacebook.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import lombok.Getter;

/**
 * InventoryHolder
 * 
 * @author sky
 * @since 2018年2月3日 下午1:30:14
 */
public class SpaceBookDeleteHolder implements InventoryHolder {

	@Getter
	private String bookID;
	
	public SpaceBookDeleteHolder(String bookID) {
		this.bookID = bookID;
	}
	
	@Override
	public Inventory getInventory() {
		return null;
	}

}
