
xoroshiro
=========

Implementions of the xoroshiro32++, xoroshiro64++ and xoroshiro128** PRNG algorthims by David Blackman and Sebastiano Vigna in C++, Verilog, VHDL and SpinalHDL.

The xoroshiro32++ and xoroshiro18** PRNG algorithms are the same as those implemented in the hardware of the Parallax Propeller 2 micro-controller. Not finding any implementations of these around the net I created these whilst in discussion with those that put together the Parallax implementation[1] The origninal source of xoroshiro128** can be found in the Blackman/Vigna paper[2] along with descriptions of the others.

The xoroshiro64++ is included here as a nice long cycle PRNG that needs no multiply. The xoroshiro128** implementation here does not use multiplication despite it being specified as using two multiplies in paper. That makes them both small(er) and simple for implementation on FPGA or as software for machines with no multiply instruction.

HDL for use in FPGA is written using SpinalHDL[3] from which Verilog and VHDL versions can be generated.

### C++ Version

The xoroshiro32++, xoroshiro32++ and xoroshiro128** are implemented in the header file src/main/c/xoroshoro.h. There is a test harness in xoroshiro.cpp

#### Compile with:

    $ g++ -Wall -O3 -o  xoroshiro src/main/c/xoroshiro.cpp

#### Test with:

$ ./xoroshiro
First 16 outputs of Xoroshiro32++:
0201
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

First 16 outputs of Xoroshiro64++:
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
5788242d
d8ece2a3
7a242fab
add23d97
98ef01be

First 16 outputs of Xoroshiro128**:
0000000000001680
0000001696801680
e682e68000001680
800016f099c09692
db9ce96699c368c0
acd4a897e816f3bc
736225bd8540f37d
d5ef4bc8db65564a
e6844f92c4467f20
ecbfdd2089c19276
3e3c8f32277dc824
6135641f1dadd2ca
0695119b0de23cfa
8837ded221b7094d
99f5fdc2c04a92d6
57262a94b712228c


### SpinalHDL Version

#### Prerequists

To build Verilog from the SpinalHDL sources install the Scala Build Tool, sbt, as per the instructions in the SpinalHDL documentation.
To run the testbench also install the Verilator.

#### Generate Verilog

Clone or download this repository.

    $ git clone  https://github.com/ZiCog/xoroshiro.git

Open a terminal in the root of it and run "sbt run". At the first execution, the process could take some seconds

    $ cd xoroshiro
    $ sbt run

If you want to generate the Verilog of your design

    $ sbt "run-main XoroshiroPlusPlus.XororshiroPluPlusVerilog"

If you want to generate the VHDL of your design

    $ sbt "run-main Sodor.SodorVhdl"

Verilog and VHDL files will be generated into the root directory of the repo.

#### Test

If you want to run the Scala written testbench (see src/main/scala/XoroshiroPlusPlus/xoroshiro-plusplus.scala/xoroshiro-plusplus-sim.scala)

    $ sbt "run-main XoroshiroPlusPlus.XoroshiroPlusPlusSim"

Which should produce 16 iterations of each PRNG in it's output, c.f. the C++ results above.

    $ sbt "run-main XoroshiroPlusPlus.XoroshiroPlusPlusSim"
    [info] Loading project definition from /mnt/c/Users/michael/Documents/xoroshiro/project
    ...
    ...
    [info] [Progress] Start Xoroshiro128StarStar test_Xoroshiro128** simulation with seed -6512988233703174377, wave in /mnt/c/Users/michael/Documents/xoroshiro/./simWorkspace/Xoroshiro128StarStar/test_Xoroshiro128**.vcd
    [info] 0000000000000000
    [info] 0000000000001680
    [info] 0000001696801680
    [info] e682e68000001680
    [info] 800016f099c09692
    [info] db9ce96699c368c0
    [info] acd4a897e816f3bc
    [info] 736225bd8540f37d
    [info] d5ef4bc8db65564a
    [info] e6844f92c4467f20
    [info] ecbfdd2089c19276
    [info] 3e3c8f32277dc824
    [info] 6135641f1dadd2ca
    [info] 0695119b0de23cfa
    [info] 8837ded221b7094d
    [info] 99f5fdc2c04a92d6
    [info] [Done] Simulation done in 34.384 ms
    ...
    ...
    [info] [Progress] Start Xoroshiro64PlusPlus test_Xoroshiro64PlusPlus simulation with seed -5229270926031110962, wave in /mnt/c/Users/michael/Documents/xoroshiro/./simWorkspace/Xoroshiro64PlusPlus/test_Xoroshiro64PlusPlus.vcd
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
    [info] [Done] Simulation done in 11.564 ms
    ...
    ...
    [info] [Progress] Start Xoroshiro32PlusPlus test_Xoroshiro32PlusPlus simulation with seed -7323994742309800941, wave in /mnt/c/Users/michael/Documents/xoroshiro/./simWorkspace/Xoroshiro32PlusPlus/test_Xoroshiro32PlusPlus.vcd
    [info] 0000
    [info] 0201
    [info] 6269
    [info] ae16
    [info] 12a2
    [info] 4ae8
    [info] d719
    [info] 0c52
    [info] 984b
    [info] 1df1
    [info] 743c
    [info] dba0
    [info] bcc6
    [info] 34c9
    [info] 746c
    [info] 3643
    [info] [Done] Simulation done in 11.661 ms
    [success] Total time: 10 s, completed Oct 22, 2018 5:53:48 PM

The top level spinal code is defined in src/main/scala/XoroshiroPlusPlus/xoroshiro-plusplus.scala. The test bench is in xoroshiro-plusplus-sim.scala

### Verilog Version and Quartus Demo

The Verilog version included here is that generated from SpinalHDL above. In the files Xoroshiro32PlusPlus.v, Xoroshiro64PlusPlus.v, Xoroshiro128StarStar.v 

There is a Quartus project file that will build a demo of the xoroshiro128StarStar PRNG for the DE0 Nano board. The demo will display the high eight bits of the PRNG output on the 8 onboard LEDS and step through iterations, one every 1.5 seconds.

Other Verilog support files are:

* SwitchDebounce.v  -  Debounces switch presses.
* Monostable.v      -  Generates a single clock width pulse on detecting a rising edge in input
* Top.v             -  The top level Verilog of the demo.

### VHDL Version.

The VHDL version of xoroshiro64++ and xoroshoro32++ can be built from the SpinalHDL sources with the following command:

$ sbt "run-main XoroshiroPlusPlus.XororshiroPluPlusVhdl"

There is no test bench for the VHDL versions.

### References

[1] The Parallax Forums discussion: http://forums.parallax.com/discussion/166176/random-lfsr-on-p2/p62

[2] Scrambled Linear Pseudorandom Number Generators: https://arxiv.org/pdf/1805.01407.pdf

[3] SpinalHDL: http://spinalhdl.github.io/SpinalDoc
     
### Credits

David Blackman and Sebastiano Vigna for creating these and many other PRNG.

TonyB_ and evanh on the Parallax Forums for testing, help, discussion and advice

