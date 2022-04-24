package jsceneviewerawt;

import jscenegraph.database.inventor.SbVec2s;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

public interface QWidget {
    boolean isVisible();

    Object format();

    void setCursor(Cursor cursor);

    void swapBuffers();

    void render();

    Dimension getSize();

    GraphicsConfiguration getGraphicsConfiguration();

    Rectangle getBounds();

    void repaint();

    void addComponentListener(ComponentListener componentListener);

    void requestFocus();

    void addMouseListener(MouseListener mouseListener);

    void addMouseMotionListener(MouseMotionListener mouseMotionListener);

    void addMouseWheelListener(MouseWheelListener mouseWheelListener);

    void addKeyListener(KeyListener keyListener);

    default SbVec2s getGlxSize() {
        // QGLWidget::size calls QWidget::size which returns the size in window coordinates
        // we need to scale this size by the device pixel ratio.
        Dimension size = getSize()/*size() * mainWidget.devicePixelRatio()*/;

        AffineTransform at = getGraphicsConfiguration().getDefaultTransform();

        return new SbVec2s ((short)(size.width*at.getScaleX()), (short)(size.height*at.getScaleY()));
    }
}
