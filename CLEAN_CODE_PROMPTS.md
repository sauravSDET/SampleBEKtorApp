# Refactoring Prompt

Below is a consolidated **single prompt** that captures all the **refactoring principles** we have followed step by step. You can provide this to ChatGPT (or any developer) to **apply the exact same transformations** in one go:

---

### Refactoring Prompt

**Objective**: Refactor the given code base to make it more readable, maintainable, and consistent by following these principles:

1. **Single Responsibility Principle**
    - Each function should do **exactly one thing**.
    - Whenever you feel the need to write a comment explaining what a piece of code does, break that logic into a well-named private function instead.
2. **Use Function Names as Documentation**
    - Avoid inline comments.
    - Prefer *descriptive function names* that explain *what* they do.
    - Keep the *how* details inside the function body.
3. **Minimize Mutability**
    - Do **not** mutate fields of data classes directly.
    - Instead, return new data-class objects using `.copy(...)`.
    - This applies to both the main request object and any data containers we build.
4. **Decompose Large Methods**
    - For any big method (`invoke`, or otherwise), split the logic into smaller private functions.
    - Let the large method act as an “orchestrator” calling those smaller methods in sequence.
5. **Orchestration Stages**
    - Structure the high-level flow into clear stages:
        1. *Validate the request*
        2. *Prepare the request* (e.g., update or transform it if necessary)
        3. *Collect all relevant information* (e.g., fetching data from various services)
        4. *Prepare/Build the response object* to be returned
    - Each stage can be its own focused function or set of functions.
6. **Self-Contained Data Classes**
    - If multiple pieces of related data (e.g., various vehicle-related IDs) are needed across steps, create a **private data class** to store them (e.g. `VehicleData`).
    - Build that data class in a single private function, using further subfunctions as needed.
7. **No Comments, Clear Function Names**
    - If you find yourself wanting to place a comment, try to replace it with a dedicated function whose name is the comment.
    - Example:
        - Instead of `// remove scooter vehicle if eligible`, create `shouldRemoveScooter(...)` and `removeScooterVehicle(...)`.
8. **Check vs. Act Separation**
    - If you have a function that does “check a condition” and “perform an action,” split it into two:
        1. A “check” function (e.g. `shouldDisableAutoVehicle`).
        2. An “action” function (e.g. `removeAutoVehicle`).
9. **Functional Style Where Possible**
    - Use Kotlin’s **`map`, `flatMap`, `filter`, etc.** to avoid mutable collections.
    - Keep expressions concise and expressive.
10. **Build Incrementally**
    - If a piece of data needs multiple steps to derive, break each step into its own function.
    - Return an intermediate, *new* data class or list rather than mutating an existing one.
11. **Push Object Creation Close to Usage**
    - If an object is only needed within a particular private function, create it inside that function instead of passing it around from higher levels.
    - Minimize global or “far-up” object creation.

---

### How to Use This Prompt

1. **Provide your existing code** (the large method or class you want refactored).
2. **Include** the above prompt in your request.
3. ChatGPT (or the developer) will apply **all** these principles **in one pass**, transforming the code accordingly.

This ensures your final code base follows:

- **Single Responsibility** across small functions,
- **Minimal inline comments**,
- **Clear naming** for clarity,
- **Immutable data handling** via `.copy(...)`,
- **Data classes** for complex multi-field objects,
- and an overall **clean, maintainable** structure.

# Review Prompt

### Code Review Guidelines

1. **Single Responsibility Principle**
    - Check if each function or class is handling exactly one job.
    - If it’s doing more than one thing, recommend splitting it into smaller, more focused parts.
2. **Use Function Names for Documentation**
    - Avoid using explanatory comments within function bodies.
    - Prefer clearly named private functions that explain *what* is happening.
3. **Favor Immutability**
    - Look for places where data classes or objects are being mutated.
    - Suggest using `.copy(...)` to create a new instance when fields must change.
4. **Decompose Large Methods**
    - Identify any long methods with multiple responsibilities.
    - Suggest extracting sections of code into dedicated private functions.
5. **Orchestration Stages**
    - Check if the code follows clear stages: validate, prepare, collect, and build final output.
    - If not, advise reorganizing into these distinct phases.
6. **Utilize Self-Contained Data Classes**
    - If you see code that manually handles related data across multiple variables or fields,recommend creating a small data class (e.g. `VehicleData`) to group them logically.
7. **Replace Comments with Clear Function Names**
    - When encountering inline comments that describe *what* the code does,recommend creating a private function named accordingly.
8. **Separate “Check” and “Act”**
    - If a function both evaluates a condition and performs the resulting action,advise splitting it into two functions (e.g., `shouldRemoveVehicle(...)` vs. `removeVehicle(...)`).
9. **Leverage Kotlin’s Functional Style**
    - In place of manual loops and mutable lists, suggest using `map`, `flatMap`, or `filter`.
    - Look for ways to remove unnecessary mutable state.
10. **Incremental, Non-Mutating Steps**
    - Recommend building up complex data through multiple steps, returning new data structures rather than mutating existing ones.
    - This makes the code more predictable, testable, and readable.
11. **Push Object Creation Close to Usage**
    - Whenever you see objects created far away from their usage, suggest moving that creation inside the relevant function.
    - This reduces scope and makes the code simpler.

---

### How to Use

- **Provide your code** to the AI.
- **Give these guidelines** to the AI as part of your prompt.
- **Ask** the AI to apply each guideline, pointing out places where the code deviates and offering specific, actionable improvement suggestions.

The AI should return a review highlighting changes such as:

- Breaking up lengthy methods,
- Introducing well-named private functions,
- Eliminating mutability,
- Using `.copy(...)` for updates,
- Creating concise data classes,
- Replacing comments with clear function names, and
- Adopting a functional approach where appropriate.