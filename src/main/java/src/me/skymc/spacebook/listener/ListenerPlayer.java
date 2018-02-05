package me.skymc.spacebook.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;

import me.skymc.spacebook.SpaceBook;
import me.skymc.spacebook.api.SpaceBookAPI;
import me.skymc.spacebook.inventory.SpaceBookCreateHolder;
import me.skymc.taboolib.inventory.ItemUtils;

/**
 * 玩家监听
 * 
 * @author sky
 * @since 2018年2月3日 下午1:02:12
 */
public class ListenerPlayer implements Listener {
	
	@EventHandler
	public void quit(PlayerQuitEvent e) {
		e.getPlayer().removeMetadata("spacebook-open", SpaceBook.getInst());
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		// 检查点击方式
		if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) || e.getHand() == EquipmentSlot.OFF_HAND) {
			return;
		}
		
		// 检查手持物品
		if (!SpaceBookAPI.isBookItem(e.getPlayer().getItemInHand())) {
			return;
		}
		else {
			e.setCancelled(true);
		}
		
		// 检查附近是否有其他时空之书
		if (SpaceBookAPI.isBookNear(e.getPlayer().getLocation())) {
			SpaceBook.getLanguage().send(e.getPlayer(), "BOOK-NEAR");
			return;
		}
		
		// 创建背包
		Inventory inventory = Bukkit.createInventory(new SpaceBookCreateHolder(), 9, SpaceBook.getLanguage().get("CREATE-TITLE"));
		// 确认按钮
		inventory.setItem(4, ItemUtils.loadItem(SpaceBook.getInst().getConfig(), "Settings.createitem", null));
		// 打开背包
		e.getPlayer().openInventory(inventory);
	}

}
