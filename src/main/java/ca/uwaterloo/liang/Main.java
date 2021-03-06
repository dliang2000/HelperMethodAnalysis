package ca.uwaterloo.liang;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTransform", HelperMethodTransformer.v()) {
        });
        Options.v().set_prepend_classpath(true);
        Options.v().set_verbose(true);
        Options.v().set_whole_program(true);
        List<String> pd = new ArrayList<>();
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

        private HelperMethodTransformer() {
        }

        public static HelperMethodTransformer v() {
            return instance;
        }

        private boolean isDefinedOutsideTestClass(SootMethod m) {
            String ss = m.getSubSignature();
            SootClass sc = m.getDeclaringClass();
            // unfortunately, test classes are not annotated
            // we approximate by filtering out anything defined in java.*
            do {
                if (!sc.declaresMethod(ss))
                    break;
                if (sc.getJavaPackageName().startsWith("java")) {
                    return true;
                }
                sc = sc.getSuperclass();
            } while (sc != null);
            return false;
        }

        @Override
        protected void internalTransform(String phaseName, Map options) {
            CHATransformer.v().transform();
            CallGraph cg;
            Map<SootMethod, Integer> multiCalledHelpers = new HashMap<>();
            Map<SootMethod, Integer> multiCalledHelpersFromSameTest = new HashMap<>();

            Iterator<SootClass> classIt = Scene.v().getApplicationClasses().iterator();

            if (Scene.v().hasCallGraph()) {
                cg = Scene.v().getCallGraph();
            } else {
                new CallGraphBuilder().build();
                cg = Scene.v().getCallGraph();
                Scene.v().releaseCallGraph();
            }

            List<String[]> linesToAdd = new ArrayList<>();
            linesToAdd.add(new String[] { "Class", "Helper Method Name", "Total Number of Calls",
                    "Total Number of Distinct Test-Case Callers", "Total Number of Multi-called Test Cases" });

            while (classIt.hasNext()) {
                SootClass appClass = (SootClass) classIt.next();
                System.out.println("SootClass Visited: " + appClass.toString());
                if (!appClass.isConcrete())
                    continue;
                System.out.println("Concrete SootClass Package: " + appClass.getPackageName() + ", SootClass Name: "
                        + appClass.getName());
                Iterator<SootMethod> mIt = appClass.getMethods().iterator();
                while (mIt.hasNext()) {
                    SootMethod helper = (SootMethod) mIt.next();
                    if (helper.isAbstract() || helper.isNative() || helper.isConstructor()
                            || helper.isStaticInitializer())
                        continue;
                    if (isDefinedOutsideTestClass(helper))
                        continue;
                    System.out.println("visiting potential helper SootMethod " + helper.getSubSignature()
                            + " in SootClass " + appClass.getName());

                    Map<SootMethod, Integer> helperCallersCount = new HashMap<>();

                    Iterator<Edge> it = cg.edgesInto(helper);
                    int callsToHelper = 0;
                    while (it.hasNext()) {
                        Edge e = (Edge) it.next();
                        SootMethod srcMethod = e.src();
                        System.out
                                .println("SootMethod " + srcMethod.getSubSignature() + " called this potential helper");
                        if (isTestCase(srcMethod)) {
                            callsToHelper++;
                            int c = helperCallersCount.getOrDefault(srcMethod, 0);
                            helperCallersCount.put(srcMethod, c + 1);
                        }
                    }
                    // identify helper methods that are called more than once from anywhere and
                    // record how many times called
                    if (callsToHelper > 1) {
                        multiCalledHelpers.put(helper, callsToHelper);
                    }

                    // identify helper methods that are called more than once from same method and
                    // record many distinct methods they are multi-called from
                    for (Entry<SootMethod, Integer> entry : helperCallersCount.entrySet()) {
                        if (entry.getValue() > 1) {
                            int c = multiCalledHelpersFromSameTest.getOrDefault(helper, 0);
                            multiCalledHelpersFromSameTest.put(helper, c + 1);
                        }
                    }
                    // Add the helper method data to the csv file if it is being called more than
                    // once
                    if (callsToHelper > 1) {
                        linesToAdd.add(new String[] { appClass.getName(), helper.getSubSignature(),
                                String.valueOf(callsToHelper), String.valueOf(helperCallersCount.size()),
                                String.valueOf(multiCalledHelpersFromSameTest.get(helper)) });
                    }
                }
            }

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(output_path + "/" + benchmark + ".txt"));
                StringBuilder sb = new StringBuilder();
                sb.append("# Helper Methods called multiple times: " + multiCalledHelpers.size() + "\n");
                for (Entry<SootMethod, Integer> entry : multiCalledHelpers.entrySet()) {
                    SootMethod helper = entry.getKey();
                    sb.append("Helper Method Class: " + helper.getDeclaringClass().getName() + ", Helper Method name: "
                            + helper.getSubSignature() + "\n");
                    Iterator<Edge> it = cg.edgesInto(helper);
                    while (it.hasNext()) {
                        SootMethod caller = it.next().src();
                        if (isTestCase(caller)) {
                            sb.append("\tCalled from: " + caller.getDeclaringClass().getName() + ", method: "
                                    + caller.getSubSignature() + "\n");
                        }
                    }
                }

                sb.append("\n# Helper Methods called multiple times from the same test case: "
                        + multiCalledHelpersFromSameTest.size() + "\n");
                for (Entry<SootMethod, Integer> entry : multiCalledHelpersFromSameTest.entrySet()) {
                    SootMethod helper = entry.getKey();
                    sb.append("Helper Method Class: " + helper.getDeclaringClass().getName() + ", Helper Method name: "
                            + helper.getSubSignature() + "\n");
                    Iterator<Edge> it = cg.edgesInto(helper);
                    while (it.hasNext()) {
                        SootMethod caller = it.next().src();
                        if (isTestCase(caller)) {
                            sb.append("\tCalled from: " + caller.getDeclaringClass().getName() + ", method: "
                                    + caller.getSubSignature() + "\n");
                        }
                    }
                }
                writer.write(sb.toString());
                writer.close();

                File csvOutputFile = new File(output_path + "/CSV_Files/" + benchmark + "_helper_method_counter.csv");
                try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
                    linesToAdd.stream().map(this::convertToCSV).forEach(pw::println);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Because the subsignature of methods could contain comma, we use tab as
        // delimiter for csv
        public String convertToCSV(String[] data) {
            return Stream.of(data).collect(Collectors.joining("\t"));
        }
    }

    private static boolean isTestCase(SootMethod sm) {
        // JUnit 3
        if (sm.getName().startsWith("test") && sm.getParameterCount() == 0 && sm.getReturnType().toString() == "void") {
            System.out.println("Test case found: " + sm.getSubSignature());
            return true;
        }

        // JUnit 4+

        List<soot.tagkit.Tag> smTags = sm.getTags();
        soot.tagkit.VisibilityAnnotationTag tag = (soot.tagkit.VisibilityAnnotationTag) sm
                .getTag("VisibilityAnnotationTag");
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
