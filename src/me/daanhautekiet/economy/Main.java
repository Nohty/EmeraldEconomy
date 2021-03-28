package me.daanhautekiet.economy;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {

  public Economy eco;

  @Override
  public void onEnable() {
    if (!setupEconomy()) {
      System.out.println("\u001B[31m" + "You must have Vault and an Economy Plugin Installed" + "\u001B[0m");
      getServer().getPluginManager().disablePlugin(this);
      return;
    }
    this.saveDefaultConfig();
    this.getCommand("EmeraldEconomy").setExecutor(new Emerald(this));
    this.getCommand("Deposit").setExecutor(new Emerald(this));
    this.getCommand("Withdraw").setExecutor(new Emerald(this));
  }

  @Override
  public void onDisable() {
    return;
  }

  private boolean setupEconomy() {
    RegisteredServiceProvider<Economy> economy = getServer().getServicesManager()
        .getRegistration(net.milkbowl.vault.economy.Economy.class);
    if (economy != null)
      eco = economy.getProvider();
    return (eco != null);
  }
}
