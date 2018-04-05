package me.skymc.spacebook.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * 玩家工具
 * 
 * @author sky
 * @since 2018年2月3日 下午6:15:42
 */
public class PlayerUtils {
	
	/**
	 * 是否为管理员模式
	 * 
	 * @param player 玩家
	 * @return {@link Boolean}
	 */
	@SuppressWarnings("deprecation")
	public static boolean isAdminMode(Player player) {
		return player.isOp() && player.getItemInHand().getType().equals(Material.COMMAND);
	}
	
	/**
	 * 清理聊天框
	 * 
	 * @param player 玩家
	 * @param line 行数
	 */
	public static void clearScreen(Player player, int line) {
		for (int i = 0 ; i < line ; i++) {
			player.sendMessage("");
		}
	}
}
