
xoroshiro-plusplus
==================

Implementions of the xoroshiro32++ and xoroshiro64++ PRNG algorthims by David Blackman and Sebastiano Vigna in C++, Verilog, VHDL and SpinalHDL.

These PRNG algorithms are the same as those implemented in the hardware of the Parallax Propeller 2 micro-controller. The xoroshiro64++ is included here as a nice long cycle PRNG that needs no multiply.

### C++ Version

The xoroshiro32++ and xoroshiro32++ are implemented in the header file src/main/c/xoroshoroPlusPlus.h. 

#### Compile with:

    $ g++ -Wall -O3 -o  xoroshiroPlusPlus src/main/c/xoroshiroPlusPlus.cpp

#### Test with:

    $ ./xoroshiroPlusPlus
    First 16 outputs of Xoroshiro32PlusPlus:
    6269
    ae16
    12a2
    4ae8
    d719
    0c52
    984b
    1df1
    743c
    dba0
    bcc6
    34c9
    746c
    3643
    07ff
    bbc0

    First 16 outputs of Xoroshiro64PlusPlus:
    48020a01
    81662931
    cd2b5253
    d3e6cbe6
    cd5af43d
    860aa4ba
    b7bea7fb
    63dcaff3
    762d74c9
    3e7d7e8f
    e10e0616
    v5788242d
    d8ece2a3
    7a242fab
    vadd23d97
    98ef01be


### SpinalHDL Version

#### Prerequists

To build Verilog from the SpinalHDL sources install the Scala Build Tool, sbt, as per the instructions in the SpinalHDL documentation.
To run the testbench also install the Verilator.

#### Generate Verilog

Clone or download this repository.

    $ git clone  https://github.com/ZiCog/sodor-spinal.git

Open a terminal in the root of it and run "sbt run". At the first execution, the process could take some seconds

    $ cd xoroshiro-plusplus
    $ sbt run

If you want to generate the Verilog of your design

    $ sbt "run-main XoroshiroPlusPlus.XororshiroPluPlusVerilog"

If you want to generate the VHDL of your design

    $ sbt "run-main Sodor.SodorVhdl"

Verilog and VHDL files will be generated into the root directory of the repo.

#### Test

If you want to run the Scala written testbench (see src/main/scala/XoroshiroPlusPlus/xoroshiro-plusplus.scala/xoroshiro-plusplus-sim.scala)

    $ sbt "run-main XoroshiroPlusPlus.XoroshiroPlusPlusSim"

Which should produce 16 iterations of the PRNG in it's output, c.f. the C++ reults above.

    [info] Loading project definition from /mnt/c/Users/zicog/Documents/xoroshiro-plusplus/project
    [info] Set current project to xoroshiro-plusplus (in build file:/mnt/c/Users/zicog/Documents/xoroshiro-plusplus/)
    [info] Running XoroshiroPlusPlus.XoroshiroPlusPlusSim
    [info] [Runtime] SpinalHDL v1.1.5    git head : 0310b2489a097f2b9de5535e02192d9ddd2764ae    
    [info] [Runtime] JVM max memory : 1817.0MiB
    [info] [Runtime] Current date : 2018.10.20 10:01:36
    [info] [Progress] at 0.000 : Elaborate components
    [info] [Progress] at 0.210 : Checks and transforms
    [info] [Progress] at 0.325 : Generate Verilog
    [info] [Progress] at 0.330 :   emit Xoroshiro64PlusPlus
    [info] [Info] Number of registers : 64
    [info] [Done] at 0.427
    [info] [Progress] Simulation workspace in /mnt/c/Users/zicog/Documents/xoroshiro-plusplus/./simWorkspace/Xoroshiro64PlusPlus
    [info] [Progress] Verilator compilation started
    [info] [Progress] Verilator compilation done in 3503.793 ms
    [info] [Progress] Start Xoroshiro64PlusPlus test_Xoroshiro64PlusPlus simulation with seed -296360197591574595, wave in             /mnt/c/Users/zicog/Documents/xoroshiro-plusplus/./simWorkspace/Xoroshiro64PlusPlus/test_Xoroshiro64PlusPlus.vcd
    [info] 48020a01
    [info] 81662931
    [info] cd2b5253
    [info] d3e6cbe6
    [info] cd5af43d
    [info] 860aa4ba
    [info] b7bea7fb
    [info] 63dcaff3
    [info] 762d74c9
    [info] 3e7d7e8f
    [info] e10e0616
    [info] 5788242d
    [info] d8ece2a3
    [info] 7a242fab
    [info] add23d97
    [info] 98ef01be
    [info] [Done] Simulation done in 38.373 ms
    [success] Total time: 6 s, completed Oct 20, 2018 10:01:41 AM

The top level spinal code is defined in src\main\scala\sodor 

### Verilog Version

The Verilog version is in the files Xoroshiro32PlusPlus.v and Xoroshiro64PlusPlus.v 

There is a Quartus project file that will build a demo of the xoroshiro64++ PRNG for the DE0 Nano board. The demo will display the output of the PRNG on the 8 onboard LEDS and step through iterations on each press of key 1.

Other Verilog support files are:

* SwitchDebounce.v  -  Debounces switch presses.
* Monostable.v      -  Generates a single clock width pulse on detecting a rising edge in input
* Top.v             -  The top level Verilog of the demo.

### VHDL Version.

The VHDL version of xoroshiro64++ and xoroshoro32++ can be built from the SpinalHDL sources with the following command:

$ sbt "run-main XoroshiroPlusPlus.XororshiroPluPlusVhdl"

There is no test bench for the VHDL versions.

### References

     Scrambled Linear Pseudorandom Number Generators: https://arxiv.org/pdf/1805.01407.pdf

     The Parallax Forums discussion: http://forums.parallax.com/discussion/166176/random-lfsr-on-p2/p62

     SpinalHDL: http://spinalhdl.github.io/SpinalDoc
     
### Credits

David Blackman and Sebastiano Vigna for creating these and many other PRNG.

TonyB_ and evanh on the Parallax Forums for testing, help, discussion and advice



