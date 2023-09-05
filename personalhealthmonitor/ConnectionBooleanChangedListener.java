package personalhealthmonitor;

import java.util.ArrayList;
import java.util.List;

public interface ConnectionBooleanChangedListener {
    void OnMyBooleanChanged();
}

class Connect {
    protected static boolean myBoolean = false;
    protected static List<ConnectionBooleanChangedListener> listeners = new ArrayList<ConnectionBooleanChangedListener>();

    public static boolean getMyBoolean() { return myBoolean; }

    public static void setMyBoolean(boolean value) {
        myBoolean = value;

        for (ConnectionBooleanChangedListener l : listeners) {
            l.OnMyBooleanChanged();
        }
    }

    public static void addMyBooleanListener(ConnectionBooleanChangedListener l) {
        listeners.add(l);
    }
}