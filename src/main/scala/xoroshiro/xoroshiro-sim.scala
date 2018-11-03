package Xoroshiro

import spinal.core._
import spinal.sim._
import spinal.core.sim._

import scala.util.Random


// Xoroshiro64PlusPlus testbench
object XoroshiroSim {
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


// AsyncReceiver testbench
object AsyncReceiverSim {
  def main(args: Array[String]) {

    val compiled = SimConfig.withWave.compile {
      val dut = new AsyncReceiver
      dut.state.simPublic()
      dut.bitCount.simPublic()
      dut.shifter.simPublic()
      dut.bitTimeOut.simPublic()
      dut
    }
    compiled.doSim("AsyncReceiver") { dut =>

  //    var data : Long = 0

      def initDutSignals (): Unit = {
        dut.io.enable #= false
        dut.io.mem_valid #= false
        dut.io.mem_addr #= 0
        dut.io.baudClockX16 #= false
        dut.io.rx #= true
      }

      def busToRead(address:Int) {
        dut.io.enable #= true
        dut.io.mem_valid #= true
        dut.io.mem_addr #= address
      }

      def busToIdle() {
        dut.io.enable #= false
        dut.io.mem_valid #= false
        dut.io.mem_addr #= 0
      }

      var baudClock16Count = 0
      var baudClcokDiv = 0
      var baudClock16 = false
      def generateBaudClock16(): Unit = {
        baudClcokDiv += 1
        if (baudClcokDiv == 2) {
          baudClcokDiv = 0
          if (baudClock16) {
            baudClock16 = false
          } else {
            baudClock16 = true
            baudClock16Count += 1
          }
          dut.io.baudClockX16 #= baudClock16
        }
      }

      // Fork a process to generate the reset and the clock on the dut
      dut.clockDomain.forkStimulus(period = 10)

      var modelState = 0

      initDutSignals()

      var idx = 0
      while (idx < 1000) {
        generateBaudClock16()

        busToRead(4)
        dut.clockDomain.waitRisingEdge()
        // Read outputs
        print(f"clock: ${idx}%08d, ")
        print(f"baudclkCnt: ${baudClock16Count}%08d, ")
        print(f"rx: ${dut.io.rx.toBoolean}, ")
        print(f"state: ${dut.state.toInt}%08d, ")
        print(f"bitTimeOut: ${dut.bitTimeOut.toInt}%08d, ")
        print(f"bitcnt: ${dut.bitCount.toInt}%08d, ")
        print(f"shifter: ${dut.shifter.toInt}%08x, ")
        print(f"data: ${dut.io.mem_rdata.toLong}%08x")
        println()

        busToIdle()
        dut.clockDomain.waitRisingEdge()

        // Start bit
        if (baudClock16Count == 2 * 16) {
          dut.io.rx #= false
        }
        // Bit 0
        if (baudClock16Count == 3 * 16) {
          dut.io.rx #= false
        }
        // Bit 1
        if (baudClock16Count == 4 * 16) {
          dut.io.rx #= true
        }
        // Bit 2
        if (baudClock16Count == 5 * 16) {
          dut.io.rx #= false
        }
        // Bit 3
        if (baudClock16Count == 6 * 16) {
          dut.io.rx #= true
        }
        // Bit 4
        if (baudClock16Count == 7 * 16) {
          dut.io.rx #= false
        }
        // Bit 5
        if (baudClock16Count == 8 * 16) {
          dut.io.rx #= true
        }
        // Bit 6
        if (baudClock16Count == 9 * 16) {
          dut.io.rx #= false
        }
        // Bit 7
        if (baudClock16Count == 10 * 16) {
          dut.io.rx #= true
        }
        // Stop bit
        if (baudClock16Count == 11 * 16) {
          dut.io.rx #= true
        }

        idx += 1
      }


      busToRead(0)
      dut.clockDomain.waitRisingEdge()
      // Read outputs
      println("Expect data = 55 :")
      print(f"clock: ${idx}%08d, ")
      print(f"baudclkCnt: ${baudClock16Count}%08d, ")
      print(f"rx: ${dut.io.rx.toBoolean}, ")
      print(f"state: ${dut.state.toInt}%08d, ")
      print(f"bitTimeOut: ${dut.bitTimeOut.toInt}%08d, ")
      print(f"bitcnt: ${dut.bitCount.toInt}%08d, ")
      print(f"shifter: ${dut.shifter.toInt}%08x, ")
      print(f"data: ${dut.io.mem_rdata.toLong}%08x")
      println()

      busToRead(4)
      dut.clockDomain.waitRisingEdge()
      // Read outputs
      println("Expect full = 0 :")
      print(f"clock: ${idx}%08d, ")
      print(f"baudclkCnt: ${baudClock16Count}%08d, ")
      print(f"rx: ${dut.io.rx.toBoolean}, ")
      print(f"state: ${dut.state.toInt}%08d, ")
      print(f"bitTimeOut: ${dut.bitTimeOut.toInt}%08d, ")
      print(f"bitcnt: ${dut.bitCount.toInt}%08d, ")
      print(f"shifter: ${dut.shifter.toInt}%08x, ")
      print(f"full: ${dut.io.mem_rdata.toLong}%08x")
      println()
    }




  }
}
