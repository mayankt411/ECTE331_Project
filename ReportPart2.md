# ECTE331 Project - Part 2: Thread Synchronisation and Communication
**Author:** Mayan (mayankt411)  
**Subject:** ECTE331 – Real-Time Embedded Systems  
**Date:** July 2, 2026

---

## 1. Part (a): Correct Final Values of Shared Variables

Using the analytical formula $\sum_{i=0}^{n} i = \frac{n(n+1)}{2}$:

| Variable | Formula | Computation | Result |
|---|---|---|---|
| A1 | $\sum_{i=0}^{500} i$ | $\frac{500 \times 501}{2}$ | **125,250** |
| B1 | $\sum_{i=0}^{250} i$ | $\frac{250 \times 251}{2}$ | **31,375** |
| B2 | $A1 + \sum_{i=0}^{200} i$ | $125{,}250 + \frac{200 \times 201}{2}$ | **145,350** |
| A2 | $B2 + \sum_{i=0}^{300} i$ | $145{,}350 + \frac{300 \times 301}{2}$ | **190,500** |
| B3 | $A2 + \sum_{i=0}^{400} i$ | $190{,}500 + \frac{400 \times 401}{2}$ | **270,700** |
| A3 | $B3 + \sum_{i=0}^{400} i$ | $270{,}700 + \frac{400 \times 401}{2}$ | **350,900** |

---

## 2. Part (b): Synchronisation Strategy

### Dependency Analysis
From Figure 2.1, the execution dependencies are:
- **FuncB2 depends on FuncA1**: B2 reads `A1`, which is written by FuncA1.
- **FuncA2 depends on FuncB2**: A2 reads `B2`, which is written by FuncB2.
- **FuncB3 depends on FuncA2**: B3 reads `A2`, which is written by FuncA2.
- **FuncA3 depends on FuncB3**: A3 reads `B3`, which is written by FuncB3.

FuncA1 and FuncB1 have no cross-thread dependency and can run freely in parallel.

### Synchronisation Mechanism: Binary Semaphores

Active waiting (busy-loop on a condition) and `Thread.sleep` both waste CPU resources and are not suitable for real-time embedded systems contexts where determinism and efficiency matter.

Instead, we use **binary semaphores** from `java.util.concurrent.Semaphore`, initialised to `0` permits. When a thread calls `acquire()` on a zero-permit semaphore, it **blocks** (suspending without consuming CPU) until another thread calls `release()`. This is the standard producer-consumer coordination mechanism.

Four semaphores are used:

| Semaphore | Released by | Acquired by | Purpose |
|---|---|---|---|
| `semA1` | Thread A (after FuncA1) | Thread B (before FuncB2) | Ensures FuncB2 runs only after A1 is ready |
| `semB2` | Thread B (after FuncB2) | Thread A (before FuncA2) | Ensures FuncA2 runs only after B2 is ready |
| `semA2` | Thread A (after FuncA2) | Thread B (before FuncB3) | Ensures FuncB3 runs only after A2 is ready |
| `semB3` | Thread B (after FuncB3) | Thread A (before FuncA3) | Ensures FuncA3 runs only after B3 is ready |

This correctly enforces all dependencies irrespective of the OS thread scheduler's behaviour.

---

## 3. Part (c): Code Implementation

### Class Descriptions

| Class | Responsibility |
|---|---|
| `MathUtility.java` | Static utility method `sumTo(n)` using a for-loop |
| `SharedVariables.java` | Holds all shared `volatile` variables A1–A3, B1–B3 |
| `ThreadA.java` | Implements FuncA1, FuncA2, FuncA3 with semaphore sync |
| `ThreadB.java` | Implements FuncB1, FuncB2, FuncB3 with semaphore sync |
| `SyncMain.java` | Main class: creates threads, runs iterations, verifies |

### Key Synchronisation Code (ThreadA.java)

```java
// FuncA1 has no dependency — runs immediately
funcA1();
semA1.release();          // Signal Thread B: A1 is ready

semB2.acquire();          // Block until Thread B finishes FuncB2
funcA2();
semA2.release();          // Signal Thread B: A2 is ready

semB3.acquire();          // Block until Thread B finishes FuncB3
funcA3();
```

### Key Synchronisation Code (ThreadB.java)

```java
// FuncB1 has no dependency — runs immediately in parallel with FuncA1
funcB1();

semA1.acquire();          // Block until Thread A finishes FuncA1
funcB2();
semB2.release();          // Signal Thread A: B2 is ready

semA2.acquire();          // Block until Thread A finishes FuncA2
funcB3();
semB3.release();          // Signal Thread A: B3 is ready
```

The use of `volatile` on the shared fields in `SharedVariables.java` ensures that writes made by one thread are immediately visible to the other thread, preventing CPU cache coherence issues.

---

## 4. Part (d): Verification Loop and Testing

The `SyncMain.java` class runs the two-thread system for **10,000 iterations**. In each iteration:
1. A fresh `SharedVariables` instance is created (reset to zero).
2. Four new semaphores are initialised to 0.
3. Thread A and Thread B are instantiated and started.
4. `Thread.join()` is called on both threads to block the main thread until both finish.
5. The computed values are compared against the analytically expected values.

A high iteration count is used to deliberately stress-test the synchronisation under varied OS scheduling scenarios. If even one of the 10,000 iterations produced a wrong result, it would indicate a race condition or ordering violation. 

### Verification Output

```
=== ECTE331 Part 2: Thread Synchronisation Verification ===
Running 10,000 iterations to verify correctness...

--- Sample Output from Iteration 1 ---
  A1 = 125,250  (expected: 125,250)
  B1 = 31,375   (expected: 31,375)
  B2 = 145,350  (expected: 145,350)
  A2 = 190,500  (expected: 190,500)
  B3 = 270,700  (expected: 270,700)
  A3 = 350,900  (expected: 350,900)

=== Verification Summary ===
Total Iterations : 10,000
PASSED           : 10,000
FAILED           : 0
Result: ALL ITERATIONS PASSED. Synchronisation is CORRECT.
```

All 10,000 iterations produced correct results, confirming the semaphore-based synchronisation is correct and deterministic.
