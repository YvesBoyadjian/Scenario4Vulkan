package jsceneviewerawt;

import org.lwjgl.PointerBuffer;
import org.lwjgl.util.vma.VmaAllocatorCreateInfo;
import org.lwjgl.util.vma.VmaVulkanFunctions;
import org.lwjgl.vulkan.VkInstance;
import vkbootstrap.example.Init;
import vkbootstrap.example.RenderData;
import vkbootstrap.example.Renderer;
import vkbootstrap.example.Triangle;
import vulkanguide.VulkanEngine;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.util.vma.Vma.vmaCreateAllocator;
import static org.lwjgl.util.vma.Vma.vmaDestroyAllocator;

public class VulkanState {
    final VulkanEngine engine;
    final Init init = new Init();
    final RenderData render_data = new RenderData();

    final List<Runnable> cleaners = new ArrayList<>();

    public VulkanState(VulkanEngine engine) {
        this.engine = engine;
    }

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
        Triangle.init_allocator(init);
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

    public void draw_VK(Renderer renderer) {
        Triangle.draw_frame (init, render_data,renderer);
    }

    public void cleanup_VK() {
        init.arrow_operator().vkDeviceWaitIdle.invoke (init.device.device[0]);

        cleaners.forEach((r)->r.run());

        Triangle.cleanup(init,render_data);
    }

    public Init getInit() {
        return init;
    }

    public RenderData getData() {
        return render_data;
    }

    public void addCleaner(Runnable cleaner) {
        cleaners.add(cleaner);
    }
}
