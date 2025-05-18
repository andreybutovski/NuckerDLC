package im.nucker.ui.display;

import im.nucker.events.EventDisplay;
import im.nucker.events.EventUpdate;
import im.nucker.utils.client.IMinecraft;

public interface ElementRenderer extends IMinecraft {
    void update(EventUpdate e);

    void render(EventDisplay eventDisplay);
}
