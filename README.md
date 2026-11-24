# SPL Assignment 2 – Concurrent Task Execution Engine

This project was developed as part of the **Systems Programming Laboratory (SPL)** course.

The system implements a concurrent execution engine that processes mathematical operations defined in JSON input files.
Tasks are parsed, scheduled, and executed using multiple threads.

The project demonstrates principles of **concurrent programming, task scheduling, and modular system design in Java**.

---

## Project Structure

src/        Java source code  
Examples/   Example input and output files  
pom.xml     Maven build configuration  

---

## Technologies

- Java
- Maven
- Multithreading
- JSON parsing

---

## Build

To compile the project run:

mvn clean compile

---

## Run

To run the program:

mvn exec:java

Alternatively, you can package the project:

mvn package

---

## Example Input

Example input files can be found in the `Examples` directory.

These files describe operations that the system should execute concurrently.

---

## Authors

- Ron Ohayon
- Liad Levi
