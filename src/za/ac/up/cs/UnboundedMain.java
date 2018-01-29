package za.ac.up.cs;

import cnf.Formula;
import cnf.Var;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.minisat.core.Solver;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.IteratorInt;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static cnf.CNF.and;
import static cnf.CNF.neg;

public class UnboundedMain {

    public static void main(String[] args) throws IOException {
        if (args.length > 3) {
            printUsage();
            return;
        }
        int loc = 1;

        try {
            System.out.println(new Date());
            int maxBound = Integer.parseInt(args[2]);

            System.out.println("Max Bound: " + maxBound);

            CFG cfg1 = Helpers.readCfg(args[0]);
            CFG cfg2 = Helpers.readCfg(args[1]);

            Properties config = Helpers.loadConfigurationFile();
            System.out.println();
            UnboundedModelChecker modelChecker = new UnboundedModelChecker(cfg1, maxBound, config);
            Solver solver = SolverFactory.newMiniLearningHeap();

            System.out.println("===========" + args[0] + "===========");
            checkSafety(maxBound, modelChecker, solver, loc);
            System.out.println();
            System.out.println("===========" + args[1] + "===========");
            modelChecker.setCfgs(cfg2);
            checkSafety(maxBound, modelChecker, solver, loc);
//            solver.addClause()
//        solver.addAllClauses(clauses); //add a formula consisting of clauses to the solver
//        solver.addClause(literals);    //add a clause consisting of literals to the solver
//        solver.isSatisfiable(assumps); // check satisfiability under assumptions e.g. 3 or -3 if 3 represents 'unknown'


        } catch (NumberFormatException e) {
            System.out.println("Number format exception: " + e);
        }


    }

    private static void checkSafety(int maxBound, UnboundedModelChecker modelChecker, Solver solver, int loc) {
        Formula baseCase = modelChecker.getBaseCaseFormula(null);
        System.out.println("baseCase = " + baseCase);
        System.out.println();

        // Safety encoding
        List<Formula> safetyFormulas = new ArrayList<>();
        for (int k = 0; k < maxBound - 1; k++) {
            safetyFormulas.add(modelChecker.safeLoc(k, loc, 4));
        }
        safetyFormulas.add(neg(modelChecker.safeLoc(maxBound - 1, loc, 4)));
        Formula ltlEncoding = and(safetyFormulas);
        System.out.println("ltlEncoding = " + ltlEncoding);
        //

        Formula formula = and(baseCase, ltlEncoding);

        //            boolean b = modelChecker.checkSatisfiability(and(baseCase, neg(UnboundedModelChecker.UNKNOWN)), solver, null);
        System.out.println("==== UNKNOWN FORMULA ====");
        ArrayList<Integer> unknownAssumps = new ArrayList<>();
        for (Map.Entry<String, Var> e : modelChecker.getVars().entrySet()) {
            if (e.getKey().startsWith("u")) {
                unknownAssumps.add(e.getValue().number);
            }
        }
        unknownAssumps.add(3);
        int[] unknownInts = unknownAssumps.stream().mapToInt(x -> x).toArray();
        boolean bUnknown = modelChecker.checkSatisfiability(formula, solver, new VecInt(unknownInts));
        modelChecker.printVars();
        System.out.println("Is satisfiable? = " + bUnknown);

        printStats(solver);

        System.out.println();
        System.out.println("==== NOT UNKNOWN FORMULA ====");
        ArrayList<Integer> notUnknownAssumps = new ArrayList<>();
        for (Map.Entry<String, Var> e : modelChecker.getVars().entrySet()) {
            if (e.getKey().startsWith("u")) {
                notUnknownAssumps.add(-e.getValue().number); // Note the negation
            }
        }
        notUnknownAssumps.add(-3);
        int[] notUnknownInts = notUnknownAssumps.stream().mapToInt(x -> x).toArray();

        boolean bNotUnknown = modelChecker.checkSatisfiability(formula, solver, new VecInt(notUnknownInts));
        modelChecker.printVars();
        System.out.println("Is satisfiable? = " + bNotUnknown);

        printStats(solver);

        System.out.println();

        System.out.println("Unknown formula satisfiable: " + bUnknown);
        System.out.println("Not unknown formula satisfiable: " + bNotUnknown);
    }

    private static void printStats(Solver solver) {
        PrintWriter out = new PrintWriter(System.out);
        solver.printLearntClausesInfos(out, "Learnt clause: ");
        out.flush();
        System.out.println();

        IVec learnedConstraints = solver.getLearnedConstraints();
//                IteratorInt iterator = outLearnt.iterator();
        Iterator iterator = learnedConstraints.iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            System.out.println("Learned constraint: " + next);
        }

        IVecInt outLearnt = solver.getOutLearnt();
        IteratorInt outLearntIterator = outLearnt.iterator();
        while (outLearntIterator.hasNext()) {
            System.out.println("solver.getOutLearnt() : " + outLearntIterator.next());
        }

        Map stat = solver.getStat();

        stat.forEach((o, o2) -> System.out.println(o + " --- " + o2));
    }

    private static void printUsage() {
        System.out.println("USAGE: inputfile.json <maxBound>");
    }

}
