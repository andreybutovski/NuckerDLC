package im.nucker.functions.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {

    Combat("Combat", "E"),
    Movement("Movement", "D"),
    Render("Render", "F"),
    Player("Player", "B"),
    Misc("Misc", "C"),
    Themes("Themes", "G");
    private final String name;
    private final String icon;


}
