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

    public static void dataUpdated(){
        for (Refreshable l : dataUpdateListeners)
            l.refresh();
    }

    public static void permissionsChanged(Permission p) {
        VistaLogInController.setPermission(p);
    }
}
