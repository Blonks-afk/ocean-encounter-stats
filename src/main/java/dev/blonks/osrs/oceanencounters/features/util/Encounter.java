package dev.blonks.osrs.oceanencounters.features.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.gameval.NpcID;

import java.util.List;

@RequiredArgsConstructor
@Getter
public enum Encounter {
    WIND("Strong Winds", List.of(
            NpcID.SAILING_CHANCE_ENCOUNTERS_WIND_HIGH,
            NpcID.SAILING_CHANCE_ENCOUNTERS_WIND_MED,
            NpcID.SAILING_CHANCE_ENCOUNTERS_WIND_LOW
    )),
    GLOW("Mysterious Glow", List.of(
            NpcID.SAILING_CHANCE_ENCOUNTERS_GLOW
    )),
    CRATE("Lost Crate", List.of(
            NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_WOOD,
            NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_OAK,
            NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_TEAK,
            NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_MAHOGANY,
            NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CAMPHOR,
            NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_IRONWOOD
    )),
    CASTAWAY("Castaway", List.of(
            NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_MAN1_ADRIFT,
//            NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_MAN1_PASSENGER,
            NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_MAN2_ADRIFT,
//            NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_MAN2_PASSENGER,
            NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_WOMAN1_ADRIFT,
//            NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_WOMAN1_PASSENGER,
            NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_WOMAN2_ADRIFT,
//            NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_WOMAN2_PASSENGER,
            NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_WOMAN3_ADRIFT,
//            NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_WOMAN3_PASSENGER,
            NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_WILSON_ADRIFT,
//            NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_WILSON_PASSENGER,
            NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_DOG1_ADRIFT
            //NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_DOG1_PASSENGER,
    )),
    CLAM("Giant Clam", List.of(
            NpcID.SAILING_CHANCE_ENCOUNTERS_CLAM_OPEN,
            NpcID.SAILING_CHANCE_ENCOUNTERS_CLAM_PEARL
    )),
    TURTLE("Clue Turtle", List.of(
            NpcID.SAILING_CHANCE_ENCOUNTER_CLUE_TURTLE
    )),
    MAN("Ocean Man", List.of(
            NpcID.SAILING_CHANCE_ENCOUNTERS_OCEAN_MAN
    )),
    CASKET("Lost Casket", List.of(
            NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CASKET_BEGINNER,
            NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CASKET_EASY,
            NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CASKET_MEDIUM,
            NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CASKET_HARD,
            NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CASKET_ELITE,
            NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CASKET_MASTER
    ));

    private final String name;
    private final List<Integer> npcIds;

    public static Encounter fromNpcId(int npcId) {
        for (Encounter type : Encounter.values()) {
            if (type.getNpcIds().contains(npcId)) {
                return type;
            }
        }
        return null;
    }
}
