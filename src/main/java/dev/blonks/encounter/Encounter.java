package dev.blonks.encounter;

import lombok.Getter;

public enum Encounter {
    WIND("Strong Winds"),
    GLOW("Mysterious Glow"),
    CRATE("Lost Crate"),
    CASTAWAY("Castaway"),
    CLAM("Giant Clam"),
    TURTLE("Clue Turtle"),
    MAN("Ocean Man"),
    CASKET("Lost Casket");

    Encounter(String name) {
        this.name = name;
    }
    @Getter
    private final String name;
}
