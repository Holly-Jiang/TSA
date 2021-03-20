# TSA
## Introduction
The goal of quantum circuit adjustment is to construct mappings from logical quantum circuits to physical ones in an acceptable amount of time, and in the meantime to introduce  as few auxiliary gates as possible. We present an effective approach to constructing the mappings. It consists of two keys steps: one makes use of a combined subgraph isomorphism (CSI) to initialize a mapping, the other dynamically adjusts the mapping by using a Tabu search based adjustment (TSA). Our experiments show that, compared with the VF2 algorithm recently considered in the literature, CSI can save 22.26\% of auxiliary gates and reduce the depths of output circuits by 11.76\% on average in the initialization of the mapping, and  TSA has a better scalability than many state-of-the-art algorithms for adjusting mappings.

For the details, please refer to our paper
"Quantum Circuit Transformation Based on Subgraph Isomorphism and Tabu Search".
If you have any further questions, please feel free to contact us.

Please cite our paper, if you use our source code.

## Project structure
- out
    - artifacts: Store the corresponding jar package
        - PrecessorMain_jar: Batch preprocessing
        - IniMappingComplete_jar: Batch generate initial mappings
        - MainCombine_jar: Comparison of initial mapping results
        - MyTabuSearch_jar: Batch adjust the circuits
        - single_jar: Handling a single circuit
- src: Project codes

## Compile
First of all, we need to package the SubgraphMatching, please see the SubgraphMatching project for details.
Under the directory of the project`src/main/java/cn/ecnu/tabusearch/run`, 
package each class into a jar package and place it in the folder `out/artifacts`.

## Test
**Initial mapping comparison**
Use the following commands to perform the greedy algorithm _optm_, VF2 subgraph matching initial mapping _wgtgraph_,
and the comparison of the _CSIC_ initial mapping . The parameter indicates the size limit of the processing circuit.
The initial mapping of _CSIC_ uses the initial mapping that has been generated in `src/resources/pri_ini_qx20/`. 
 ```
java -jar tabusearch.jar [small/medium/large/all]
```
```
3_17_13
0 36 22 0
1 78 52 6
2 36 22 0
3 36 22 0
```
The result stored in file `/src/main/resources/compare/total_A_ini_connect`. 
The first line is the circuit name, the second line is the initial circuit situation, the third line is the result of optm, 
the fourth line is the result of wght, and the fifth line is the result of CSIC. 
In each line, 'a b c d', a indicates which kind of circuit. 1 represents the initial circuit, 2 represents optm, 3 represents wght, 
and 4 represents CSIC.
b represents the latest few 2-qubit gates of the generating circuit, c represents the minimum depth of the generating circuit, 
and d represents the minimum number of exchanges of the generating circuit.

**Handling a single circuit**
Execute the following command to process a single circuit in the folder where the jar package is located.
```$xslt
java -jar tabusearch.jar ../../../src/main/resources/example/0example.qasm connect num
```
The result stored in file `src/resources/compare/total`. Each circuit starts with 'x y z m n' where x represents the smallest initial mapping file subscript, y represents the initial number of 2-qubits gates, z represents the number of 2-qubit gates of the generating circuit, m represents the number of layers of the generating circuit, n represents the minimum number of exchanges, and t represents the time required for the search. 
```
0example
51 9 12 6 1 0
```


**Batch process the circuits**

First execute the following command in the PrecessorMain_jar folder: batch preprocessing and
  start_index represents the start index of the filelist.
```$xslt
java -jar tabusearch.jar [start_index]
```
The result stored in folders `src/resources/examples_result/` and `src/resources/pre_result/`.
 
Second execute the following command in the IniMappingComplete_jar folder: connect or num to indicate the strategy for generating the initial mapping.

```$xslt
java -jar tabusearch.jar  connect
```
The results are stored in the folder `src/resources/pre_ini_qx20/`, which includes all initial mappings.

Final execute the following command in the MyTabuSearch_jar: small/medium/large/all represents the scale of the circuit, connect/num represents the strategy for generating the initial mapping, and num/depth represents the two evaluation functions.
```$xslt
java -jar tabusearch.jar small  connect num
or
java -jar tabusearch.jar medium  degree num
or
java -jar tabusearch.jar large  connect depth

```
The result in `\TSA\tsa\src\main\resources\compare\total`.




