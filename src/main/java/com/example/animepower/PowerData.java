package com.example.animepower;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Stores one power assignment per player UUID (name is cached alongside the
 * UUID purely so /power list can print readable names without needing the
 * player to be online). Data is saved to the overworld's level data folder
 * and therefore survives server restarts.
 */
public class PowerData extends SavedData {

    private static final String DATA_NAME = "animepower_data";

    public static final class Entry {
        public final String name;
        public final String power;

        public Entry(String name, String power) {
            this.name = name;
            this.power = power;
        }
    }

    private final Map<UUID, Entry> assignments = new LinkedHashMap<>();

    public static PowerData get(MinecraftServer server) {
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        DimensionDataStorage storage = overworld.getDataStorage();
        SavedData.Factory<PowerData> factory = new SavedData.Factory<>(PowerData::new, PowerData::load);
        return storage.computeIfAbsent(factory, DATA_NAME);
    }

    public static PowerData load(CompoundTag tag) {
        PowerData data = new PowerData();
        ListTag list = tag.getList("Assignments", 10); // 10 = CompoundTag id
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            UUID uuid = entry.getUUID("UUID");
            String name = entry.getString("Name");
            String power = entry.getString("Power");
            data.assignments.put(uuid, new Entry(name, power));
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (Map.Entry<UUID, Entry> e : assignments.entrySet()) {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("UUID", e.getKey());
            entry.putString("Name", e.getValue().name);
            entry.putString("Power", e.getValue().power);
            list.add(entry);
        }
        tag.put("Assignments", list);
        return tag;
    }

    public void setPower(UUID uuid, String name, String power) {
        assignments.put(uuid, new Entry(name, power));
        setDirty();
    }

    public void clearPower(UUID uuid) {
        if (assignments.remove(uuid) != null) {
            setDirty();
        }
    }

    /** Returns the power name, or null if the player has none assigned. */
    public String getPower(UUID uuid) {
        Entry e = assignments.get(uuid);
        return e == null ? null : e.power;
    }

    public Map<UUID, Entry> getAll() {
        return assignments;
    }
}
