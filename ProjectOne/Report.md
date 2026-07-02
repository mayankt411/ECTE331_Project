# ECTE331 Project: Fault-Tolerant Autonomous Drone Navigation System
**Author:** Mayan (Student ID: mayankt411)  
**Subject:** ECTE331 - Real-Time Embedded Systems  
**Date:** June 28, 2026  

---

## 1. Executive Summary

This report documents the design, verification, and execution analysis of the simulated **Fault-Tolerant Autonomous Drone Navigation System** implemented in Java. The primary objective is to demonstrate concepts of software fault-tolerance, redundancy (Triple Modular Redundancy - TMR), exception handling, and reliability monitoring under noisy or failing sensor conditions.

The system utilizes three altitude sensors (Sensor A, Sensor B, Sensor C) and outputs a final voted altitude. If sensors fail or output corrupted readings, the controller adapts using voting, outlier exclusion, or fallback mechanisms. If critical failures repeat consecutively, the system initiates a transition to **SAFE MODE** and exits.

---

## 2. System Architecture & Fault-Tolerance Logic

### 2.1 Sensor Failure and Corruption Probability
Each sensor implements randomized behavior matching the specification:
* **Hardware Failure (0 - 14% chance):** Throws a custom `SensorReadException`.
* **Corrupted Reading (15 - 29% chance):** Returns an out-of-bounds reading outside `[0:200]` meters.
* **Valid Reading (30 - 99% chance):** Returns a valid integer reading within `[0:200]` meters.

### 2.2 Triple Modular Redundancy (TMR) Majority Voting
The `DroneController` votes on sensor outputs:
* **3 Valid Sensors:** If any two (or all three) are equal, the system selects that value. A differing sensor is flagged as an `OUTLIER`.
* **2 Valid Sensors:** If they are equal, the system selects that value.
* **No Majority (All differ or < 2 valid sensors):** System registers a reliability failure and falls back to the last known valid altitude.

### 2.3 Reliability and SAFE MODE Rules
A reliability failure increments the consecutive failure count if:
1. Fewer than 2 sensors are valid.
2. All valid sensors produce differing outputs (no majority).

If the system experiences **two consecutive reliability failures**, it throws a custom `SystemReliabilityException` causing the simulation to enter **SAFE MODE** and terminate.

---

## 3. Demonstration of Use Cases (Log Analysis)

Below are log file excerpts demonstrating all specified behaviors during simulation runs.

### Use Case 1: Healthy Majority Vote
From `log_4.txt` at Step 2:
```
[2026-07-01 21:34:27.576] [FLIGHT_LOG] --- Simulation Step 2 ---
[2026-07-01 21:34:27.583] [INPUT] Sensor A: 106m, Sensor B: -27m, Sensor C: 106m
[2026-07-01 21:34:27.606] [CORRUPTED_READING] Sensor B produced corrupted reading: -27 m
[2026-07-01 21:34:27.608] [OUTLIER_DETECTION] Outlier sensors detected: [Sensor B]
[2026-07-01 21:34:27.608] [MAJORITY_DECISION] Majority vote successful. Altitude set to: 106 m
```
* **Explanation:** Sensor A and C both agree on `106m`. Sensor B produces a corrupted reading (`-27m`) and is flagged as an outlier. TMR resolves the correct altitude successfully.

### Use Case 2: Sensor Failure (Exception Handling)
From `log_4.txt` at Step 4:
```
[2026-07-01 21:34:28.627] [FLIGHT_LOG] --- Simulation Step 4 ---
[2026-07-01 21:34:28.634] [SENSOR_FAILURE] Sensor B read failure: Hardware failure detected in Sensor B
[2026-07-01 21:34:28.637] [INPUT] Sensor A: 116m, Sensor B: FAILED, Sensor C: 116m
[2026-07-01 21:34:28.638] [SENSOR_FAILURE] Sensor B failed (exception thrown)
[2026-07-01 21:34:28.638] [OUTLIER_DETECTION] Outlier sensors detected: [Sensor B]
[2026-07-01 21:34:28.640] [MAJORITY_DECISION] Majority vote successful. Altitude set to: 116 m
```
* **Explanation:** Sensor B experiences a hardware fault throwing `SensorReadException`. The controller detects only 2 valid sensor readings, matches them (`116m`), and establishes a majority.

### Use Case 3: No Majority / All Differ (Single Failure)
From `log_4.txt` at Step 1:
```
[2026-07-01 21:34:27.035] [FLIGHT_LOG] --- Simulation Step 1 ---
[2026-07-01 21:34:27.036] [INPUT] Sensor A: 108m, Sensor B: 107m, Sensor C: 105m
[2026-07-01 21:34:27.037] [OUTLIER_DETECTION] All sensor outputs differ. Outliers: [Sensor A, Sensor B, Sensor C]
[2026-07-01 21:34:27.046] [RELIABILITY_FAILURE] Reliability failure occurred (1/2 consecutive). Detail: No majority found among sensor readings. All outputs differ.
[2026-07-01 21:34:27.046] [FALLBACK_DECISION] Falling back to previous altitude: 100 m
```
* **Explanation:** All three sensors have slightly different values. The controller fails to find a majority, increments the consecutive failures to 1, and falls back to the previous altitude.

### Use Case 4: SAFE MODE Triggered (Consecutive Failures)
From `log_4.txt` at Step 5 and 6:
```
[2026-07-01 21:34:29.152] [FLIGHT_LOG] --- Simulation Step 5 ---
...
[2026-07-01 21:34:29.159] [RELIABILITY_FAILURE] Reliability failure occurred (1/2 consecutive). Detail: No majority found among sensor readings. All outputs differ.
[2026-07-01 21:34:29.168] [FALLBACK_DECISION] Falling back to previous altitude: 116 m
...
[2026-07-01 21:34:29.676] [FLIGHT_LOG] --- Simulation Step 6 ---
[2026-07-01 21:34:29.685] [SENSOR_FAILURE] Sensor C read failure: Hardware failure detected in Sensor C
[2026-07-01 21:34:29.685] [INPUT] Sensor A: 225m, Sensor B: 132m, Sensor C: FAILED
[2026-07-01 21:34:29.687] [CORRUPTED_READING] Sensor A produced corrupted reading: 225 m
[2026-07-01 21:34:29.687] [SENSOR_FAILURE] Sensor C failed (exception thrown)
[2026-07-01 21:34:29.687] [RELIABILITY_FAILURE] Reliability failure occurred (2/2 consecutive). Detail: Fewer than 2 valid sensor readings exist. Outliers: [Sensor A, Sensor C]
[2026-07-01 21:34:29.687] [SAFE_MODE_ACTIVATION] CRITICAL: Two consecutive failures reached. Activating SAFE MODE!
[2026-07-01 21:34:29.689] [SAFE_MODE_ACTIVE] CRITICAL SYSTEM STATE: Catch block triggered SAFE MODE transition.
[2026-07-01 21:34:29.689] [SAFE_MODE_ACTIVE] Exception message: System entered SAFE MODE due to critical reliability failures.
[2026-07-01 21:34:29.689] [SYSTEM_STOP] System shut down safely and stopped execution.
```
* **Explanation:** In Step 5, all sensor outputs differ (failure 1). In Step 6, Sensor C fails with an exception and Sensor A produces a corrupted reading, leaving only one valid sensor (failure 2). Since two consecutive reliability failures occur, the system throws `SystemReliabilityException` and enters SAFE MODE.

---

## 4. Conclusion
The software successfully fulfills all functional, architectural, and reliability requirements of ECTE331. The TMR strategy ensures resilience against single faults, and the state-based monitoring guarantees a safe and structured exit under double faults.
