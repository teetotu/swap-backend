package ru.swap.model;

import java.util.HashMap;
import java.util.Map;

public enum Section {
    PERSONAL_ITEMS("Личные вещи"),
    TRANSPORT("Транспорт"),
    CAR_PARTS("Автозапчасти"),
    HOUSEHOLD_PRODUCTS("Товары для дома"),
    ELECTRONICS("Электроника"),
    MUSICAL_INSTRUMENTS("Музыкальные инструменты"),
    TOYS("Игрушки"),
    NOT_FOUND("Not found");

    private final String sectionDescription;

    Section(String value) {
        sectionDescription = value;
    }

    public String getSectionDescription() {
        return sectionDescription;
    }

    private static final Map<String, Section> map;
    static {
        map = new HashMap<String, Section>();
        for (Section v : Section.values()) {
            map.put(v.getSectionDescription(), v);
        }
    }
    public static Section findByKey(String sectionDescription) {
        return map.get(sectionDescription);
    }
}
