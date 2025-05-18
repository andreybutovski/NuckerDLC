package im.nucker.ui.display;

import im.nucker.events.EventUpdate;
import im.nucker.utils.client.IMinecraft;

public interface ElementUpdater extends IMinecraft {

    void update(EventUpdate e);
}
