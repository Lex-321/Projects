package app;

import javafx.scene.image.Image;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 *  Klasa odpowiedzialna za zapis gry
 */
public class LoadGame {
    private final String[] slotNames = {"Empty Save", "Empty Save", "Empty Save"};
    private final int[] avatarNumber = {0, 0, 0};
    private final String[] avatarPaths = {
            "/catvatars/black_cat.png",
            "/catvatars/grey_cat.png",
            "/catvatars/orange_cat.png",
            "/catvatars/white_cat.png"
    };

    public LoadGame() {
        loadSaveData();
    }
    /**
    *wczytanie danych z pliku
    */
    public void loadSaveData() {
        Properties properties = new Properties();
        try (FileReader reader = new FileReader("saves.properties")) {
            properties.load(reader);
            for (int i = 0; i < slotNames.length; i++) {
                slotNames[i] = properties.getProperty("slot_" + (i + 1) + "_name", "Empty Save");
                avatarNumber[i] = Integer.parseInt(properties.getProperty("slot_" + (i + 1) + "_avatar", "0"));
            }
        } catch (IOException e) {
            System.out.println("File not found");
        }
    }
    /**
    * zapisanie danych do pliku
    */
    public void saveData() {
        Properties properties = new Properties();
        try (FileWriter writer = new FileWriter("saves.properties")) {
            for (int i = 0; i < slotNames.length; i++) {
                properties.setProperty("slot_" + (i + 1) + "_name", slotNames[i]);
                properties.setProperty("slot_" + (i + 1) + "_avatar", String.valueOf(avatarNumber[i]));
            }
            properties.store(writer, "Save Slots Data");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
    * czyszczenie slota
    */
    public void clearSlot(int slot) {
        if (slot >= 1 && slot <= slotNames.length) {
            slotNames[slot - 1] = "Empty Save";
            avatarNumber[slot - 1] = 0;
            saveData();
        }
    }
    /**
     * pobieranie nazwy
     */
    public String getSlotName(int slot) {
        if (slot >= 1 && slot <= slotNames.length) {
            return slotNames[slot - 1];
        }
        return "Invalid Slot";
    }
    /**
    * pobranie indeksu avatara
    */
    public int getSlotAvatarIndex(int slot) {
        if (slot >= 1 && slot <= avatarNumber.length) {
            return avatarNumber[slot - 1];
        }
        return 0;
    }
    /**
    * pobranie obrazu avatara
    */
    public Image getAvatarImage(int avatarIndex) {
        if (avatarIndex >= 0 && avatarIndex < avatarPaths.length) {
            return new Image(getClass().getResourceAsStream(avatarPaths[avatarIndex]));
        }
        return null;
    }

    public void setSlotName(int slot, String name) {
        if (slot >= 1 && slot <= slotNames.length) {
            slotNames[slot - 1] = name;
        }
    }

    public void setSlotAvatarIndex(int slot, int avatarIndex) {
        if (slot >= 1 && slot <= avatarNumber.length) {
            avatarNumber[slot - 1] = avatarIndex;
        }
    }

    public int getCurrentLevel(int slot) {
        Properties properties = new Properties();
        try (FileReader reader = new FileReader("saves.properties")) {
            properties.load(reader);
            // Pobieramy poziom zapisany w pliku dla danego slotu
            return Integer.parseInt(properties.getProperty("last_completed_level_slot_" + slot, "1"));  // Domyślnie 1, jeśli brak danych
        } catch (IOException e) {
            e.printStackTrace();
            return 1;  // Domyślny poziom, jeśli wystąpił błąd
        }
    }
}
