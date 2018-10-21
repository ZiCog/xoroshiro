package XoroshiroPlusPlus

import spinal.core._
import spinal.sim._
import spinal.core.sim._

import scala.util.Random


// Xoroshiro64PlusPlus testbench
object XoroshiroPlusPlusSim {
  def main(args: Array[String]) {

    val compiled128 = SimConfig.withWave.compile{
      val dut = new Xoroshiro128StarStar
      dut
    }
    compiled128.doSim("test_Xoroshiro128**"){dut =>
      // Fork a process to generate the reset and the clock on the dut
      dut.clockDomain.forkStimulus(period = 10)

      var modelState = 0

      var idx = 0
      while(idx < 16){
        // Drive the DUT inputs with random values
        dut.io.next #= true

        // Wait a rising edge on the clock
        dut.clockDomain.waitRisingEdge()

        println(f"${dut.io.prngHigh.toLong}%08x" + f"${dut.io.prngLow.toLong}%08x" )
          // Check that the DUT values match with the reference model ones
        // val modelFlag = modelState == 0 || dut.io.cond1.toBoolean
        // assert(dut.io.state.toInt == modelState)
        // assert(dut.io.flag.toBoolean == modelFlag)

        // Update the reference model value
        //if(dut.io.cond0.toBoolean) {
        //  modelState = (modelState + 1) & 0xFF
        //}

        idx += 1
      }
    }

    val compiled64 = SimConfig.withWave.compile{
      val dut = new Xoroshiro64PlusPlus
      dut
    }

    compiled64.doSim("test_Xoroshiro64PlusPlus"){dut =>
      // Fork a process to generate the reset and the clock on the dut
      dut.clockDomain.forkStimulus(period = 10)

      var modelState = 0

      var idx = 0
      while(idx < 16){
        // Drive the DUT inputs with random values
        dut.io.next #= true

        // Wait a rising edge on the clock
        dut.clockDomain.waitRisingEdge()

        println(f"${dut.io.prng.toLong}%08x")

        // Check that the DUT values match with the reference model ones
        // val modelFlag = modelState == 0 || dut.io.cond1.toBoolean
        // assert(dut.io.state.toInt == modelState)
        // assert(dut.io.flag.toBoolean == modelFlag)

        // Update the reference model value
        //if(dut.io.cond0.toBoolean) {
        //  modelState = (modelState + 1) & 0xFF
        //}

        idx += 1
      }
    }

    val compiled32 = SimConfig.withWave.compile{
      val dut = new Xoroshiro32PlusPlus ;
      dut
    }

    compiled32.doSim("test_Xoroshiro32PlusPlus"){dut =>
      // Fork a process to generate the reset and the clock on the dut
      dut.clockDomain.forkStimulus(period = 10)

      var modelState = 0

      var idx = 0
      while(idx < 16){
        // Drive the DUT inputs with random values
        dut.io.next #= true

        // Wait a rising edge on the clock
        dut.clockDomain.waitRisingEdge()

        println(f"${dut.io.prng.toLong}%04x")

        // Check that the DUT values match with the reference model ones
        // val modelFlag = modelState == 0 || dut.io.cond1.toBoolean
        // assert(dut.io.state.toInt == modelState)
        // assert(dut.io.flag.toBoolean == modelFlag)

        // Update the reference model value
        //if(dut.io.cond0.toBoolean) {
        //  modelState = (modelState + 1) & 0xFF
        //}

        idx += 1
      }
    }



  }
}
