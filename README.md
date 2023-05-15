[![Build Status](https://github.com/scijava/script-editor-scala/actions/workflows/build.yml/badge.svg)](https://github.com/scijava/script-editor-scala/actions/workflows/build.yml)

# script-editor-scala

Scala language support for SciJava Script Editor

Scala language support for SciJava [Script Editor](https://imagej.net/scripting/script-editor).
Provides auto completion for Scala code in the editor.


Hints for Developer
-------------------

### Interactive Testing from Command Line

You can start the Script Editor using Maven:

```
mvn -P"exec,editor"
```

### Printing Debugging Info Execution

Use Java System property `scijava.log.level` to control debug printout.
For instance, you can set it to `trace` during JVM invocation

```
-Dscijava.log.level=trace
```