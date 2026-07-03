# ECTE331 Project Three: Real-Time Robotic Arm Controller with Priority Management

## 1. System Overview
The goal of this project is to simulate a robotic arm system containing three real-time concurrent tasks (threads) that share a critical resource (`MotorController`). The three threads are:
- **Safety Monitor**: High Priority (Priority 8) - Detects emergency conditions and stops the arm.
- **Motion Planner**: Medium Priority (Priority 5) - Sends movement commands.
- **System Logger**: Low Priority (Priority 2) - Records system activity.

When threads of varying priorities share a resource, a common issue known as **Priority Inversion** occurs. This project implements a scenario demonstrating this issue and resolves it using two protocols: **Priority Inheritance** and **Priority Ceiling**.

## 2. Implementations (Tasks 1 & 2)
The shared resource (`MotorController`) requires mutual exclusion so that only one thread can access it at any given time. This is achieved using Java's `synchronized` block which acts as a monitor lock on the object. The three threads were successfully implemented by extending the Java `Thread` class and utilizing their respective `getPriority()` and `setPriority()` methods.

## 3. Priority Inversion Demonstration (Task 3)
A priority inversion scenario was constructed using `CountDownLatch` components to force the following sequence:
1. The low-priority **System Logger** acquires the `MotorController` resource lock.
2. The high-priority **Safety Monitor** starts execution and attempts to acquire the lock, becoming blocked (waiting for Logger).
3. The medium-priority **Motion Planner** starts executing a long-running CPU-bound task without needing the lock. 

Since the OS scheduler prioritizes the medium-priority Motion Planner over the low-priority System Logger, the Logger gets preempted and is delayed in releasing its lock. Consequently, the high-priority Safety Monitor is forced to wait for both the Logger and the Motion Planner, demonstrating severe priority inversion. 
Wait times observed during baseline testing average around **425-450 ms**.

## 4. Priority Inheritance Protocol (Task 4)
In this protocol, when a lower-priority thread holds a resource that a higher-priority thread needs, the lower-priority thread temporarily inherits the priority of the highest-priority blocked thread. This prevents medium-priority threads from preempting it.

This was simulated in `MotorControllerInheritance.java` by checking if a high priority thread is waiting, and boosting the holder's priority to 8 upon lock acquisition. As a result, the low-priority thread quickly finishes its critical section.
Wait times observed significantly drop to an average of **~290 ms**.

## 5. Priority Ceiling Protocol (Task 5)
In this protocol, the resource itself is assigned a "ceiling priority", which is equal to the highest priority of any thread that can ever access it (in this case, Priority 8 for the Safety Monitor). Whenever a thread acquires the resource, its priority is immediately boosted to the ceiling priority.

Implemented in `MotorControllerCeiling.java`, any thread accessing it immediately becomes Priority 8. This guarantees it will not be preempted by the Motion Planner (Priority 5).
Wait times observed drop to an average of **~295 ms**.

## 6. Performance Evaluation (Task 6)
An automated `PerformanceEvaluator` runs the three scenarios 10 times each and calculates the average wait time for the Safety Monitor thread.

### Output Result Chart:
```text
==================================================
   Task 6: Performance Evaluation (Average Wait Time)
==================================================
Average Waiting Time for High Priority Thread (Safety Monitor):
1. Baseline (No Protocol) : 428 ms
2. Priority Inheritance   : 292 ms
3. Priority Ceiling       : 297 ms

Chart Representation:
Baseline    | ##########################################
Inheritance | #############################
Ceiling     | #############################
```

**Conclusion:** Both Priority Inheritance and Priority Ceiling correctly solve the Priority Inversion problem, reducing the delay of the high-priority thread by ~30% in this simulation. Priority Ceiling acts immediately without needing to track blocked threads dynamically, making it slightly simpler to implement but raising thread priorities even when there is no contention. Priority Inheritance is slightly more dynamic but requires accurate knowledge of blocked threads.
