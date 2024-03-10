package vkbootstrap.example;

import java.util.ArrayList;
import java.util.List;

public class Cleaner {

    final List<Runnable> cleaners = new ArrayList<>();

    public void cleanup_VK(Init init, RenderData render_data) {
        init.arrow_operator().vkDeviceWaitIdle.invoke (init.device.device[0]);

        cleaners.forEach((r)->r.run());

        Triangle.cleanup(init,render_data);
    }

    public void addCleaner(Runnable cleaner) {
        cleaners.add(cleaner);
    }
}
