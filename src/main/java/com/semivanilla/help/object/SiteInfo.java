package com.semivanilla.help.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class SiteInfo { //can't think of a better name
    private String name;
    private Material material;
    private List<String> lore;
    private List<String> commands;
}
