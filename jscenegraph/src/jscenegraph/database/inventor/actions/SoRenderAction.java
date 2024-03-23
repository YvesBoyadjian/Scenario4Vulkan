package jscenegraph.database.inventor.actions;

public abstract class SoRenderAction extends SoAction {

    //! Keep track of which planes we need to view-volume cull test
    //! against:
    private int                 cullBits;

    public int                 getCullTestResults() { return cullBits; }
    public void                setCullTestResults(int b) { cullBits = b; }

    public SoRenderAction() {
        super();

        // These three bits keep track of which view-volume planes we need
        // to test against; by default, all bits are 1.
        cullBits            = 7;
    }
}
