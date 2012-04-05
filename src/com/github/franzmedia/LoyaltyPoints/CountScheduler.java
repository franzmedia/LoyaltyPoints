package com.github.franzmedia.LoyaltyPoints;
import java.util.Date;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CountScheduler implements Runnable {
	private LoyaltyPoints plugin;
	private long updateTimer;

	public CountScheduler(LoyaltyPoints isCool) {
		updateTimer = new Date().getTime();
		plugin = isCool;
	}
	
	/*
	 * update timer! in an attempt to save system resources, this plugin has
	 * only one timer that tracks when to check all updates. this is also in
	 * seconds, and must be less than or equal to the cycle-time-in-seconds the
	 * less the number, updates are checked more often, but more resources are
	 * used the more the number, the updates are checked less often, but less
	 * resources are used.
	 */
	public void run() {
		
		
		Long now = new Date().getTime();
		
		if(now - updateTimer >= (plugin.getUpdateTimer()*1000)){
			LPFileManager.save();
			updateTimer = now;
		}
		int rest = 0;
		int cycle = plugin.getCycleNumber()*1000;
		for (Player player : plugin.getServer().getOnlinePlayers()) {
			String m = player.getName();
			if ((now - plugin.getTimeComparison().get(m)) >= cycle) { // cycleNumber amount of seconds has passed
				rest = (int) (now - plugin.getTimeComparison().get(m))-cycle;
				plugin.getLoyaltyPoints().put(m, plugin.getLoyaltyPoints().get(m) + plugin.getIncrement());
				plugin.getTimeComparison().put(m, (now+rest));
				plugin.debug("loyalt before: "+ plugin.getLoyaltTime().get(m));
				plugin.getLoyaltTime().put(m, rest);
				plugin.debug("loyalt after: "+ plugin.getLoyaltTime().get(m));		
		}else{
			plugin.getLoyaltTime().put(m, (plugin.getLoyaltTime().get(m)+ (int) ((now - plugin.getLoyaltStart().get(m))/1000) ));	
		}
			plugin.debug("running now:"+ now + " timecomparison: " + plugin.getTimeComparison().get(m) +"DIF: "+(now - plugin.getTimeComparison().get(m)) + " cycle:" + cycle );
			plugin.getLoyaltTotalTime().put(m, (plugin.getLoyaltTotalTime().get(m)+ (int) ((now - plugin.getLoyaltStart().get(m))/1000) ));
			plugin.getLoyaltStart().put(m, now);
		}	
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new CountScheduler(plugin), (long) plugin.getUpdateTimer());
	}
	
}
