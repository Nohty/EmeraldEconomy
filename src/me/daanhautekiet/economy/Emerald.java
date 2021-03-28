package me.daanhautekiet.economy;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class Emerald implements CommandExecutor {

  private Main plugin;

  public Emerald(Main plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("You can't run this command in the console!");
      return true;
    }
    Player player = (Player) sender;
    if (label.equalsIgnoreCase("emeraldeconomy") || label.equalsIgnoreCase("ee")) {
      if (args.length == 0) {
        player.sendMessage(ChatColor.RED + "Usage: /EmeraldEconomy reload");
        return true;
      }
      reloadConfig(player);
      return true;
    } else if (label.equalsIgnoreCase("deposit") || label.equalsIgnoreCase("dep")) {
      if (args.length == 0) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("error-messages.deposit.no-amount-specified")));
        return true;
      }
      try {
        deposit(player, Integer.parseInt(args[0]));
      } catch (NumberFormatException e) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("error-messages.deposit.no-amount-specified")));
      }
      return true;
    } else if (label.equalsIgnoreCase("withdraw") || label.equalsIgnoreCase("with")) {
      if (args.length == 0) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("error-messages.withdraw.no-amount-specified")));
        return true;
      }
      try {
        withdraw(player, Integer.parseInt(args[0]));
      } catch (NumberFormatException e) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("error-messages.withdraw.no-amount-specified")));
      }
      return true;
    }
    return false;
  }

  private void withdraw(Player player, int amount) {
    if (!(player.hasPermission("emeraldeconomy.withdraw"))) {
      player.sendMessage(
          ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error-messages.no-permission")));
      return;
    }
    if (player.getInventory().firstEmpty() == -1) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&',
          plugin.getConfig().getString("error-messages.withdraw.full-inventory")));
      return;
    }
    if (!checkBalance(player, amount)) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&',
          plugin.getConfig().getString("error-messages.withdraw.no-money")));
      return;
    }
    if (withdrawFromPlayer(player, amount)) {
      player.getInventory().addItem(getItem(amount));
      player.sendMessage(ChatColor.GOLD + "You have withdrawn " + plugin.getConfig().getString("money.symbol")
          + Integer.toString(amount));
    }
  }

  private void deposit(Player player, int amount) {
    if (!(player.hasPermission("emeraldeconomy.deposit"))) {
      player.sendMessage(
          ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error-messages.no-permission")));
      return;
    }
    if (!checkItem(player, amount)) {
      player.sendMessage(
          ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error-messages.deposit.no-items")));
      return;
    }
    if (amount > 64)
      amount = 64;
    if (depositToPlayer(player, amount)) {
      player.getInventory().getItemInMainHand()
          .setAmount(player.getInventory().getItemInMainHand().getAmount() - amount);
      player.sendMessage(ChatColor.GOLD + "You have deposit " + plugin.getConfig().getString("money.symbol")
          + Integer.toString(amount));
    }
  }

  private void reloadConfig(Player player) {
    if (!(player.hasPermission("emeraldeconomy.reload"))) {
      player.sendMessage(
          ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error-messages.no-permission")));
      return;
    }
    plugin.reloadConfig();
    player.sendMessage(ChatColor.GREEN + "Config reloaded");
  }

  private boolean checkBalance(Player player, int amount) {
    return (plugin.eco.getBalance(player) >= amount);
  }

  private boolean checkItem(Player player, int amount) {
    if (player.getInventory().getItemInMainHand().getType().equals(Material.EMERALD))
      if (player.getInventory().getItemInMainHand().getItemMeta().hasLore())
        if (player.getInventory().getItemInMainHand().getItemMeta().getLore().get(1)
            .equals(ChatColor.BLUE + "" + ChatColor.MAGIC + plugin.getConfig().getString("money.secret-word"))) {
          if (amount > 64) {
            amount = 64;
          }
          return (player.getInventory().getItemInMainHand().getAmount() >= amount);
        }
    return false;
  }

  private boolean withdrawFromPlayer(Player player, int amount) {
    return (plugin.eco.withdrawPlayer(player, amount).type == ResponseType.SUCCESS);
  }

  private boolean depositToPlayer(Player player, int amount) {
    return (plugin.eco.depositPlayer(player, amount).type == ResponseType.SUCCESS);
  }

  private ItemStack getItem(int amount) {
    ItemStack money = new ItemStack(Material.EMERALD, amount);
    ItemMeta meta = money.getItemMeta();
    meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + plugin.getConfig().getString("money.name"));
    List<String> lore = new ArrayList<String>();
    lore.add("");
    lore.add(ChatColor.BLUE + "" + ChatColor.MAGIC + plugin.getConfig().getString("money.secret-word"));
    meta.setLore(lore);
    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    money.setItemMeta(meta);
    return money;
  }
}
