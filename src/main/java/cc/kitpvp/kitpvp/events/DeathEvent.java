package cc.kitpvp.kitpvp.events;

import cc.kitpvp.kitpvp.player.Profile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class DeathEvent extends Event {
	private static final HandlerList HANDLER_LIST = new HandlerList();

	private final Player victim;
	private final Player killer;
	private final Profile killerProfile;

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

}
