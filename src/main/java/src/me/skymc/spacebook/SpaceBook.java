package me.skymc.spacebook;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import lombok.Getter;
import me.skymc.spacebook.api.SpaceBookAPI;
import me.skymc.spacebook.inventory.SpaceBookCreateHolder;
import me.skymc.spacebook.inventory.SpaceBookDeleteHolder;
import me.skymc.spacebook.inventory.SpaceBookHolder;
import me.skymc.spacebook.listener.ListenerInventory;
import me.skymc.spacebook.listener.ListenerPlayer;
import me.skymc.taboolib.playerdata.DataUtils;
import me.skymc.taboolib.string.language2.Language2;

/**
 * �������
 * 
 * @author sky
 * @since 2018��2��3�� ����12:40:11
 */
public class SpaceBook extends JavaPlugin {
	
	@Getter
	private static SpaceBook inst;
	
	@Getter
	private static Language2 language;
	
	@Getter
	private static FileConfiguration spaceBookData;
	
	@Getter
	private static HashMap<String, Hologram> hologramsData = new HashMap<>();
	
	@Override
	public void onLoad() {
		inst = this;
		saveDefaultConfig();
	}
	
	@Override
	public void onEnable() {
		// ���������ļ�
		language = new Language2(this);
		// ���������ļ�
		spaceBookData = DataUtils.addPluginData("data", this);
		
		// ע�������
		registerListener();
		// ����ʱ��֮��
		Bukkit.getScheduler().runTask(this, () -> reloadSpaceBook());
	}
	
	@Override
	public void onDisable() {
		// �ر�����ʱ��֮�����
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getOpenInventory().getTopInventory().getHolder() instanceof SpaceBookHolder
					|| player.getOpenInventory().getTopInventory().getHolder() instanceof SpaceBookCreateHolder
					|| player.getOpenInventory().getTopInventory().getHolder() instanceof SpaceBookDeleteHolder) {
				player.closeInventory();
			}
		}
		// ע�����������ȫϢ
		for (Hologram holo : HologramsAPI.getHolograms(this)) {
			if (!holo.isDeleted()) {
				holo.delete();
			}
		}
	}
	
	/**
	 * ��������ʱ��֮��
	 */
	public void reloadSpaceBook() {
		for (String bookID : getSpaceBookData().getConfigurationSection("").getKeys(false)) {
			SpaceBookAPI.createBook(bookID);
		}
	}
	
	/**
	 * ע�������
	 */
	private void registerListener() {
		Bukkit.getPluginManager().registerEvents(new ListenerPlayer(), this);
		Bukkit.getPluginManager().registerEvents(new ListenerInventory(), this);
	}
}
