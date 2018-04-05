package me.skymc.spacebook.api;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.object.CraftHologram;

import me.skymc.spacebook.SpaceBook;
import me.skymc.spacebook.inventory.SpaceBookDeleteHolder;
import me.skymc.spacebook.inventory.SpaceBookHolder;
import me.skymc.spacebook.touch.SpaceTouchHandler;
import me.skymc.spacebook.utils.PlayerUtils;
import me.skymc.taboolib.inventory.InventoryUtil;
import me.skymc.taboolib.inventory.ItemUtils;
import me.skymc.taboolib.jsonformatter.JSONFormatter;
import me.skymc.taboolib.jsonformatter.click.SuggestCommandEvent;
import me.skymc.taboolib.jsonformatter.hover.ShowTextEvent;
import me.skymc.taboolib.message.ChatCatcher;
import me.skymc.taboolib.message.ChatCatcher.Catcher;
import me.skymc.taboolib.other.NumberUtils;

/**
 * API
 * 
 * @author sky
 * @since 2018年2月3日 下午1:04:06
 */
public class SpaceBookAPI {
	
	/**
	 * 从TabooLib 物品库中获取时空之书, 如果物品不存在则返回石头
	 * 
	 * @return {@link ItemStack}
	 */
	public static ItemStack getBookItem() {
		ItemStack item = ItemUtils.getCacheItem(SpaceBook.getInst().getConfig().getString("Settings.bookitem"));
		return item == null ? new ItemStack(Material.STONE) : item.clone();
	}
	
	/**
	 * 从TabooLib 物品库中获取时空碎片, 如果物品不存在则返回石头
	 * 
	 * @return {@link ItemStack}
	 */
	public static ItemStack getReplyItem() {
		ItemStack item = ItemUtils.getCacheItem(SpaceBook.getInst().getConfig().getString("Settings.beplyitem"));
		return item == null ? new ItemStack(Material.STONE) : item.clone();
	}
	
	/**
	 * 检查物品是否为时空之书
	 * 
	 * @param item 物品
	 * @return {@link Boolean}
	 */
	public static boolean isBookItem(ItemStack item) {
		if (item == null || item.getType().equals(Material.AIR)) {
			return false;
		}
		return getBookItem().isSimilar(item);
	}
	
	/**
	 * 检查物品是否为时空碎片
	 * 
	 * @param item 物品
	 * @return {@link Boolean}
	 */
	public static boolean isReplyItem(ItemStack item) {
		if (item == null || item.getType().equals(Material.AIR)) {
			return false;
		}
		return getReplyItem().isSimilar(item);
	}
	
	/**
	 * 检查附近是否有其他时空之书
	 * 
	 * @param checkLocation 检查坐标
	 * @return {@link Boolean}
	 */
	public static boolean isBookNear(Location checkLocation) {
		for (String bookID : SpaceBook.getSpaceBookData().getConfigurationSection("").getKeys(false)) {
			Location location = (Location) SpaceBook.getSpaceBookData().get(bookID + ".Location");
			
			// 先检查世界后检查距离
			if (location.getWorld().getName().equals(checkLocation.getWorld().getName()) && location.distance(checkLocation) <= SpaceBook.getInst().getConfig().getInt("Settings.effectiveredius")) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取时空之书最大页数
	 * 
	 * @param bookID 序列号
	 * @return {@link Integer}
	 */
	public static int getMaxPage(String bookID) {
		// 检查序列号是否存在 或 没有回复
		if (!SpaceBook.getSpaceBookData().contains(bookID) 
				|| !SpaceBook.getSpaceBookData().contains(bookID + ".Reply") 
				|| SpaceBook.getSpaceBookData().getConfigurationSection(bookID + ".Reply").getKeys(false).size() == 0) {
			return 1;
		}
		return (int) Math.ceil(SpaceBook.getSpaceBookData().getConfigurationSection(bookID + ".Reply").getKeys(false).size() / 28D);
	}
	
	/**
	 * 创建时空之书数据
	 * 
	 * @param location 坐标
	 * @param ownder 创建者
	 */
	public static void createBook(Location location, String ownder) {
		String bookID = String.valueOf(System.currentTimeMillis());
		// 设置配置
		SpaceBook.getSpaceBookData().set(bookID + ".Ownder", ownder);
		SpaceBook.getSpaceBookData().set(bookID + ".Location", location);
		SpaceBook.getSpaceBookData().createSection(bookID + ".Reply");
		// 实现时空之书
		createBook(bookID);
	}
	
	/**
	 * 实现时空之书（将已存在的时空之书创建到服务器中）
	 * 
	 * @param bookID 序列号
	 */
	@SuppressWarnings("deprecation")
	public static void createBook(String bookID) {
		// 检查序列号是否存在
		if (!SpaceBook.getSpaceBookData().contains(bookID)) {
			return;
		}
		// 检查是否已经被实现
		if (SpaceBook.getHologramsData().containsKey(bookID)) {
			Hologram holo = SpaceBook.getHologramsData().get(bookID);
			// 删除现有全息
			if (!holo.isDeleted()) {
				holo.delete();
			}
		}
		// 创建新的全息
		CraftHologram holo = (CraftHologram) HologramsAPI.createHologram(SpaceBook.getInst(), (Location) SpaceBook.getSpaceBookData().get(bookID + ".Location"));
		// 添加物品
		holo.appendItemLine(new ItemStack(ItemUtils.asMaterial(SpaceBook.getInst().getConfig().getString("Settings.hologram.icon"))));
		// 添加文本
		for (String line : SpaceBook.getInst().getConfig().getStringList("Settings.hologram.text")) {
			holo.appendTextLine(line.replace("&", "§"));
		}
		
		// 添加点击处理器
		holo.setTouchHandler(new SpaceTouchHandler(bookID));
		SpaceBook.getHologramsData().put(bookID, holo);
	}
	
	/**
	 * 删除时空之书
	 * 
	 * @param bookID 序列号
	 */
	public static void deleteBook(String bookID) {
		// 删除数据
		SpaceBook.getSpaceBookData().set(bookID, null);
		// 删除全息
		if (SpaceBook.getHologramsData().containsKey(bookID)) {
			Hologram holo = SpaceBook.getHologramsData().get(bookID);
			if (!holo.isDeleted()) {
				holo.delete();
			}
		}
	}
	
	/**
	 * 打开时空之书删除界面
	 * 
	 * @param player
	 * @param bookID
	 */
	public static void openInventoryAdmin(Player player, String bookID) {
		// 创建背包
		Inventory inventory = Bukkit.createInventory(new SpaceBookDeleteHolder(bookID), 9, "确认删除当前时空之书?");
		
		// 创建物品
		ItemStack item = new ItemStack(Material.BARRIER); {
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("§4确认删除");
			meta.setLore(Arrays.asList(
					"", 
					"§f§m                       ",
					"§7 创建者: §8" + SpaceBook.getSpaceBookData().getString(bookID + ".Ownder"),
					"§7 回复数: §8" + SpaceBook.getSpaceBookData().getConfigurationSection(bookID + ".Reply").getKeys(false).size(),
					"§f§m                       ",
					"", 
					"§c删除后无法撤销", 
					"§c该时空之书将会 §4§l永久性 §c移除"));
			
			item.setItemMeta(meta);
			inventory.setItem(4, item);
		}
		// 打开背包
		player.openInventory(inventory);
	}
	
	/**
	 * 为玩家打开时空之书的界面
	 * 
	 * @param bookID 序列号
	 */
	public static void openInventory(Player player, String bookID, int page) {
		// 如果正在打开中
		if (player.hasMetadata("spacebook-open")) {
			SpaceBook.getLanguage().get("OPEN-WAIT").send(player);
			return;
		}
		else {
			SpaceBook.getLanguage().get("OPEN-PRE").send(player);
			player.setMetadata("spacebook-open", new FixedMetadataValue(SpaceBook.getInst(), true));
		}
		
		// 执行任务
		new BukkitRunnable() {
			
			SimpleDateFormat dateFormat = new SimpleDateFormat(SpaceBook.getInst().getConfig().getString("Settings.dateformat"));
			
			@Override
			public void run() {
				// 检查序列号是否存在
				if (!SpaceBook.getSpaceBookData().contains(bookID)) {
					player.removeMetadata("spacebook-open", SpaceBook.getInst());
					return;
				}
				
				// 创建背包
				SpaceBookHolder holder = new SpaceBookHolder(bookID, page);
				Inventory inventory = Bukkit.createInventory(holder, 54, SpaceBook.getLanguage().get("MENU-TITLE").asString());
				
				// 信息按钮
				ItemStack info = ItemUtils.loadItem(SpaceBook.getInst().getConfig(), "Settings.infoitem", null); {
					ItemMeta meta = info.getItemMeta();
					List<String> lore = meta.getLore();
					for (int i = 0 ; i < lore.size() ; i++) {
						lore.set(i, lore.get(i)
								.replace("$date", dateFormat.format(Long.valueOf(bookID)))
								.replace("$ownder", SpaceBook.getSpaceBookData().getString(bookID + ".Ownder")));
					}
					meta.setLore(lore);
					meta.setDisplayName(meta.getDisplayName() + (PlayerUtils.isAdminMode(player) ? "§4 [点击删除]" : ""));
					info.setItemMeta(meta);
					inventory.setItem(49, info);
				}
				
				// 翻页按钮
				inventory.setItem(47, ItemUtils.loadItem(SpaceBook.getInst().getConfig(), "Settings.backpage", null));
				inventory.setItem(51, ItemUtils.loadItem(SpaceBook.getInst().getConfig(), "Settings.nextpage", null));
				
				// 如果没有留言
				if (!SpaceBook.getSpaceBookData().contains(bookID + ".Reply") || SpaceBook.getSpaceBookData().getConfigurationSection(bookID + ".Reply").getKeys(false).size() == 0) {
					inventory.setItem(22, ItemUtils.loadItem(SpaceBook.getInst().getConfig(), "Settings.emptyitem", null));
				}
				else {
					int start = (page - 1) * 28;
					int end = page * 28;
					int loop = 0;
					
					Iterator<String> i = SpaceBook.getSpaceBookData().getConfigurationSection(bookID + ".Reply").getKeys(false).iterator();
					while (i.hasNext()) {
						// 获取信息
						String replyID = i.next();
						// 判断位置
						if (loop >= start) {
							if (loop < end) {
								// 获取信息
								String playerName = SpaceBook.getSpaceBookData().getString(bookID + ".Reply." + replyID + ".Player");
								String message = SpaceBook.getSpaceBookData().getString(bookID + ".Reply." + replyID + ".Message");
								
								// 留言按钮
								ItemStack reply = ItemUtils.loadItem(SpaceBook.getInst().getConfig(), "Settings.messageitem", null); {
									ItemMeta meta = reply.getItemMeta();
									List<String> lore = meta.getLore();
									
									// 替换原本描述
									for (int j = 0 ; j < lore.size() ; j++) {
										lore.set(j, lore.get(j)
												.replace("$date", dateFormat.format(Long.valueOf(replyID)))
												.replace("$ownder", playerName));
									}
									// 添加回复描述
									for (String _message : message.split("%n%")) {
										lore.add(SpaceBook.getInst().getConfig().getString("Settings.replycolor") + _message.replace("&", "§"));
									}
									
									// 设置描述和名称
									meta.setLore(lore);
									meta.setDisplayName(meta.getDisplayName().replace("$ownder", playerName) + (PlayerUtils.isAdminMode(player) ? "§4 [点击删除]" : ""));
									reply.setItemMeta(meta);
									
									// 设置物品
									inventory.setItem(InventoryUtil.SLOT_OF_CENTENTS.get(loop - start), reply);
									// 设置数据
									holder.getReplyData().put(InventoryUtil.SLOT_OF_CENTENTS.get(loop - start), replyID);
								}
							}
							else {
								break;
							}
						}
						loop++;
					}
				}
				
				// 切回主线程打开背包
				new BukkitRunnable() {
					
					@Override
					public void run() {
						player.openInventory(inventory);
						player.removeMetadata("spacebook-open", SpaceBook.getInst());
					}
				}.runTask(SpaceBook.getInst());
			}
		}.runTaskAsynchronously(SpaceBook.getInst());
	}
	
	/**
	 * 创建回复引导
	 * 
	 * @param player 玩家
	 * @param bookID 序列号
	 */
	@SuppressWarnings("unchecked")
	public static void createReplyGuide(Player player, String bookID) {
		// 检查序列号是否存在
		if (!SpaceBook.getSpaceBookData().contains(bookID)) {
			return;
		}
		
		// 检查玩家是否有时空碎片
		if (!InventoryUtil.hasItem(player, getReplyItem(), 1, true)) {
			SpaceBook.getLanguage().get("REPLY-EMPTY").send(player);
			return;
		}
		else {
			// 检查是否有其他引导器
			try {
				Field field = ChatCatcher.class.getDeclaredFields()[0];
				field.setAccessible(true);
				HashMap<String, LinkedList<Catcher>> map = (HashMap<String, LinkedList<Catcher>>) field.get(new ChatCatcher());
				if (map.containsKey(player.getName()) && map.get(player.getName()).size() > 0) {
					SpaceBook.getLanguage().get("REPLY-CATCHER-ERROR").send(player);
					return;
				}
			}
			catch (Exception e) {
				// TODO: handle exceptions
				e.printStackTrace();
			}
			
			// 回复信息
			List<String> message = new ArrayList<>();
		
			// 聊天捕捉器
			ChatCatcher.call(player, new ChatCatcher.Catcher() {
				
				@Override
				public void cancel() {
					// 返回物品
					player.getInventory().addItem(getReplyItem());
					// 退出引导
					PlayerUtils.clearScreen(player, 30);
					SpaceBook.getLanguage().get("REPLY-QUIT").send(player);
				}
				
				@Override
				public Catcher before() {
					JSONFormatter json = new JSONFormatter();
					// 分界线
					json.append(SpaceBook.getLanguage().get("REPLY-HEAD").asString());
					json.newLine();
					json.append(SpaceBook.getLanguage().get("REPLY-TITLE").asString());
					json.newLine();
					
					// 循环信息
					for (int i = 0 ; i < message.size() && i < SpaceBook.getInst().getConfig().getInt("Settings.limitline") ; i++) {
						// 编辑
						json.appendHoverClick(
								SpaceBook.getLanguage().get("REPLY-BUTTON-EDIT").asString(), 
								new ShowTextEvent(SpaceBook.getLanguage().get("REPLY-BUTTON-EDIT-TEXT").asString()), 
								new SuggestCommandEvent("edit " + i + " " + message.get(i)));
						// 删除
						json.appendHoverClick(
								SpaceBook.getLanguage().get("REPLY-BUTTON-DELETE").asString(), 
								new ShowTextEvent(SpaceBook.getLanguage().get("REPLY-BUTTON-DELETE-TEXT").asString()), 
								new SuggestCommandEvent("delete " + i));
						// 文本
						json.append(" §f| " + SpaceBook.getInst().getConfig().getString("Settings.replycolor") + message.get(i).replace("&", "§"));
						json.newLine();
					}
					
					//分界线
					json.append(SpaceBook.getLanguage().get("REPLY-TITLE").asString());
					json.newLine();
					
					// 完成
					json.appendHoverClick(
							SpaceBook.getLanguage().get("REPLY-BUTTON-SUCCESS").asString(), 
							new ShowTextEvent(SpaceBook.getLanguage().get("REPLY-BUTTON-SUCCESS-TEXT").asString()), 
							new SuggestCommandEvent("success"));
					// 撤销
					json.appendHoverClick(
							SpaceBook.getLanguage().get("REPLY-BUTTON-UNDO").asString(), 
							new ShowTextEvent(SpaceBook.getLanguage().get("REPLY-BUTTON-UNDO-TEXT").asString()), 
							new SuggestCommandEvent("quit()"));
					
					// 下一行
					if (message.size() < SpaceBook.getInst().getConfig().getInt("Settings.limitline")) {
						json.append(" §f| ");
						json.appendHoverClick(
								SpaceBook.getLanguage().get("REPLY-BUTTON-ADDLINE").asString(), 
								new ShowTextEvent(SpaceBook.getLanguage().get("REPLY-BUTTON-ADDLINE-TEXT").asString()), 
								new SuggestCommandEvent("addline 内容"));
					}
					
					//分界线
					json.newLine();
					json.append(SpaceBook.getLanguage().get("REPLY-TITLE").asString());
					
					// 发送
					PlayerUtils.clearScreen(player, 30);
					json.send(player);
					return this;
				}
				
				@Override
				public boolean after(String args) {
					if (args.startsWith("addline") && args.split(" ").length > 1 && message.size() < SpaceBook.getInst().getConfig().getInt("Settings.limitline")) {
						message.add(subString(args.replace("addline ", "")));
					}
					else if (args.startsWith("edit") && args.split(" ").length > 2) {
						int line = NumberUtils.getInteger(args.split(" ")[1]);
						// 文本行数 > 选择行数
						if (message.size() > line) {
							message.set(line, subString(args.replaceAll("edit (\\S+) ", "")));
						}
					}
					else if (args.startsWith("delete") && args.split(" ").length > 1) {
						int line = NumberUtils.getInteger(args.split(" ")[1]);
						// 文本行数 > 选择行数
						if (message.size() > line) {
							message.remove(line);
						}
					}
					else if (args.startsWith("success") && message.size() > 0) {
						// 合并文本
						StringBuilder sb = new StringBuilder();
						for (String msg : message) {
							sb.append(msg + "%n%");
						}
						
						// 检查序列号是否存在
						if (!SpaceBook.getSpaceBookData().contains(bookID)) {
							// 返回物品
							player.getInventory().addItem(getReplyItem());
							// 退出引导
							PlayerUtils.clearScreen(player, 30);
							SpaceBook.getLanguage().get("REPLY-NULL").send(player);
						}
						else {
							// 添加文本
							String time = String.valueOf(System.currentTimeMillis());
							SpaceBook.getSpaceBookData().set(bookID + ".Reply." + time + ".Player", player.getName());
							SpaceBook.getSpaceBookData().set(bookID + ".Reply." + time + ".Message", sb.substring(0, sb.length() - 3));
							
							// 提示信息
							PlayerUtils.clearScreen(player, 30);
							SpaceBook.getLanguage().get("REPLY-SUCCESS").send(player);
						}
						return false;
					}
					return true;
				}
				
				/**
				 * 砍掉超出限制的文本
				 * 
				 * @param reply
				 * @return
				 */
				private String subString(String reply) {
					if (reply.length() > SpaceBook.getInst().getConfig().getInt("Settings.lengthline")) {
						reply = reply.substring(0, reply.length() - (reply.length() - SpaceBook.getInst().getConfig().getInt("Settings.lengthline")));
					}
					return reply;
				}
			});
		}
	}
}
