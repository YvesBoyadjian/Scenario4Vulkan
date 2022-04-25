package vkbootstrap.example;

import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkQueue;
import vulkanguide.AllocatedImage;

import java.util.ArrayList;
import java.util.List;

public class RenderData {
    public VkQueue graphics_queue;
    public VkQueue present_queue;

    public final List<ImageData> image_datas = new ArrayList<>();

    public final long[] depth_image_view = new long[1];
    public final AllocatedImage depthImage = new AllocatedImage();

    public /*VkRenderPass*/final long[] render_pass = new long[1];
    public /*VkPipelinLayout*/final long[] pipeline_layout = new long[1];
    public /*VkPipeline*/final long[] graphics_pipeline = new long[1];

    public /*VkCommandPool*/final long[] command_pool = new long[1];

    public final List</*VkSemaphore*/long[]> available_semaphores = new ArrayList<>();
    public final List</*VkSemaphore*/long[]> finished_semaphore = new ArrayList<>();
    public final List</*VkFence*/long[]> in_flight_fences = new ArrayList<>();
    public long current_frame = 0;
}
