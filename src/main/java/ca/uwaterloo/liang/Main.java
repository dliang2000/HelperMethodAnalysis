package ca.uwaterloo.liang;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import soot.Kind;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.CallGraphBuilder;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;
import soot.tagkit.AnnotationTag;

public class Main {
	private static String benchmark;
	public static void main(String[] args) throws IOException {
		 PackManager.v().getPack("wjtp").add(
			     new Transform("wjtp.myTransform", HelperMethodTransformer.v()) {
			     });
		 Options.v().set_prepend_classpath(true);                                                                                 
		 Options.v().set_verbose(true);
		 Options.v().set_whole_program(true);
		 List<String> pd = new ArrayList<>();
		 //System.out.println("args[0]: " + args[0]);
		 //System.out.println("args[1]: " + args[1]);
         pd.add("-main-class"); 
         pd.add(args[0]);
		 pd.add("-process-dir");
		 pd.add(args[1]);
		 pd.add("-process-dir");
		 pd.add(args[2]);
		 Options.v().set_soot_classpath(args[3]);
		 Main.benchmark = args[4];
		 soot.Main.main(pd.toArray(new String[0]));
	}
	
	static class HelperMethodTransformer extends SceneTransformer {
		private final static HelperMethodTransformer instance = new HelperMethodTransformer();
	    private HelperMethodTransformer() {}
		public static HelperMethodTransformer v() { return instance; }
	    @Override
		protected void internalTransform(String phaseName, Map options) {
	    	final String output_path = "/home/daveroar/Graduation_Studies/ThesisWork/HelperMethodAnalysis/analysis_output";
	    	CHATransformer.v().transform();
	    	CallGraph cg;
	    	int count = 0;
	    	List<SootMethod> listOfTestsCalled = new ArrayList<SootMethod>();
	    	Map<SootMethod, List<SootMethod>> helpersMap = new HashMap<SootMethod, List<SootMethod>>();

	    	Iterator<SootClass> classIt = Scene.v().getApplicationClasses().iterator();
	    	
	    	if (Scene.v().hasCallGraph()) {
	    		//System.out.println("Scene's call graph: " + Scene.v().getCallGraph().toString());
	    	    cg = Scene.v().getCallGraph();
	    	} else {
	    	    new CallGraphBuilder().build();
	    	    cg = Scene.v().getCallGraph();
	    	    Scene.v().releaseCallGraph();
	    	}
	    	
	    	while (classIt.hasNext()) {
	    	    SootClass appClass = (SootClass) classIt.next();
	    	    System.out.println("SootClass Visited: " + appClass.toString());
	    	    if (!appClass.isConcrete())
	    	    	continue;
	    	    System.out.println("Concrete SootClass Package: " + appClass.getPackageName() + ", SootClass Name: " + appClass.getName());
	    	    Iterator<SootMethod> mIt = appClass.getMethods().iterator();
	    	    while (mIt.hasNext()) {
	    	      SootMethod sm = (SootMethod) mIt.next();
	    	      if (sm.isAbstract() && sm.isNative())
	    	        continue;
	    	      System.out.println("SootMethod " + sm.getSubSignature() + " is visited in SootClass " + appClass.getName());
	    	      Iterator<Edge> it = cg.edgesInto(sm);
	    	      
	    	      while (it.hasNext()) {
	    	    	//System.out.println("Edge Iterator: " + (Edge) it.next());
	    	        Edge e = (Edge) it.next();

	    	        SootMethod srcMethod = e.src();
	    	        System.out.println("SootMethod " + srcMethod.getSubSignature() + " called " + sm.getSubSignature());
	    	        if (isTestCase(srcMethod)) {
	    	          count++;
	    	          listOfTestsCalled.add(srcMethod);
	    	        }
	    	      }
	    	      if (count > 1) {
	    	        helpersMap.put(sm, listOfTestsCalled);
	    	      }
	    	      count = 0;
		    	  listOfTestsCalled = new ArrayList<SootMethod>();
	    	    }
	    	}
	        try {
	        	BufferedWriter writer = new BufferedWriter(new FileWriter(output_path+"/"+benchmark+".txt"));
	        	StringBuilder sb = new StringBuilder();
	        	sb.append("Helper Method size: " + helpersMap.size() + "\n");
	        	for (Entry<SootMethod, List<SootMethod>> entry: helpersMap.entrySet()) {
	        		sb.append("Helper Method Class: " + entry.getKey().getDeclaringClass().getName() + ", Helper Method name: " + entry.getKey().getSubSignature()+"\n");
	        		List<SootMethod> sootMethodList = entry.getValue();
	        		for (SootMethod sm: sootMethodList) {
	        			sb.append("\tTest Case Class: " + sm.getDeclaringClass().getName() + ", Test Case that called the Helper Method: " + sm.getSubSignature() +"\n");
	        		}
	        	}
	        	writer.write(sb.toString());
	        	writer.close();
	        } catch (IOException e) {
	        	// TODO Auto-generated catch block
	        	e.printStackTrace();
	        }
	    }
	}
	
	private static boolean isTestCase(SootMethod sm) {		
		boolean isTest = false;
		// System
		if (sm.getName().startsWith("test") && sm.getDeclaringClass().getName().contains("Test") 
				&& sm.getParameterCount() == 0 && sm.getReturnType().toString() == "void") {
			System.out.println("Test case found: " + sm.getSubSignature());
			return true;
		}
		AnnotationTag tag = (AnnotationTag) sm.getTag("AnnotationTag");
		if (tag != null) {
			System.out.println("VisibilityAnnotationTag: " + tag.toString());
//			if (tag.getType().equals(""))
//		    for (AnnotationTag annotation : tag.getAnnotations()) {
//		    	List<AnnotationElem> elemsList = new ArrayList<AnnotationElem>(annotation.getElems());
//		        for (AnnotationElem elem: elemsList) {
//		        	System.out.println(elem.toString());
//		        	if (elem.getName().contains("Test")) {
//		        		isTest = true;
//		        		System.out.println("Test case found: " + sm.getSignature());
//		        	}
//		        	break;
//		        }
//		    }
		}
		return isTest;
	}
}

//&& sm.getDeclaringClass().getName().contains("Test")
