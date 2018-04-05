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
 * 插件主类
 * 
 * @author sky
 * @since 2018年2月3日 下午12:40:11
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
		// 载入语言文件
		language = new Language2(this);
		// 载入数据文件
		spaceBookData = DataUtils.addPluginData("data", this);
		
		// 注册监听器
		registerListener();
		// 载入时空之书
		Bukkit.getScheduler().runTask(this, () -> reloadSpaceBook());
	}
	
	@Override
	public void onDisable() {
		// 关闭所有时空之书界面
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getOpenInventory().getTopInventory().getHolder() instanceof SpaceBookHolder
					|| player.getOpenInventory().getTopInventory().getHolder() instanceof SpaceBookCreateHolder
					|| player.getOpenInventory().getTopInventory().getHolder() instanceof SpaceBookDeleteHolder) {
				player.closeInventory();
			}
		}
		// 注销本插件所有全息
		for (Hologram holo : HologramsAPI.getHolograms(this)) {
			if (!holo.isDeleted()) {
				holo.delete();
			}
		}
	}
	
	/**
	 * 重载所有时空之书
	 */
	public void reloadSpaceBook() {
		for (String bookID : getSpaceBookData().getConfigurationSection("").getKeys(false)) {
			SpaceBookAPI.createBook(bookID);
		}
	}
	
	/**
	 * 注册监听器
	 */
	private void registerListener() {
		Bukkit.getPluginManager().registerEvents(new ListenerPlayer(), this);
		Bukkit.getPluginManager().registerEvents(new ListenerInventory(), this);
	}
}
