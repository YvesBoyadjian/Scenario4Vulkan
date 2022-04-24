package vkbootstrap.example;

import org.lwjgl.vulkan.VkCommandBuffer;

import java.util.ArrayList;
import java.util.List;

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

    public void setFrameBuffer(long framebuffer) {
        this.framebuffer = framebuffer;
    }

    public void setCommandbuffer(VkCommandBuffer buffer) {
        command_buffer = buffer;
    }
}
