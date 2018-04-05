package me.skymc.spacebook.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.skymc.spacebook.SpaceBook;
import me.skymc.spacebook.api.SpaceBookAPI;
import me.skymc.spacebook.inventory.SpaceBookCreateHolder;
import me.skymc.spacebook.inventory.SpaceBookDeleteHolder;
import me.skymc.spacebook.inventory.SpaceBookHolder;
import me.skymc.spacebook.utils.PlayerUtils;

/**
 * 背包监听
 * 
 * @author sky
 * @since 2018年2月3日 下午6:14:55
 */
public class ListenerInventory implements Listener {
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void click(InventoryClickEvent e) {
		// 检查背包类型
		if (e.getInventory().getHolder() instanceof SpaceBookCreateHolder) {
			e.setCancelled(true);
			
			// 确认按钮
			if (e.getRawSlot() != 4) {
				return;
			}
			
			// 检查附近是否有其他时空之书
			if (SpaceBookAPI.isBookNear(e.getWhoClicked().getLocation())) {
				SpaceBook.getLanguage().get("BOOK-NEAR").send(e.getWhoClicked());
				return;
			}
			
			// 删除手中物品
			if (e.getWhoClicked().getItemInHand().getAmount() > 1) {
				e.getWhoClicked().getItemInHand().setAmount(e.getWhoClicked().getItemInHand().getAmount() - 1);
			}
			else {
				e.getWhoClicked().setItemInHand(null);
			}
			
			// 关闭背包
			e.getWhoClicked().closeInventory();
			// 创建配置
			SpaceBookAPI.createBook(e.getWhoClicked().getLocation().add(0, 2, 0), e.getWhoClicked().getName());
			// 全服提示
			SpaceBook.getLanguage().get("CREATE-MESSAGE").addPlaceholder("$player", e.getWhoClicked().getName()).broadcast();
		}
		else if (e.getInventory().getHolder() instanceof SpaceBookDeleteHolder) {
			e.setCancelled(true);
			// 获取背包信息
			SpaceBookDeleteHolder holder = (SpaceBookDeleteHolder) e.getInventory().getHolder();
			
			// 删除按键
			if (e.getRawSlot() == 4) {
				// 关闭背包
				e.getWhoClicked().closeInventory();
				// 提示信息
				SpaceBook.getLanguage().get("BOOK-DELETE").send(e.getWhoClicked());
				// 删除数据
				SpaceBookAPI.deleteBook(holder.getBookID());
			}
		}
		else if (e.getInventory().getHolder() instanceof SpaceBookHolder) {
			e.setCancelled(true);
			// 获取背包信息
			SpaceBookHolder holder = (SpaceBookHolder) e.getInventory().getHolder();
			
			// 如果点击的是信息按钮
			if (e.getRawSlot() == 49) {
				// 如果是管理员模式
				if (PlayerUtils.isAdminMode((Player) e.getWhoClicked())) {
					// 打开删除界面
					SpaceBookAPI.openInventoryAdmin((Player) e.getWhoClicked(), holder.getBookID());
				}
				else {
					// 关闭背包
					e.getWhoClicked().closeInventory();
					// 回复引导
					SpaceBookAPI.createReplyGuide((Player) e.getWhoClicked(), holder.getBookID());
				}
			}
			
			// 上一页
			else if (e.getRawSlot() == 47) {
				// 如果是第一页
				if (holder.getPage() == 1) {
					return;
				}
				else {
					// 前往上一页
					SpaceBookAPI.openInventory((Player) e.getWhoClicked(), holder.getBookID(), holder.getPage() - 1);
				}
			}
			
			// 下一页
			else if (e.getRawSlot() == 51) {
				// 如果是最后一页
				if (holder.getPage() == SpaceBookAPI.getMaxPage(holder.getBookID())) {
					return;
				}
				else {
					// 前往下一页
					SpaceBookAPI.openInventory((Player) e.getWhoClicked(), holder.getBookID(), holder.getPage() + 1);
				}
			}
			
			// 回复按钮且管理员模式
			else if (e.getRawSlot() >= 10 && e.getRawSlot() <= 43 && PlayerUtils.isAdminMode((Player) e.getWhoClicked()) && holder.getReplyData().containsKey(e.getRawSlot())) {
				// 删除回复
				SpaceBook.getSpaceBookData().set(holder.getBookID() + ".Reply." + holder.getReplyData().get(e.getRawSlot()), null);
				// 提示信息
				SpaceBook.getLanguage().get("MENU-DELETE").send(e.getWhoClicked());;
				// 重新打开
				SpaceBookAPI.openInventory((Player) e.getWhoClicked(), holder.getBookID(), holder.getPage());
			}
		}
	}

}
