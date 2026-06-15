package dev.blonks.osrs.oceanencounters.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.gameval.NpcID;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum Encounter {
    WIND("Strong Winds", List.of(
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTERS_WIND_HIGH, "High"),
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTERS_WIND_MED, "Medium"),
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTERS_WIND_LOW, "Low")
    )),
    GLOW("Mysterious Glow", List.of(
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTERS_GLOW, "Mysterious Glow")
    )),
    CRATE("Lost Crate", List.of(
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_WOOD, "Wood"),
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_OAK, "Oak"),
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_TEAK, "Teak"),
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_MAHOGANY, "Mahogany"),
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CAMPHOR, "Camphor"),
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_IRONWOOD, "Ironwood")
    )),
    CASTAWAY("Castaway", List.of(
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_MAN1_ADRIFT, "Man 1"),
//            NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_MAN1_PASSENGER,
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_MAN2_ADRIFT, "Man 2"),
//            NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_MAN2_PASSENGER,
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_WOMAN1_ADRIFT, "Woman 1"),
//            NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_WOMAN1_PASSENGER,
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_WOMAN2_ADRIFT, "Woman 2"),
//            NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_WOMAN2_PASSENGER,
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_WOMAN3_ADRIFT, "Woman 3"),
//            NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_WOMAN3_PASSENGER,
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_WILSON_ADRIFT, "Wilson"),
//            NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_WILSON_PASSENGER,
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_DOG1_ADRIFT, "Dog")
            //NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_DOG1_PASSENGER,
    )),
    CLAM("Giant Clam", List.of(
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTERS_CLAM_OPEN, "Open"),
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTERS_CLAM_PEARL, "Pearl")
    )),
    TURTLE("Clue Turtle", List.of(
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTER_CLUE_TURTLE, "")
    )),
    MAN("Ocean Man", List.of(
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTERS_OCEAN_MAN, "")
    )),
    CASKET("Lost Casket", List.of(
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CASKET_BEGINNER, "Beginner"),
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CASKET_EASY, "Easy"),
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CASKET_MEDIUM, "Medium"),
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CASKET_HARD, "Hard"),
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CASKET_ELITE, "Elite"),
            new SubEncounterType(NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CASKET_MASTER, "Master")
    ));

    private final String name;
    private final List<SubEncounterType> subEncounters;

    public static Encounter fromNpcId(int npcId) {
        for (Encounter type : Encounter.values()) {
            Optional<SubEncounterType> opt = type.getSubEncounters().stream()
                    .filter(se -> se.getId() == npcId).findFirst();
            if (opt.isPresent()) {
                return type;
            }
        }
        return null;
    }

    public static SubEncounterType subTypeFromNpcId(int npcId) {
        for (Encounter type : Encounter.values()) {
            for (SubEncounterType se : type.getSubEncounters()) {
                if (se.getId() == npcId) {
                    return se;
                }
            }
        }
        return null;
    }

    public List<Integer> getNpcIds() {
        return subEncounters.stream().map(SubEncounterType::getId).collect(Collectors.toList());
    }
}
