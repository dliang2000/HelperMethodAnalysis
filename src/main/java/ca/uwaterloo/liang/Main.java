package ca.uwaterloo.liang;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
	private static String output_path;
	public static void main(String[] args) throws IOException {
		 PackManager.v().getPack("wjtp").add(
			     new Transform("wjtp.myTransform", HelperMethodTransformer.v()) {
			     });
		 Options.v().set_prepend_classpath(true);                                                                                 
		 Options.v().set_verbose(true);
		 Options.v().set_whole_program(true);
		 List<String> pd = new ArrayList<>();
		 System.out.println("args[0]: " + args[0]);
		 System.out.println("args[1]: " + args[1]);
         pd.add("-main-class"); 
         pd.add(args[0]);
		 pd.add("-process-dir");
		 pd.add(args[1]);
		 pd.add("-process-dir");
		 pd.add(args[2]);
		 Options.v().set_soot_classpath(args[3]);
		 Main.benchmark = args[4];
		 Main.output_path = args[5];
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
	    	Set<SootMethod> setOfTestsCalled = new HashSet<SootMethod>();
	    	Map<SootMethod, List<SootMethod>> helpersListMap = new HashMap<SootMethod, List<SootMethod>>();
	    	Map<SootMethod, Set<SootMethod>> helpersSetMap = new HashMap<SootMethod, Set<SootMethod>>();

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
	    	      if (sm.isAbstract() || sm.isNative() || sm.isConstructor() || sm.isStaticInitializer())
	    	        continue;
	    	      System.out.println("SootMethod " + sm.getSubSignature() + " is visited in SootClass " + appClass.getName());
	    	      Iterator<Edge> it = cg.edgesInto(sm);
	    	      
	    	      while (it.hasNext()) {
	    	    	//System.out.println("Edge Iterator: " + (Edge) it.next());
	    	        Edge e = (Edge) it.next();

	    	        SootMethod srcMethod = e.src();
	    	        System.out.println("SootMethod " + srcMethod.getSubSignature() + " called " + sm.getSubSignature());
	    	        if (isTestCase(srcMethod)) {
	    	          listOfTestsCalled.add(srcMethod);
	    	          setOfTestsCalled.add(srcMethod);
	    	        }
	    	      }
	    	      if (setOfTestsCalled.size() > 1) {
	    	    	  helpersSetMap.put(sm, setOfTestsCalled);
	    	      } else if (listOfTestsCalled.size() > 1) {
	    	    	  helpersListMap.put(sm, listOfTestsCalled);
	    	      }
		    	  listOfTestsCalled = new ArrayList<SootMethod>();
		    	  setOfTestsCalled = new HashSet<SootMethod>();
	    	    }
	    	}
	        try {
	        	BufferedWriter writer = new BufferedWriter(new FileWriter(output_path+"/"+benchmark+".txt"));
	        	StringBuilder sb = new StringBuilder();
	        	sb.append("Number of Helper Methods Called multiple times from different test cases: " + helpersSetMap.size() + "\n");
				for (Entry<SootMethod, Set<SootMethod>> entry: helpersSetMap.entrySet()) {
	        		sb.append("Helper Method Class: " + entry.getKey().getDeclaringClass().getName() + ", Helper Method name: " + entry.getKey().getSubSignature()+"\n");
	        		Set<SootMethod> sootMethodSet = entry.getValue();
	        		for (SootMethod sm: sootMethodSet) {
	        			sb.append("\tTest Case Class: " + sm.getDeclaringClass().getName() + ", Test Case that called the Helper Method: " + sm.getSubSignature() +"\n");
	        		}
	        	}
				
				sb.append("\nNumber of Helper Methods Called multiple times from the same test case: " + helpersListMap.size() + "\n");
				for (Entry<SootMethod, List<SootMethod>> entry: helpersListMap.entrySet()) {
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
		// JUnit 3
		if (sm.getName().startsWith("test") &&
			sm.getParameterCount() == 0 && sm.getReturnType().toString() == "void") {
			System.out.println("Test case found: " + sm.getSubSignature());
			return true;
		}
		// JUnit 4+
		List<soot.tagkit.Tag> smTags = sm.getTags();
		soot.tagkit.VisibilityAnnotationTag tag = (soot.tagkit.VisibilityAnnotationTag) sm.getTag("VisibilityAnnotationTag");
		if (tag != null) {
			for (AnnotationTag annotation : tag.getAnnotations()) {
				if (annotation.getType().equals("Lorg/junit/Test;")) {
				        System.out.println("Test case found: " + sm.getSignature());
					return true;
				}
		        }
		}
		return false;
	}
}

//&& sm.getDeclaringClass().getName().contains("Test")
