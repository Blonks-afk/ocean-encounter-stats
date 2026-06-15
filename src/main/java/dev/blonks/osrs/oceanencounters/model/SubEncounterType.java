package dev.blonks.osrs.oceanencounters.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class SubEncounterType {
    private final int id;
    private final String name;
}
