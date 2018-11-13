package Xoroshiro

import spinal.core._
import spinal.sim._
import spinal.core.sim._

import scala.util.Random


// AsyncReceiver testbench
object AsyncReceiverSim {
  def main(args: Array[String]) {

    val compiled = SimConfig.withWave.compile {
      val dut = new AsyncReceiver
      dut.state.simPublic()
      dut.bitCount.simPublic()
      dut.shifter.simPublic()
      dut.bitTimer.simPublic()
      dut.fifo.head.simPublic()
      dut.fifo.tail.simPublic()
      dut.fifo.full.simPublic()
      dut.fifo.empty.simPublic()
      dut
    }
    compiled.doSim("AsyncReceiver") { dut =>

      var baudClock64Count = 0
      var baudClcokDiv = 0
      var baudClock64 = false

      def generateBaudClock64(): Unit = {
        baudClcokDiv += 1
        if (baudClcokDiv == 2) {
          baudClcokDiv = 0
          if (baudClock64) {
            baudClock64 = false
          } else {
            baudClock64 = true
            baudClock64Count += 1
          }
          dut.io.baudClockX64 #= baudClock64
        }
      }

      def initDutSignals (): Unit = {
        dut.io.enable #= false
        dut.io.mem_valid #= false
        dut.io.mem_addr #= 0
        dut.io.baudClockX64 #= false
        dut.io.rx #= true
      }

      def printSignals(title: String): Unit = {
        print(title)
        print(f"baudclkCnt: ${baudClock64Count}%08d, ")
        print(f"rx: ${dut.io.rx.toBoolean}, ")
        print(f"state: ${dut.state.toInt}%08d, ")
        print(f"bitTimeOut: ${dut.bitTimer.toInt}%08d, ")
        print(f"bitcnt: ${dut.bitCount.toInt}%08d, ")
        print(f"shifter: ${dut.shifter.toInt}%08x, ")
        print(f"data: ${dut.io.mem_rdata.toLong}%08x")
        println()
      }

      def busRead(address:Int): Unit@suspendable = {
        dut.io.enable #= true
        dut.io.mem_valid #= true
        dut.io.mem_addr #= address
        while (!dut.io.mem_ready.toBoolean) {
          dut.clockDomain.waitRisingEdge()
        }
        printSignals("Reading")
        dut.io.enable #= false
        dut.io.mem_valid #= false
        dut.io.mem_addr #= 0
        dut.clockDomain.waitRisingEdge()
      }

      def waitOneBitTime(): Unit@suspendable = {
        var count = 0
        var baudClockX64 = false

        while (count < 64) {
          dut.io.baudClockX64 #= baudClockX64
          dut.clockDomain.waitRisingEdge(4)
          baudClockX64 = !baudClockX64
          dut.io.baudClockX64 #= baudClockX64
          dut.clockDomain.waitRisingEdge(4)
          baudClockX64 = !baudClockX64

          count += 1
        }
      }

      // Fork a process to generate the reset and the clock on the dut
      dut.clockDomain.forkStimulus(period = 10)

      initDutSignals()

      var idx = 0
      while (idx < 40) {
        //println("Reading RX status, expect 0")
        busRead(4)

        // Start bit
          dut.io.rx #= false
          waitOneBitTime()
        // Bit 0
          dut.io.rx #= false
          waitOneBitTime()
        // Bit 1
          dut.io.rx #= true
          waitOneBitTime()
        // Bit 2
          dut.io.rx #= false
          waitOneBitTime()
        // Bit 3
          dut.io.rx #= true
          waitOneBitTime()
        // Bit 4
          dut.io.rx #= false
          waitOneBitTime()
        // Bit 5
          dut.io.rx #= true
          waitOneBitTime()
        // Bit 6
          dut.io.rx #= false
          waitOneBitTime()
        // Bit 7
          dut.io.rx #= true
          waitOneBitTime()
        // Stop bit
          dut.io.rx #= true
          waitOneBitTime()

        idx += 1
      }

      idx = 0
      while (idx < 50) {

        println("Reading RX , expect aa")
        busRead(0)

        print(f"empty: ${dut.fifo.empty.toBoolean}, ")
        print(f"full: ${dut.fifo.full.toBoolean}, ")
        print(f"head: ${dut.fifo.head.toInt}%08d, ")
        print(f"tail: ${dut.fifo.tail.toInt}%08d, ")
        println()

        println("Reading RX status, expect 0")
        busRead(4)

        print(f"empty: ${dut.fifo.empty.toBoolean}, ")
        print(f"full: ${dut.fifo.full.toBoolean}, ")
        print(f"head: ${dut.fifo.head.toInt}%08d, ")
        print(f"tail: ${dut.fifo.tail.toInt}%08d, ")
        println()

        idx += 1
      }
    }
  }
}
