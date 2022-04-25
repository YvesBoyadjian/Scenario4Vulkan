package jsceneviewerawt;

import jscenegraph.database.inventor.SbVec2s;
import jsceneviewerawt.inventor.qt.SoQtGLWidget;
import org.lwjgl.opengl.awt.GLData;
import org.lwjgl.vulkan.awt.AWTVKCanvas;
import org.lwjgl.vulkan.awt.VKData;
import vulkanguide.VulkanEngine;

import javax.swing.*;
import java.awt.*;

public class QVKWidget extends AWTVKCanvas implements QWidget {

    private SoQtGLWidget _w;

    private VKData data;

    private VulkanState vkState;

    public QVKWidget(SoQtGLWidget parent, VKData data, VulkanState vkState) {
        super(data);
        this.data = data;
        _w = parent;
        this.vkState = vkState;
    }

    @Override
    public void initVK() {
        vkState.setSurface(surface);
//        SbVec2s glxSize = getGlxSize();
//        engine._windowExtent.width(glxSize.getX());
//        engine._windowExtent.height(glxSize.getY());
        _w.initializeVK(vkState);
    }

    @Override
    public void paintVK() {
        SbVec2s glxSize = getGlxSize();
//        engine._windowExtent.width(glxSize.getX());
//        engine._windowExtent.height(glxSize.getY());
        _w.paintVK(vkState);
    }
/*
    @Override
    public void repaint() {
        if (SwingUtilities.isEventDispatchThread()) {
            super.repaint();
        } else {
            SwingUtilities.invokeLater(() -> super.repaint());
        }
    }
*/

    /* override and return false on components that DO NOT require
       a clearRect() before painting (i.e. native components) */
    public boolean shouldClearRectBeforePaint() {
        return false;
    }

    public VKData format() {
        return data;
    }

    @Override
    public void swapBuffers() {

    }

    @Override
    public void render() {
        repaint();
    }

    public void dispose() {
        vkState.cleanup_VK();
    }
}
