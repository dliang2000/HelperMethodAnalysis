package ca.uwaterloo.liang;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.CallGraphBuilder;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationTag;
import soot.tagkit.VisibilityAnnotationTag;

public class Main {
	public static void main(String[] args) throws IOException {
		 PackManager.v().getPack("wjtp").add(
			     new Transform("wjtp.myTransform", HelperMethodTransformer.v()) {
			     });
		 Options.v().set_prepend_classpath(true);                                                                                 
		 Options.v().set_verbose(true);                                                                                            
		 List<String> pd = new ArrayList<>();                                                                                   
		 pd.add("-process-dir");                                                                                                  
		 pd.add(args[0]);                                                                                                       
		 Options.v().set_soot_classpath(args[1]);                                                                                  
		 Options.v().set_whole_program(true);                                                                                      
		 soot.Main.main(pd.toArray(new String[0]));
	}
	
	static class HelperMethodTransformer extends SceneTransformer {
		private final static HelperMethodTransformer instance = new HelperMethodTransformer();
	    private HelperMethodTransformer() {}
		public static HelperMethodTransformer v() { return instance; }
	    @Override
		protected void internalTransform(String phaseName, Map options) {
	    	CHATransformer.v().transform();
	    	CallGraph cg;
	    	int count = 0;
	    	List<SootMethod> listOfTestsCalled = new ArrayList<SootMethod>();
	    	Map<SootMethod, List<SootMethod>> helpersMap = new HashMap<SootMethod, List<SootMethod>>();

	    	Iterator<SootClass> classIt = Scene.v().getApplicationClasses().iterator();
	    	if (Scene.v().hasCallGraph()) {
	    	    cg = Scene.v().getCallGraph();
	    	} else {
	    	    new CallGraphBuilder().build();
	    	    cg = Scene.v().getCallGraph();
	    	    Scene.v().releaseCallGraph();
	    	}
	    	
	    	while (classIt.hasNext()) {
	    	    SootClass appClass = (SootClass) classIt.next();
	    	    Iterator<SootMethod> mIt = appClass.getMethods().iterator();
	    	    while (mIt.hasNext()) {
	    	      SootMethod sm = (SootMethod) mIt.next();
	    	      if (sm.isAbstract() && sm.isNative())
	    	        continue;
	    	      Iterator<Edge> it = cg.edgesInto(sm);
	    	      while (it.hasNext()) {
	    	        Edge e = (Edge) it.next();
	    	        Stmt callSite = e.srcStmt();
	    	        if (callSite == null) {
	    	          continue;
	    	        }
	    	        SootMethod target = e.tgt();
	    	        if (isTestCase(target)) {
	    	          count++;
	    	          listOfTestsCalled.add(target);
	    	        }
	    	      }
	    	      if (count > 1) {
	    	        helpersMap.put(sm, listOfTestsCalled);
	    	      }
	    	      count = 0;
	    	      listOfTestsCalled = new ArrayList<SootMethod>();
	    	    }
	    	}
	    }
	}
	
	private static boolean isTestCase(SootMethod sm) {		
		boolean isTest = false;		
		if (sm.getName().startsWith("test"))
			return true;
		VisibilityAnnotationTag tag = (VisibilityAnnotationTag) sm.getTag("VisibilityAnnotationTag");
		if (tag != null) {
		    for (AnnotationTag annotation : tag.getAnnotations()) {
		    	List<AnnotationElem> elemsList = new ArrayList<AnnotationElem>(annotation.getElems());
		        for (AnnotationElem elem: elemsList) {
		        	System.out.println(elem.toString());
		        	if (elem.getName().contains("Test"))
		        		isTest = true;
		        	break;
		        }
		    }
		}
		return isTest;
	}
}
