package application;

import java.util.ArrayList;
import java.util.List;

interface Refreshable {
    void refresh();
}

public class ServertriggeredEvents {
    private static List<Refreshable> dataUpdateListeners = new ArrayList<Refreshable>();

    public static void addDataUpdateListener(Refreshable toAdd) {
        dataUpdateListeners.add(toAdd);
    }
    public static void removeDataUpdateListener(Refreshable toRemove) {
        dataUpdateListeners.remove(toRemove);
    }
    public static void clearDataUpdateListeners() {
        dataUpdateListeners.clear();
    }

    public static void dataUpdated(){
        ServerData.downloadEverything(); 

        for (Refreshable l : dataUpdateListeners)
            l.refresh();
    }

    public static void permissionsChanged(Permission p) {
        VistaLogInController.setPermission(p);
    }

    public static void error(String msg){
        Dialogs.error(msg, "Błąd po stronie serwera!");
    }
}
