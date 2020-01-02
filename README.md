JUMBOConverters
===============
A set of libraries ("converters") which provide conversion to and from CML.

---
*Note:*
As of 2020-01-01 the the official home of the JUMBOConverters is:
<https://github.com/BlueObelisk/jumbo-converters>.  
The old home of JUMBOConverters at: <https://bitbucket.org/wwmm/jumbo-converters>,
is subject to be removed once Bitbucket removes all Mercurial repositories mid-2020.

---

In general conversion TO CML is lossless as CML can - in principle - hold most concepts found in chemical files. In
general conversion FROM CML is lossy. JUMBOConverters concentrates on preserving document structure where possible and
therefore is particularly well suited for complex document types (although converters for simpler ones are obviously
possible).

JUMBOConverters may normalize or transform certain aspects of CML (e.g. deducing atomParity from bondStereo). However it
is not primarily an engine for calculating molecular or other properties.

JUMBOConverters is designed as a series of modules where the output from one can be piped into the input of another. It
should be possible to integrate it into workflows or scripts. Each module should be side-effect-free (although error-
reporting and logging is still being worked on).

The JUMBOConverters package includes a simple tool for converting all files in a directory structure. This uses ApacheCommons
DirectoryWalker and visits every file in the tree with a given suffix. The output files all have the same given output suffix and
 are located in a specified absolute or relative directory structure.

Each converter is accessed through its java classname, such as
`org.xmlcml.cml.converters.molecule.pubchem.PubchemXML2CMLConverter`
The caller must know the name of the converter (there is no inifinite magic yet).

The distribution includes an executable file whose `main()` method runs `org.xmlcml.cml.converters.ConverterCommand`.
This must be in your classpath. This uses the Apache CommandLine interface to interpret a command. Typical usage:

 * `java -jar jumboconverters.jar`  
   will give the commandline options (and I hope a list of converter classes)
 * `java -jar jumboconverters.jar -converter org.xmlcml.cml.converters.molecule.pubchem.PubchemXML2CMLConverter`  
   will run the Pubchem2CML converter in the current directory and with default suffixes (not recommended)
 * `java -jar jumboconverters.jar -converter org.xmlcml.cml.converters.molecule.pubchem.PubchemXML2CMLConverter -sd examples/input -odir ../output -is pubchem.xml -os pubchem.cml`  
   will run the Pubchem2CML converter starting in the grandchild directory (examples/input) and output to
   examples/output (relative to each input directory). the input suffix is pubchem.xml and the output pubchem.cml
