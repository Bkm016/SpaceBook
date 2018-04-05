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
 * @since 2018��2��3�� ����1:04:06
 */
public class SpaceBookAPI {
	
	/**
	 * ��TabooLib ��Ʒ���л�ȡʱ��֮��, �����Ʒ�������򷵻�ʯͷ
	 * 
	 * @return {@link ItemStack}
	 */
	public static ItemStack getBookItem() {
		ItemStack item = ItemUtils.getCacheItem(SpaceBook.getInst().getConfig().getString("Settings.bookitem"));
		return item == null ? new ItemStack(Material.STONE) : item.clone();
	}
	
	/**
	 * ��TabooLib ��Ʒ���л�ȡʱ����Ƭ, �����Ʒ�������򷵻�ʯͷ
	 * 
	 * @return {@link ItemStack}
	 */
	public static ItemStack getReplyItem() {
		ItemStack item = ItemUtils.getCacheItem(SpaceBook.getInst().getConfig().getString("Settings.beplyitem"));
		return item == null ? new ItemStack(Material.STONE) : item.clone();
	}
	
	/**
	 * �����Ʒ�Ƿ�Ϊʱ��֮��
	 * 
	 * @param item ��Ʒ
	 * @return {@link Boolean}
	 */
	public static boolean isBookItem(ItemStack item) {
		if (item == null || item.getType().equals(Material.AIR)) {
			return false;
		}
		return getBookItem().isSimilar(item);
	}
	
	/**
	 * �����Ʒ�Ƿ�Ϊʱ����Ƭ
	 * 
	 * @param item ��Ʒ
	 * @return {@link Boolean}
	 */
	public static boolean isReplyItem(ItemStack item) {
		if (item == null || item.getType().equals(Material.AIR)) {
			return false;
		}
		return getReplyItem().isSimilar(item);
	}
	
	/**
	 * ��鸽���Ƿ�������ʱ��֮��
	 * 
	 * @param checkLocation �������
	 * @return {@link Boolean}
	 */
	public static boolean isBookNear(Location checkLocation) {
		for (String bookID : SpaceBook.getSpaceBookData().getConfigurationSection("").getKeys(false)) {
			Location location = (Location) SpaceBook.getSpaceBookData().get(bookID + ".Location");
			
			// �ȼ������������
			if (location.getWorld().getName().equals(checkLocation.getWorld().getName()) && location.distance(checkLocation) <= SpaceBook.getInst().getConfig().getInt("Settings.effectiveredius")) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * ��ȡʱ��֮�����ҳ��
	 * 
	 * @param bookID ���к�
	 * @return {@link Integer}
	 */
	public static int getMaxPage(String bookID) {
		// ������к��Ƿ���� �� û�лظ�
		if (!SpaceBook.getSpaceBookData().contains(bookID) 
				|| !SpaceBook.getSpaceBookData().contains(bookID + ".Reply") 
				|| SpaceBook.getSpaceBookData().getConfigurationSection(bookID + ".Reply").getKeys(false).size() == 0) {
			return 1;
		}
		return (int) Math.ceil(SpaceBook.getSpaceBookData().getConfigurationSection(bookID + ".Reply").getKeys(false).size() / 28D);
	}
	
	/**
	 * ����ʱ��֮������
	 * 
	 * @param location ����
	 * @param ownder ������
	 */
	public static void createBook(Location location, String ownder) {
		String bookID = String.valueOf(System.currentTimeMillis());
		// ��������
		SpaceBook.getSpaceBookData().set(bookID + ".Ownder", ownder);
		SpaceBook.getSpaceBookData().set(bookID + ".Location", location);
		SpaceBook.getSpaceBookData().createSection(bookID + ".Reply");
		// ʵ��ʱ��֮��
		createBook(bookID);
	}
	
	/**
	 * ʵ��ʱ��֮�飨���Ѵ��ڵ�ʱ��֮�鴴�����������У�
	 * 
	 * @param bookID ���к�
	 */
	@SuppressWarnings("deprecation")
	public static void createBook(String bookID) {
		// ������к��Ƿ����
		if (!SpaceBook.getSpaceBookData().contains(bookID)) {
			return;
		}
		// ����Ƿ��Ѿ���ʵ��
		if (SpaceBook.getHologramsData().containsKey(bookID)) {
			Hologram holo = SpaceBook.getHologramsData().get(bookID);
			// ɾ������ȫϢ
			if (!holo.isDeleted()) {
				holo.delete();
			}
		}
		// �����µ�ȫϢ
		CraftHologram holo = (CraftHologram) HologramsAPI.createHologram(SpaceBook.getInst(), (Location) SpaceBook.getSpaceBookData().get(bookID + ".Location"));
		// �����Ʒ
		holo.appendItemLine(new ItemStack(ItemUtils.asMaterial(SpaceBook.getInst().getConfig().getString("Settings.hologram.icon"))));
		// ����ı�
		for (String line : SpaceBook.getInst().getConfig().getStringList("Settings.hologram.text")) {
			holo.appendTextLine(line.replace("&", "��"));
		}
		
		// ��ӵ��������
		holo.setTouchHandler(new SpaceTouchHandler(bookID));
		SpaceBook.getHologramsData().put(bookID, holo);
	}
	
	/**
	 * ɾ��ʱ��֮��
	 * 
	 * @param bookID ���к�
	 */
	public static void deleteBook(String bookID) {
		// ɾ������
		SpaceBook.getSpaceBookData().set(bookID, null);
		// ɾ��ȫϢ
		if (SpaceBook.getHologramsData().containsKey(bookID)) {
			Hologram holo = SpaceBook.getHologramsData().get(bookID);
			if (!holo.isDeleted()) {
				holo.delete();
			}
		}
	}
	
	/**
	 * ��ʱ��֮��ɾ������
	 * 
	 * @param player
	 * @param bookID
	 */
	public static void openInventoryAdmin(Player player, String bookID) {
		// ��������
		Inventory inventory = Bukkit.createInventory(new SpaceBookDeleteHolder(bookID), 9, "ȷ��ɾ����ǰʱ��֮��?");
		
		// ������Ʒ
		ItemStack item = new ItemStack(Material.BARRIER); {
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("��4ȷ��ɾ��");
			meta.setLore(Arrays.asList(
					"", 
					"��f��m                       ",
					"��7 ������: ��8" + SpaceBook.getSpaceBookData().getString(bookID + ".Ownder"),
					"��7 �ظ���: ��8" + SpaceBook.getSpaceBookData().getConfigurationSection(bookID + ".Reply").getKeys(false).size(),
					"��f��m                       ",
					"", 
					"��cɾ�����޷�����", 
					"��c��ʱ��֮�齫�� ��4��l������ ��c�Ƴ�"));
			
			item.setItemMeta(meta);
			inventory.setItem(4, item);
		}
		// �򿪱���
		player.openInventory(inventory);
	}
	
	/**
	 * Ϊ��Ҵ�ʱ��֮��Ľ���
	 * 
	 * @param bookID ���к�
	 */
	public static void openInventory(Player player, String bookID, int page) {
		// ������ڴ���
		if (player.hasMetadata("spacebook-open")) {
			SpaceBook.getLanguage().get("OPEN-WAIT").send(player);
			return;
		}
		else {
			SpaceBook.getLanguage().get("OPEN-PRE").send(player);
			player.setMetadata("spacebook-open", new FixedMetadataValue(SpaceBook.getInst(), true));
		}
		
		// ִ������
		new BukkitRunnable() {
			
			SimpleDateFormat dateFormat = new SimpleDateFormat(SpaceBook.getInst().getConfig().getString("Settings.dateformat"));
			
			@Override
			public void run() {
				// ������к��Ƿ����
				if (!SpaceBook.getSpaceBookData().contains(bookID)) {
					player.removeMetadata("spacebook-open", SpaceBook.getInst());
					return;
				}
				
				// ��������
				SpaceBookHolder holder = new SpaceBookHolder(bookID, page);
				Inventory inventory = Bukkit.createInventory(holder, 54, SpaceBook.getLanguage().get("MENU-TITLE").asString());
				
				// ��Ϣ��ť
				ItemStack info = ItemUtils.loadItem(SpaceBook.getInst().getConfig(), "Settings.infoitem", null); {
					ItemMeta meta = info.getItemMeta();
					List<String> lore = meta.getLore();
					for (int i = 0 ; i < lore.size() ; i++) {
						lore.set(i, lore.get(i)
								.replace("$date", dateFormat.format(Long.valueOf(bookID)))
								.replace("$ownder", SpaceBook.getSpaceBookData().getString(bookID + ".Ownder")));
					}
					meta.setLore(lore);
					meta.setDisplayName(meta.getDisplayName() + (PlayerUtils.isAdminMode(player) ? "��4 [���ɾ��]" : ""));
					info.setItemMeta(meta);
					inventory.setItem(49, info);
				}
				
				// ��ҳ��ť
				inventory.setItem(47, ItemUtils.loadItem(SpaceBook.getInst().getConfig(), "Settings.backpage", null));
				inventory.setItem(51, ItemUtils.loadItem(SpaceBook.getInst().getConfig(), "Settings.nextpage", null));
				
				// ���û������
				if (!SpaceBook.getSpaceBookData().contains(bookID + ".Reply") || SpaceBook.getSpaceBookData().getConfigurationSection(bookID + ".Reply").getKeys(false).size() == 0) {
					inventory.setItem(22, ItemUtils.loadItem(SpaceBook.getInst().getConfig(), "Settings.emptyitem", null));
				}
				else {
					int start = (page - 1) * 28;
					int end = page * 28;
					int loop = 0;
					
					Iterator<String> i = SpaceBook.getSpaceBookData().getConfigurationSection(bookID + ".Reply").getKeys(false).iterator();
					while (i.hasNext()) {
						// ��ȡ��Ϣ
						String replyID = i.next();
						// �ж�λ��
						if (loop >= start) {
							if (loop < end) {
								// ��ȡ��Ϣ
								String playerName = SpaceBook.getSpaceBookData().getString(bookID + ".Reply." + replyID + ".Player");
								String message = SpaceBook.getSpaceBookData().getString(bookID + ".Reply." + replyID + ".Message");
								
								// ���԰�ť
								ItemStack reply = ItemUtils.loadItem(SpaceBook.getInst().getConfig(), "Settings.messageitem", null); {
									ItemMeta meta = reply.getItemMeta();
									List<String> lore = meta.getLore();
									
									// �滻ԭ������
									for (int j = 0 ; j < lore.size() ; j++) {
										lore.set(j, lore.get(j)
												.replace("$date", dateFormat.format(Long.valueOf(replyID)))
												.replace("$ownder", playerName));
									}
									// ��ӻظ�����
									for (String _message : message.split("%n%")) {
										lore.add(SpaceBook.getInst().getConfig().getString("Settings.replycolor") + _message.replace("&", "��"));
									}
									
									// ��������������
									meta.setLore(lore);
									meta.setDisplayName(meta.getDisplayName().replace("$ownder", playerName) + (PlayerUtils.isAdminMode(player) ? "��4 [���ɾ��]" : ""));
									reply.setItemMeta(meta);
									
									// ������Ʒ
									inventory.setItem(InventoryUtil.SLOT_OF_CENTENTS.get(loop - start), reply);
									// ��������
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
				
				// �л����̴߳򿪱���
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
	 * �����ظ�����
	 * 
	 * @param player ���
	 * @param bookID ���к�
	 */
	@SuppressWarnings("unchecked")
	public static void createReplyGuide(Player player, String bookID) {
		// ������к��Ƿ����
		if (!SpaceBook.getSpaceBookData().contains(bookID)) {
			return;
		}
		
		// �������Ƿ���ʱ����Ƭ
		if (!InventoryUtil.hasItem(player, getReplyItem(), 1, true)) {
			SpaceBook.getLanguage().get("REPLY-EMPTY").send(player);
			return;
		}
		else {
			// ����Ƿ�������������
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
			
			// �ظ���Ϣ
			List<String> message = new ArrayList<>();
		
			// ���첶׽��
			ChatCatcher.call(player, new ChatCatcher.Catcher() {
				
				@Override
				public void cancel() {
					// ������Ʒ
					player.getInventory().addItem(getReplyItem());
					// �˳�����
					PlayerUtils.clearScreen(player, 30);
					SpaceBook.getLanguage().get("REPLY-QUIT").send(player);
				}
				
				@Override
				public Catcher before() {
					JSONFormatter json = new JSONFormatter();
					// �ֽ���
					json.append(SpaceBook.getLanguage().get("REPLY-HEAD").asString());
					json.newLine();
					json.append(SpaceBook.getLanguage().get("REPLY-TITLE").asString());
					json.newLine();
					
					// ѭ����Ϣ
					for (int i = 0 ; i < message.size() && i < SpaceBook.getInst().getConfig().getInt("Settings.limitline") ; i++) {
						// �༭
						json.appendHoverClick(
								SpaceBook.getLanguage().get("REPLY-BUTTON-EDIT").asString(), 
								new ShowTextEvent(SpaceBook.getLanguage().get("REPLY-BUTTON-EDIT-TEXT").asString()), 
								new SuggestCommandEvent("edit " + i + " " + message.get(i)));
						// ɾ��
						json.appendHoverClick(
								SpaceBook.getLanguage().get("REPLY-BUTTON-DELETE").asString(), 
								new ShowTextEvent(SpaceBook.getLanguage().get("REPLY-BUTTON-DELETE-TEXT").asString()), 
								new SuggestCommandEvent("delete " + i));
						// �ı�
						json.append(" ��f| " + SpaceBook.getInst().getConfig().getString("Settings.replycolor") + message.get(i).replace("&", "��"));
						json.newLine();
					}
					
					//�ֽ���
					json.append(SpaceBook.getLanguage().get("REPLY-TITLE").asString());
					json.newLine();
					
					// ���
					json.appendHoverClick(
							SpaceBook.getLanguage().get("REPLY-BUTTON-SUCCESS").asString(), 
							new ShowTextEvent(SpaceBook.getLanguage().get("REPLY-BUTTON-SUCCESS-TEXT").asString()), 
							new SuggestCommandEvent("success"));
					// ����
					json.appendHoverClick(
							SpaceBook.getLanguage().get("REPLY-BUTTON-UNDO").asString(), 
							new ShowTextEvent(SpaceBook.getLanguage().get("REPLY-BUTTON-UNDO-TEXT").asString()), 
							new SuggestCommandEvent("quit()"));
					
					// ��һ��
					if (message.size() < SpaceBook.getInst().getConfig().getInt("Settings.limitline")) {
						json.append(" ��f| ");
						json.appendHoverClick(
								SpaceBook.getLanguage().get("REPLY-BUTTON-ADDLINE").asString(), 
								new ShowTextEvent(SpaceBook.getLanguage().get("REPLY-BUTTON-ADDLINE-TEXT").asString()), 
								new SuggestCommandEvent("addline ����"));
					}
					
					//�ֽ���
					json.newLine();
					json.append(SpaceBook.getLanguage().get("REPLY-TITLE").asString());
					
					// ����
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
						// �ı����� > ѡ������
						if (message.size() > line) {
							message.set(line, subString(args.replaceAll("edit (\\S+) ", "")));
						}
					}
					else if (args.startsWith("delete") && args.split(" ").length > 1) {
						int line = NumberUtils.getInteger(args.split(" ")[1]);
						// �ı����� > ѡ������
						if (message.size() > line) {
							message.remove(line);
						}
					}
					else if (args.startsWith("success") && message.size() > 0) {
						// �ϲ��ı�
						StringBuilder sb = new StringBuilder();
						for (String msg : message) {
							sb.append(msg + "%n%");
						}
						
						// ������к��Ƿ����
						if (!SpaceBook.getSpaceBookData().contains(bookID)) {
							// ������Ʒ
							player.getInventory().addItem(getReplyItem());
							// �˳�����
							PlayerUtils.clearScreen(player, 30);
							SpaceBook.getLanguage().get("REPLY-NULL").send(player);
						}
						else {
							// ����ı�
							String time = String.valueOf(System.currentTimeMillis());
							SpaceBook.getSpaceBookData().set(bookID + ".Reply." + time + ".Player", player.getName());
							SpaceBook.getSpaceBookData().set(bookID + ".Reply." + time + ".Message", sb.substring(0, sb.length() - 3));
							
							// ��ʾ��Ϣ
							PlayerUtils.clearScreen(player, 30);
							SpaceBook.getLanguage().get("REPLY-SUCCESS").send(player);
						}
						return false;
					}
					return true;
				}
				
				/**
				 * �����������Ƶ��ı�
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
