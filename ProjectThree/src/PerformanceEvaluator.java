/**
 * Task 6: Performance Evaluation
 */
public class PerformanceEvaluator {
    
    private static final int ITERATIONS = 10;

    public static void runEvaluation() throws InterruptedException {
        System.out.println("==================================================");
        System.out.println("   Task 6: Performance Evaluation (Average Wait Time)");
        System.out.println("==================================================");
        
        long baselineTotal = 0;
        long inheritanceTotal = 0;
        long ceilingTotal = 0;
        
        // 1. Baseline
        for (int i = 0; i < ITERATIONS; i++) {
            baselineTotal += PriorityInversionScenario.runScenario(new MotorController(), true);
        }
        long baselineAvg = baselineTotal / ITERATIONS;
        
        // 2. Priority Inheritance
        for (int i = 0; i < ITERATIONS; i++) {
            inheritanceTotal += PriorityInversionScenario.runScenario(new MotorControllerInheritance(), true);
        }
        long inheritanceAvg = inheritanceTotal / ITERATIONS;
        
        // 3. Priority Ceiling
        for (int i = 0; i < ITERATIONS; i++) {
            ceilingTotal += PriorityInversionScenario.runScenario(new MotorControllerCeiling(), true);
        }
        long ceilingAvg = ceilingTotal / ITERATIONS;
        
        System.out.println("Average Waiting Time for High Priority Thread (Safety Monitor):");
        System.out.printf("1. Baseline (No Protocol) : %d ms%n", baselineAvg);
        System.out.printf("2. Priority Inheritance   : %d ms%n", inheritanceAvg);
        System.out.printf("3. Priority Ceiling       : %d ms%n", ceilingAvg);
        
        System.out.println("\nChart Representation:");
        System.out.println("Baseline    | " + generateBar(baselineAvg));
        System.out.println("Inheritance | " + generateBar(inheritanceAvg));
        System.out.println("Ceiling     | " + generateBar(ceilingAvg));
    }
    
    private static String generateBar(long value) {
        int length = (int) (value / 10);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append("#");
        }
        return sb.toString();
    }
}
