package com.semivanilla.help.menus;

import com.semivanilla.help.manager.ConfigManager;
import com.semivanilla.help.object.SiteInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.badbird5907.blib.menu.buttons.Button;
import net.badbird5907.blib.menu.buttons.PlaceholderButton;
import net.badbird5907.blib.menu.menu.Menu;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MainMenu extends Menu {
    @Getter
    private static final MainMenu instance = new MainMenu();

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        int i = 10;
        for (SiteInfo siteInfo : ConfigManager.getInfo()) {
            buttons.add(new InfoButton(siteInfo, i++));
        }
        buttons.add(new Placeholders());
        return buttons;
    }

    @Override
    public String getName(Player player) {
        return CC.translate(ConfigManager.getMenuName());
    }

    private class Placeholders extends PlaceholderButton {
        @Override
        public int[] getSlots() {
            return genPlaceholderSpots(IntStream.range(0, 27), 10, 11, 12, 13, 14, 15, 16);
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(CC.AQUA).build();
        }
    }

    @RequiredArgsConstructor
    private class InfoButton extends Button {
        private final SiteInfo info;
        private final int i;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(info.getMaterial()).flag(Arrays.asList(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ENCHANTS))
                    .setName(info.getName()).lore(info.getLore().stream().map(CC::translate).collect(Collectors.toList())).build();
        }

        @Override
        public int getSlot() {
            return i;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            for (String command : info.getCommands()) {
                player.performCommand(command);
            }
        }
    }
}
