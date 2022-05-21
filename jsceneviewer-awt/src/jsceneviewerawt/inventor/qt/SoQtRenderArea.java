/*
 *
 *  Copyright (C) 2000 Silicon Graphics, Inc.  All Rights Reserved.
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  Further, this software is distributed without any warranty that it is
 *  free of the rightful claim of any third person regarding infringement
 *  or the like.  Any license provided herein, whether implied or
 *  otherwise, applies only to this software file.  Patent licenses, if
 *  any, provided herein do not apply to combinations of this program with
 *  other software, or any other product whatsoever.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  Contact information: Silicon Graphics, Inc., 1600 Amphitheatre Pkwy,
 *  Mountain View, CA  94043, or:
 *
 *  http://www.sgi.com
 *
 *  For further information regarding this notice, see:
 *
 *  http://oss.sgi.com/projects/GenInfo/NoticeExplan/
 *
 */

/*
 * Copyright (C) 1990-93   Silicon Graphics, Inc.
 * Author(s): David Mott, Alain Dumesny
 * Ported to Qt4 by MeVis (http://www.mevis.de), 2006
 */

package jsceneviewerawt.inventor.qt;

import jscenegraph.database.inventor.SbColor;
import jsceneviewerawt.VulkanState;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.awt.GLData;

import com.jogamp.opengl.GL2;

import jscenegraph.database.inventor.elements.SoWindowElement;
import jscenegraph.database.inventor.events.SoEvent;
import jscenegraph.database.inventor.misc.SoState;
import jscenegraph.port.Ctx;
import jscenegraph.port.GLXContext;
import org.lwjgl.vulkan.VKCapabilitiesDevice;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkFenceCreateInfo;
import org.lwjgl.vulkan.awt.VKData;
import vkbootstrap.example.ImageData;
import vkbootstrap.example.Init;
import vkbootstrap.example.RenderData;
import vkbootstrap.example.Renderer;
import vulkanguide.VkInit;
import vulkanguide.VulkanEngine;

import java.awt.*;
import java.awt.event.ComponentEvent;

import static org.lwjgl.util.vma.Vma.vmaDestroyAllocator;
import static org.lwjgl.vulkan.VK10.*;

/**
 * @author Yves Boyadjian
 *
 */
public class SoQtRenderArea extends SoQtGLWidget {

	public enum API {
		OpenGL,
		Vulkan
	}
	
	private final SoQtSceneHandler soQtSceneHandler = new SoQtSceneHandler(this) {
		
	    //! overriden from SoQtSceneHandler
	    protected void setWindowElement(SoState state) {
	        if (state != null) {
	            // Note: Except for the GL render action, this sets highly system dependent 
	            //       context information that should not be used by typical Open Inventor nodes.
	            //       We only provide this so that legacy FME code (AppVis2 and SoRL keep working).
	            // Newer code should make use of SoViewerElement/SoViewerProxy, to also work on remote rendering.
	      //#ifdef WIN32
	              //SoWindowElement.set (state, SoQtRenderArea.this.getGLWidget().winId(), null, null, getGLRenderAction());
	      //#elif defined(__APPLE__) && !defined(APPLE_GLX)
	              //SoWindowElement.set (state, null, null, null, getGLRenderAction());
	      //#else
	              SoWindowElement.set (state, SoQtRenderArea.this.getGLWidget(), (GLXContext)null, null , getGLRenderAction());
	      //#endif
	          }
	    }
	    protected Component getDeviceWidget() { return (Canvas)getGLWidget(); }
	    protected boolean isSceneVisible() {
	    	  return (isVisible() || alwaysEnableRenderSignal);
	    }
	    protected void updateScene() {
	        redrawRequested();
	        // if alwaysEnableRenderSignal is set, we might get a callback though
	        // the widget is not visible, so check this first before rendering
	        if (isVisible()) {
	            updateGL();
	        }
	    }
		@Override
		public
		void actualRedraw() {
			SoQtRenderArea.this.actualRedraw();
		}

	};
	
	public void actualRedraw() {
		soQtSceneHandler.doActualRedraw();
	}
	
    //! this will be set to true when initializeGL is called the first time
	private boolean isGLinitialized;

	    //! if this is true, the scene manager will stay activated, even if the
	    //! widget is not visible (but only if autoRedraw is true).
	private boolean alwaysEnableRenderSignal;

	    //! flag that stores that the rendering is the first one after the viewer became visible.
	private boolean initialRendering;

	    //! flag that indicates that sample buffers are used
	private boolean useSampleBuffers;
	    //! number of sample buffers that should be used (0 = automatic)
	private int  numSampleBuffers;

	//! Used API
	private API api;

	final VulkanEngine engine = new VulkanEngine();

	public SoQtRenderArea(Container parent, int style) {
		this(parent,style,true, API.OpenGL);
	}
	public SoQtRenderArea(Container parent, int style, boolean build, API api) {
		super(parent, style);
		this.api = api;
	    SoQt.init("Dummy");

	    // Inventor specific variables
//	    QGLFormat glf (QGL::Rgba | QGL::DoubleBuffer | QGL::DepthBuffer | QGL::DirectRendering);
		if (api == API.OpenGL) {
			GLData glf = new GLData(/*GLProfile.getDefault()*/);
			glf.depthSize = 24;
			glf.doubleBuffer = true;
			// YB : must be compatible with software rendering Mesa3D for Windows, and Linux on VirtualBox
			glf.majorVersion = 4;//2;
			glf.minorVersion = 0;//1;
			glf.api = GLData.API.GL;
			glf.profile = GLData.Profile.CORE;//COMPATIBILITY; //no profile for 3.1
			glf.debug = true;// false; //true; debug profile not always available in Linux
			// Call setFormat from parent class to avoid early construction of decoration which will fail
			super.setFormat(glf);
		}
		else if (api == API.Vulkan) {
			VKData vkf = new VKData();
			VulkanState vulkanState = new VulkanState(engine);
			vulkanState.init_vulkan_instance();
			setVulkanState(vulkanState);
			vkf.instance = vulkanState.getInstance();
			super.setFormat(vkf);
		}

	    //setBorder (true);

	    autoFocus = true;

	    isGLinitialized = false;
	    alwaysEnableRenderSignal = false;
	    initialRendering = true;
	    useSampleBuffers = false;
	    numSampleBuffers = 0;

	    if (build) {
	        buildWidget(style);
	    }
	}
	
	public void destructor() {
		soQtSceneHandler.destructor();
		super.destructor();
	}

    //! Calling this forces the render area to be redrawn now.
    //! It is not necessary to call this method if auto redraw is enabled
    //! (which is the default).
    public void        render()        { updateGL(); }
    
    protected //! this is emitted when the scene manager requests a redraw of the scene
    void redrawRequested() {
    	//getGLWidget().repaint();//  redraw(); //nothing to do : updateGL does it
    }
	
    protected void visibilityChanged (boolean visible) {
        if (visible) {
            initialRendering = true; 
          }
          if ((visible || alwaysEnableRenderSignal) && soQtSceneHandler.isAutoRedraw()) {
        	  soQtSceneHandler.activate();
          } else {
        	  soQtSceneHandler.deactivate();
          }    	
    }
    

// *************************************************************************

/*!
  Toolkit-native events are attempted converted to Coin-generic events
  in the SoQtRenderArea::processEvent() method.  If this succeeds,
  they are forwarded to this method.

  This is a virtual method, and is overridden in it's subclasses to
  catch events of particular interest to the viewer classes, for
  instance.

  Return \c TRUE iff the event was processed. If not it should be
  passed on further up in the inheritance hierarchy by the caller.
  This last point is extremely important to take note of if you are
  expanding the toolkit with your own viewer class.

  This method is not part of the original SGI InventorXt API. Note
  that you can still override the toolkit-native processEvent() method
  instead of this "generic" method.
*/
    // COIN3D
protected boolean
processSoEvent(final SoEvent event)
{
  //if (this.overlayManager.processEvent(event)) { return true; }
  if (soQtSceneHandler.getSceneManager()/*this.normalManager*/.processEvent(event)) { return true; }
  return false;
}

    

    //! Redefine these to do Inventor-specific things
    protected void processEvent (ComponentEvent anyEvent, EventType type, final boolean[] isAccepted) {
    	soQtSceneHandler.processSceneEvent(anyEvent, type);    	
    }
    public void paintGL(GL2 gl2) {
        // char buffer[200];
        // sprintf (buffer, "SoQtRenderArea::paintGL, depth=%i %i\n", format().depth(), format().depthBufferSize());
    	soQtSceneHandler.paintScene();
        initialRendering = false;    	
    }

	public void paintVK(VulkanState vkState) {

		SbColor backgroundColor = soQtSceneHandler.getSceneManager().getBackgroundColor();
		vkState.getInit().setBackgroundColor(backgroundColor.getX(),backgroundColor.getY(),backgroundColor.getZ());

		Renderer renderer = new Renderer() {
			@Override
			public int render(Init init, RenderData data, ImageData imageData) {

				init.arrow_operator().vkCmdBindPipeline.invoke (imageData.command_buffer, VK_PIPELINE_BIND_POINT_GRAPHICS, data.graphics_pipeline[0]);

				init.arrow_operator().vkCmdDraw.invoke (imageData.command_buffer, 3, 1, 0, 0);
				return 0;
			}
		};

		renderer = new Renderer() {
			@Override
			public int render(Init init, RenderData data, ImageData imageData) {
				VkCommandBuffer cmd = imageData.command_buffer;
				engine.draw_objects(cmd, engine._renderables/*.data()*/, engine._renderables.size());
				return 0;
			}
		};

		renderer = new Renderer() {
			@Override
			public int render(Init init, RenderData data, ImageData imageData) {
				soQtSceneHandler.paintSceneVk(init,data,imageData);
				return 0;
			}
		};

		vkState.draw_VK(renderer);
//		getSceneHandler().getSceneGraph().touch();//render();
		//engine._frameNumber++;
		//soQtSceneHandler.paintScene();
		initialRendering = false;
	}

    public void initializeGL(GL2 gl2) {
        super.initializeGL(gl2);
        Ctx.addContext(1, gl2);
        shareID = 1;
        soQtSceneHandler.initializeScene(/*getShareID()*/shareID);
        isGLinitialized = true;    	
    }
	public void initializeVK(VulkanState vulkanState) {
		super.initializeVK(vulkanState);
		//Ctx.addContext(1, vulkanState);
		shareID = 1;
		soQtSceneHandler.initializeScene(/*getShareID()*/shareID);
		isGLinitialized = true;

		engine._chosenGPU = vulkanState.getInit().device.physical_device.physical_device;
		engine._device = vulkanState.getInit().device.device[0];
		engine._instance = vulkanState.getInstance();
		engine._renderPass[0] = vulkanState.getData().render_pass[0];
		engine._uploadContext._commandPool[0] = vulkanState.getData().command_pool[0];
		engine._graphicsQueue = vulkanState.getData().graphics_queue;
		engine._allocator = vulkanState.getInit().allocator;

		VkFenceCreateInfo uploadFenceCreateInfo = VkInit.fence_create_info();

		engine.VK_CHECK(vkCreateFence(engine._device, uploadFenceCreateInfo, null, engine._uploadContext._uploadFence));
		engine._mainDeletionQueue.push_function(() -> {
			vkDestroyFence(engine._device, engine._uploadContext._uploadFence[0], null);
		});

		vulkanState.getData().upload_fence[0] = engine._uploadContext._uploadFence[0];

		//engine.init_allocator();

		engine.init_depth_image();

		engine.init_descriptors();

		engine.init_pipelines();

		engine.load_images();

		engine.load_meshes();

		engine.init_scene();

		//everything went fine
		engine._isInitialized = true;

		vulkanState.addCleaner(engine::cleanup_light);
	}
    public void resizeGL (GL2 gl2, int width, int height) {
        super.resizeGL (gl2, width, height);
        soQtSceneHandler.resizeScene(width, height, getGlxDevicePixelRatio());

//		engine._windowExtent.width(width);
//		engine._windowExtent.height(height);
    }
 
    //! overriden from SoQtSceneHandler
    protected void setWindowElement(SoState state) {
    	soQtSceneHandler.setWindowElement(state);
    }
    protected Component getDeviceWidget() { return (Canvas)getGLWidget(); }
    protected boolean isSceneVisible() {
    	  return (isVisible() || alwaysEnableRenderSignal);    	
    }
    protected void updateScene() {
        redrawRequested();
        // if alwaysEnableRenderSignal is set, we might get a callback though
        // the widget is not visible, so check this first before rendering
        if (isVisible()) {
            updateGL();
        }    	
    }

    /**
     * Java port
     * @return
     */
    public SoQtSceneHandler getSceneHandler() {
    	return soQtSceneHandler;
    }


	public boolean setAntialiasing (boolean smoothing, int numPasses)
	{
		if (getSceneHandler().setAntialiasing(smoothing, numPasses)) {

			if (format() instanceof GLData) {
				GLData format = (GLData)this.format();
				int bits = smoothing ? 5 : 0;
				if (format.accumGreenSize != bits) {
					format.accumAlphaSize = bits;
					format.accumBlueSize = bits;
					format.accumGreenSize = bits;
					format.accumRedSize = bits;
					setFormat(format);
				}
			}
			return true;
		} else {
		  return false;
		}
	}

	public API getAPI() {
    	return api;
	}
}
