/**
 * 
 */
package jscenegraph.database.inventor.nodes;

import jscenegraph.coin3d.inventor.annex.profiler.SoNodeProfiling;
import jscenegraph.database.inventor.actions.SoGLRenderAction;
import jscenegraph.database.inventor.actions.SoVkRenderAction;

/**
 * @author Yves Boyadjian
 *
 */
//*************************************************************************
//Note: just static data here, as there's no Cheshire Cat pattern (ie
//pimpl-ptr) implemented for SoNode. (The class should be as slim as
//possible.)

public class SoGroupP {
	public interface GLRenderFunc {
		  void invoke(SoGroup group, SoNode node, SoGLRenderAction renderAction);
	}
	public interface VkRenderFunc {
		void invoke(SoGroup group, SoNode node, SoVkRenderAction renderAction);
	}
	public static GLRenderFunc glrenderfunc;
	public static void childGLRender(SoGroup thisp, SoNode child, SoGLRenderAction action) {
		  child.GLRender(action);		
	}
	// This function is called for each child to traverse, and
	// action->getCurPath() is already updated at this point.
	public static void childGLRenderProfiler(SoGroup thisp, SoNode child, SoGLRenderAction action) {
		  final SoNodeProfiling profiling = new SoNodeProfiling();
		  profiling.preTraversal(action);
		  child.GLRender(action);
		  profiling.postTraversal(action);		
	}

	public static VkRenderFunc vkrenderfunc;
	public static void childVkRender(SoGroup thisp, SoNode child, SoVkRenderAction action) {
		child.VkRender(action);
	}
	// This function is called for each child to traverse, and
	// action->getCurPath() is already updated at this point.
	public static void childVkRenderProfiler(SoGroup thisp, SoNode child, SoVkRenderAction action) {
		final SoNodeProfiling profiling = new SoNodeProfiling();
		profiling.preTraversal(action);
		child.VkRender(action);
		profiling.postTraversal(action);
	}
}
