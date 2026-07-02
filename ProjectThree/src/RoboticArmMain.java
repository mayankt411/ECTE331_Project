/**
 * Main entry point for Project Three.
 */
public class RoboticArmMain {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("==================================================");
        System.out.println("   Tasks 1-3: Baseline Priority Inversion Demo");
        System.out.println("==================================================");
        PriorityInversionScenario.runScenario(new MotorController(), false);
        
        System.out.println("\n==================================================");
        System.out.println("   Task 4: Priority Inheritance Protocol");
        System.out.println("==================================================");
        PriorityInversionScenario.runScenario(new MotorControllerInheritance(), false);
        
        System.out.println("\n==================================================");
        System.out.println("   Task 5: Priority Ceiling Protocol");
        System.out.println("==================================================");
        PriorityInversionScenario.runScenario(new MotorControllerCeiling(), false);
        
        System.out.println();
        PerformanceEvaluator.runEvaluation();
    }
}
