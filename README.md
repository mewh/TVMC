# Three-Valued Model Checker

## Build and Run


To run the tool, execute the following command in the same directory as the JAR:

```
java -jar TVMC.jar <inputfile> <bound> <options>
```

`<inputfile>` refers to the JSON file containing the control flow graph.

`<bound>` is the maximum bound (eg. 20)

`<options>` maybe be one or several of the following:
 - `--checkSafety`
 - `--checkLiveness`