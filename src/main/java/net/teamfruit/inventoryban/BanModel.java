package net.teamfruit.inventoryban;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BanModel {
    public Map<String, BanProfile> banned = new HashMap<>();

    public static class BanProfile {
        public UUID id;
        public String name;

        public BanProfile(UUID id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
