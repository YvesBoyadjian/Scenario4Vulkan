package vkbootstrap.example;

import tests.VulkanLibrary;
import vkbootstrap.VkbDevice;
import vkbootstrap.VkbInstance;
import vkbootstrap.VkbSwapchain;

public class Init {
    public /*GLFWwindow*/long window;
    public final VulkanLibrary vk_lib = new VulkanLibrary();
    public VkbInstance instance = new VkbInstance();
    public /*VkSurfaceKHR*/long surface;
    public VkbDevice device = new VkbDevice();
    public VkbSwapchain swapchain = new VkbSwapchain();
    public long allocator;
    public float background_red, background_green, background_blue;

    public VulkanLibrary arrow_operator() {
        return vk_lib;
    }

    public void setBackgroundColor(float x, float y, float z) {
        background_red = x;
        background_green = y;
        background_blue = z;
    }
}
