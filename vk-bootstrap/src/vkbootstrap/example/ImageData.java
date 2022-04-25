package vkbootstrap.example;

import org.lwjgl.vulkan.VkCommandBuffer;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.vulkan.VK10.vkFreeCommandBuffers;

public class ImageData {
    public long swapchain_image;
    public long swapchain_image_view;
    public long framebuffer;
    public VkCommandBuffer command_buffer;
    public final long[] image_in_flight = new long[1];

    public ImageData(long swapchain_image) {
        this.swapchain_image = swapchain_image;
    }

    public void setSwapchainImageView(long swapchain_image_view) {
        this.swapchain_image_view = swapchain_image_view;
    }

    public void setFrameBuffer(Init init,long framebuffer) {
        if(this.framebuffer != 0) {
            destroyFrameBuffer(init);
        }
        this.framebuffer = framebuffer;
    }

    public void setCommandbuffer(Init init, final RenderData data, VkCommandBuffer buffer) {
        if(command_buffer != null)
            freeCommandBuffer(init,data);
        command_buffer = buffer;
    }

    public void freeCommandBuffer(Init init, final RenderData data) {
        vkFreeCommandBuffers(init.device.device[0],data.command_pool[0],command_buffer);
    }

    public void destroyFrameBuffer(Init init) {
        init.arrow_operator().vkDestroyFramebuffer.invoke (init.device.device[0], framebuffer, null);
    }
}
