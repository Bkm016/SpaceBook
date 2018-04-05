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
 * ��������
 * 
 * @author sky
 * @since 2018��2��3�� ����6:14:55
 */
public class ListenerInventory implements Listener {
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void click(InventoryClickEvent e) {
		// ��鱳������
		if (e.getInventory().getHolder() instanceof SpaceBookCreateHolder) {
			e.setCancelled(true);
			
			// ȷ�ϰ�ť
			if (e.getRawSlot() != 4) {
				return;
			}
			
			// ��鸽���Ƿ�������ʱ��֮��
			if (SpaceBookAPI.isBookNear(e.getWhoClicked().getLocation())) {
				SpaceBook.getLanguage().get("BOOK-NEAR").send(e.getWhoClicked());
				return;
			}
			
			// ɾ��������Ʒ
			if (e.getWhoClicked().getItemInHand().getAmount() > 1) {
				e.getWhoClicked().getItemInHand().setAmount(e.getWhoClicked().getItemInHand().getAmount() - 1);
			}
			else {
				e.getWhoClicked().setItemInHand(null);
			}
			
			// �رձ���
			e.getWhoClicked().closeInventory();
			// ��������
			SpaceBookAPI.createBook(e.getWhoClicked().getLocation().add(0, 2, 0), e.getWhoClicked().getName());
			// ȫ����ʾ
			SpaceBook.getLanguage().get("CREATE-MESSAGE").addPlaceholder("$player", e.getWhoClicked().getName()).broadcast();
		}
		else if (e.getInventory().getHolder() instanceof SpaceBookDeleteHolder) {
			e.setCancelled(true);
			// ��ȡ������Ϣ
			SpaceBookDeleteHolder holder = (SpaceBookDeleteHolder) e.getInventory().getHolder();
			
			// ɾ������
			if (e.getRawSlot() == 4) {
				// �رձ���
				e.getWhoClicked().closeInventory();
				// ��ʾ��Ϣ
				SpaceBook.getLanguage().get("BOOK-DELETE").send(e.getWhoClicked());
				// ɾ������
				SpaceBookAPI.deleteBook(holder.getBookID());
			}
		}
		else if (e.getInventory().getHolder() instanceof SpaceBookHolder) {
			e.setCancelled(true);
			// ��ȡ������Ϣ
			SpaceBookHolder holder = (SpaceBookHolder) e.getInventory().getHolder();
			
			// ������������Ϣ��ť
			if (e.getRawSlot() == 49) {
				// ����ǹ���Աģʽ
				if (PlayerUtils.isAdminMode((Player) e.getWhoClicked())) {
					// ��ɾ������
					SpaceBookAPI.openInventoryAdmin((Player) e.getWhoClicked(), holder.getBookID());
				}
				else {
					// �رձ���
					e.getWhoClicked().closeInventory();
					// �ظ�����
					SpaceBookAPI.createReplyGuide((Player) e.getWhoClicked(), holder.getBookID());
				}
			}
			
			// ��һҳ
			else if (e.getRawSlot() == 47) {
				// ����ǵ�һҳ
				if (holder.getPage() == 1) {
					return;
				}
				else {
					// ǰ����һҳ
					SpaceBookAPI.openInventory((Player) e.getWhoClicked(), holder.getBookID(), holder.getPage() - 1);
				}
			}
			
			// ��һҳ
			else if (e.getRawSlot() == 51) {
				// ��������һҳ
				if (holder.getPage() == SpaceBookAPI.getMaxPage(holder.getBookID())) {
					return;
				}
				else {
					// ǰ����һҳ
					SpaceBookAPI.openInventory((Player) e.getWhoClicked(), holder.getBookID(), holder.getPage() + 1);
				}
			}
			
			// �ظ���ť�ҹ���Աģʽ
			else if (e.getRawSlot() >= 10 && e.getRawSlot() <= 43 && PlayerUtils.isAdminMode((Player) e.getWhoClicked()) && holder.getReplyData().containsKey(e.getRawSlot())) {
				// ɾ���ظ�
				SpaceBook.getSpaceBookData().set(holder.getBookID() + ".Reply." + holder.getReplyData().get(e.getRawSlot()), null);
				// ��ʾ��Ϣ
				SpaceBook.getLanguage().get("MENU-DELETE").send(e.getWhoClicked());;
				// ���´�
				SpaceBookAPI.openInventory((Player) e.getWhoClicked(), holder.getBookID(), holder.getPage());
			}
		}
	}

}
