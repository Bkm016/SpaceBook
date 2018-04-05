package me.skymc.spacebook.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * ��ҹ���
 * 
 * @author sky
 * @since 2018��2��3�� ����6:15:42
 */
public class PlayerUtils {
	
	/**
	 * �Ƿ�Ϊ����Աģʽ
	 * 
	 * @param player ���
	 * @return {@link Boolean}
	 */
	@SuppressWarnings("deprecation")
	public static boolean isAdminMode(Player player) {
		return player.isOp() && player.getItemInHand().getType().equals(Material.COMMAND);
	}
	
	/**
	 * ���������
	 * 
	 * @param player ���
	 * @param line ����
	 */
	public static void clearScreen(Player player, int line) {
		for (int i = 0 ; i < line ; i++) {
			player.sendMessage("");
		}
	}
}
