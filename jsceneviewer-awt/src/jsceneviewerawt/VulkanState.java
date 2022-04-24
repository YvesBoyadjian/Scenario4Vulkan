package jsceneviewerawt;

import org.lwjgl.vulkan.VkInstance;
import vkbootstrap.example.Init;
import vkbootstrap.example.RenderData;
import vkbootstrap.example.Triangle;

public class VulkanState {
    final Init init = new Init();
    final RenderData render_data = new RenderData();

    public void init_vulkan_instance() {
        Triangle.instance_initialization(init);
    }

    public VkInstance getInstance() {
        return init.instance.instance[0];
    }

    public void setSurface(long surface) {
        init.surface = surface;
    }

    public void init_VK() {
        if (0 != Triangle.device_initialization (init)) return;
        if (0 != Triangle.create_swapchain (init)) return;
        if (0 != Triangle.get_queues (init, render_data)) return;
        if (0 != Triangle.create_render_pass (init, render_data)) return;
        if (0 != Triangle.create_graphics_pipeline (init, render_data)) return;
//        if (0 != create_framebuffers (init, render_data)) return;
//        if (0 != create_command_pool (init, render_data)) return;
//        if (0 != create_command_buffers (init, render_data)) return;
        if(0 != Triangle.recreate_swapchain(init,render_data)) return;
        if (0 != Triangle.create_sync_objects (init, render_data)) return;
    }

    public void draw() {
        Triangle.draw_frame (init, render_data);
    }
}
