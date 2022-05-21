package jscenegraph.database.inventor.actions;

import jscenegraph.database.inventor.*;
import jscenegraph.database.inventor.elements.*;
import jscenegraph.database.inventor.misc.SoState;
import jscenegraph.port.Destroyable;

public class SoVkRenderAction extends SoAction implements Destroyable {

    public int getNumPasses() {
        return 1;
    }

    public boolean handleTransparency(boolean transparent) {
        return false; // TODO VK
    }

    public int getCacheContext() {
        return 0;
    }

    //! Possible return codes from a render abort callback
    public enum AbortCode {
        CONTINUE,               //!< Continue as usual
        ABORT,                  //!< Stop traversing the rest of the graph
        PRUNE,                  //!< Do not traverse this node or its children
        DELAY                   //!< Delay rendering of this node
    };

    //! Callback functions for render abort should be of this type.
    //! This typedef is defined within the class, since it needs to
    //! refer to the AbortCode enumerated type.
    public interface SoVkRenderAbortCB {

        SoVkRenderAction.AbortCode abort(Object userData);

    }

    @Override
    public SoType getTypeId() {
        return classTypeId;
    }
    public static SoType getClassTypeId()
    { return classTypeId; }
    public static void addMethod(SoType t, SoActionMethodList.SoActionMethod method)
    { methods.addMethod(t, method); }
    // java port
    public  static void                 enableElement(Class<?> klass)
    { enabledElements.enable(SoElement.getClassTypeId(klass), SoElement.getClassStackIndex(klass));}

    public static void enableElement(SoType t, int stkIndex)
    { enabledElements.enable(t, stkIndex);}
    @Override
    protected SoEnabledElementsList getEnabledElements() {
        return enabledElements;
    }
    protected  static SoEnabledElementsList enabledElements;
    protected  static SoActionMethodList   methods;
    private static SoType               classTypeId	;

    private final SbViewportRegion viewport = new SbViewportRegion();

    SoVkRenderAbortCB abortcallback;
    Object abortcallbackdata;

    final SoPathList delayedpaths = new SoPathList();
    boolean delayedpathrender;

    ////////////////////////////////////////////////////////////////////////
    //
    // Description:
    //    Constructor. The first parameter defines the viewport region
    //    into which rendering will take place.
    //
    // Use: public

    public SoVkRenderAction(final SbViewportRegion viewportRegion)

    //
    ////////////////////////////////////////////////////////////////////////
    {
        //SO_ACTION_CONSTRUCTOR(SoVkRenderAction);
        traversalMethods = methods;

        // Can't just push this on the SoViewportRegionElement stack, as the
        // state hasn't been made yet.
        viewport.copyFrom(viewportRegion);
    }


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Adds to the list of paths to render after all other stuff
//    (including delayed/sorted transparent objects) have been
//    rendered. (Used for annotation nodes.)
//
// Use: internal

    public void
    addDelayedPath(SoPath path)
//
////////////////////////////////////////////////////////////////////////
    {
        SoState thestate = this.getState();
        SoCacheElement.invalidate(thestate);
        assert(!delayedpathrender);
        delayedpaths.append(path);
    }

    //! Returns true if render action should abort - checks user callback
    public     boolean              abortNow()
    {
        if (this.hasTerminated()) return true;

//    	  #if COIN_DEBUG && 0 // for dumping the scene graph during GLRender traversals
//    	    static int debug = -1;
//    	    if (debug == -1) {
//    	      const char * env = coin_getenv("COIN_DEBUG_GLRENDER_TRAVERSAL");
//    	      debug = env && (atoi(env) > 0);
//    	    }
//    	    if (debug) {
//    	      const SoFullPath * p = (const SoFullPath *)this->getCurPath();
//    	      assert(p);
//    	      const int len = p->getLength();
//    	      for (int i=1; i < len; i++) { printf("  "); }
//    	      const SoNode * n = p->getTail();
//    	      assert(n);
//    	      printf("%p %s (\"%s\")\n",
//    	             n, n->getTypeId().getName().getString(),
//    	             n->getName().getString());
//    	    }
//    	  #endif // debug

        boolean abort = false;
        if (abortcallback != null) {
            switch (abortcallback.abort(abortcallbackdata)) {
                case CONTINUE:
                    break;
                case ABORT:
                    this.setTerminated(true);
                    abort = true;
                    break;
                case PRUNE:
                    // abort this node, but do not abort rendering
                    abort = true;
                    break;
                case DELAY:
                    this.addDelayedPath(this.getCurPath().copy());
                    // prune this node
                    abort = true;
                    break;
            }
        }
        return abort;
    }

    ////////////////////////////////////////////////////////////////////////
    //
    // Description:
    //    Initializes the SoGLRenderAction class.
    //
    // Use: internal

    public static void
    initClass()
    //
    ////////////////////////////////////////////////////////////////////////
    {
        //SO_ACTION_INIT_CLASS(SoGLRenderAction, SoAction);
        enabledElements = new SoEnabledElementsList(SoAction.enabledElements);
        methods = new SoActionMethodList(SoAction.methods);
        classTypeId    = SoType.createType(SoAction.getClassTypeId(),
                new SbName("SoVkRenderAction"), null);

        classTypeId.markImmutable();

        //SO_ENABLE(SoGLRenderAction, SoGLLazyElement);
        SoVkRenderAction.enableElement(SoVkLazyElement.class);

        //SO_ENABLE(SoGLRenderAction, SoGLRenderPassElement);
        //SoVkRenderAction.enableElement(SoGLRenderPassElement.class);

        //SO_ENABLE(SoGLRenderAction, SoViewportRegionElement);
        SoVkRenderAction.enableElement(SoViewportRegionElement.class);

        //SO_ENABLE(SoGLRenderAction, SoWindowElement);
        SoVkRenderAction.enableElement(SoWindowElement.class);

        // COIN 3D
        //SO_ENABLE(SoGLRenderAction.class, SoDecimationPercentageElement.class);
        //SO_ENABLE(SoGLRenderAction.class, SoDecimationTypeElement.class);
//        SO_ENABLE(SoVkRenderAction.class, SoVkLightIdElement.class);
//        SO_ENABLE(SoVkRenderAction.class, SoVkRenderPassElement.class);
//        SO_ENABLE(SoVkRenderAction.class, SoVkUpdateAreaElement.class);
//        SO_ENABLE(SoVkRenderAction.class, SoLazyElement.class);
//        SO_ENABLE(SoVkRenderAction.class, SoOverrideElement.class);
//        SO_ENABLE(SoVkRenderAction.class, SoTextureOverrideElement.class);
//        SO_ENABLE(SoVkRenderAction.class, SoWindowElement.class);
//        SO_ENABLE(SoVkRenderAction.class, SoVkViewportRegionElement.class);
//        SO_ENABLE(SoVkRenderAction.class, SoVkCacheContextElement.class);

        SO_ENABLE(SoVkRenderAction.class, SoVkRenderVarsElement.class);
    }

}
