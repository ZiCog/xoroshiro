package Xoroshiro

import spinal.core._
import spinal.sim._
import spinal.core.sim._

import scala.util.Random


// AsyncReceiver testbench
object FifoSim {
  def main(args: Array[String]) {

    val compiled = SimConfig.withWave.compile {
      val dut = new Fifo
      dut.count.simPublic()
      dut.head.simPublic()
      dut.tail.simPublic()
      dut
    }
    compiled.doSim("Fifo") { dut =>

      def fifoWriteByte(byte: Int): Unit@suspendable = {
        dut.io.dataIn #= byte
        dut.io.write #= true
        dut.io.read #= false
        fifoTick()
      }

      def fifoReadByte(): Unit@suspendable = {
        dut.io.dataIn #= 0x66
        dut.io.read #= true
        dut.io.write #= false
        fifoTick()
      }

      def fifoNullOp(): Unit@suspendable = {
        dut.io.dataIn #= 0x66
        dut.io.read #= false
        dut.io.write #= false
        fifoTick()
      }


      def fifoPrintSignals(): Unit = {
        print(f"count: ${dut.count.toInt}%02x, ")
        print(f"head: ${dut.head.toInt}%02x, ")
        print(f"tail: ${dut.tail.toInt}%02x, ")
        print(f"full: ${dut.io.full.toBoolean}, ")
        print(f"empty: ${dut.io.empty.toBoolean}, ")
        print(f"dataOut: ${dut.io.dataOut.toInt}%02x, ")
        println()
      }

      def fifoTick(): Unit@suspendable = {
        dut.clockDomain.waitRisingEdge()
      }

      // Fork a process to generate the reset and the clock on the dut
      dut.clockDomain.forkStimulus(period = 10)

      dut.io.dataIn #= 10
      dut.io.write #= false
      dut.io.read #= false
      dut.clockDomain.waitRisingEdge()
      dut.clockDomain.waitRisingEdge()
      dut.clockDomain.waitRisingEdge()

      println("TEST 0: Check full and empty signals")
      println("Initial signals:")
      fifoPrintSignals()
      println("Filling:")
      var loops = 0
      while (loops <= 32) {
        fifoWriteByte(loops)
        fifoPrintSignals()
        loops += 1
      }
      println("Emptying:")
      loops = 0
      while (loops <= 32) {
        fifoReadByte()
        fifoPrintSignals()
        loops += 1
      }
      println("------------------------------------------------")

      println("TEST 1: Write then read one byte to/from fifo at a time...")
      loops = 0
      while (loops < 256) {
        // Write one byte to fifo
        println(f"Writing: ${loops}%02x")
        fifoWriteByte(loops)

        // Read one byte from fifo
        println(f"Reading: ${loops}%02x ?")
        fifoReadByte()

        // What did we get ?
        fifoPrintSignals()
        loops += 1
      }

      println("TEST 2: Write then read bytes to/from fifo 12 at a time...")
      loops = 0
      var byte = 0
      while (loops < 5) {
        // Write 12 bytes to fifo
        var bytes = 0
        while (bytes < 12) {
          println(f"Writing: ${byte}%02x")
          fifoWriteByte(byte)
          byte += 1
          bytes += 1
        }

        // Read 12 bytes from fifo
        println(f"Reading 12 bytes:")
        bytes = 0
        while (bytes < 12) {
          fifoReadByte()

          // What did we get ?
          fifoPrintSignals()
          fifoNullOp()
          bytes += 1
        }
        loops += 1
      }

      println("TEST 3: Read and write fifo simultaneously")
      while (loops < 9) {
        println("Reading and writing fifo:")
        var data = loops
        var inCnt = 0
        while (inCnt < 31) {

          // Read and Write at the same time!
          dut.io.dataIn #= data
          dut.io.write #= true
          dut.io.read #= true
          fifoTick()
          /*
          print(f"count: ${dut.count.toInt}%02x, ")
          print(f"head: ${dut.head.toInt}%02x, ")
          print(f"tail: ${dut.tail.toInt}%02x, ")
          print(f"full: ${dut.io.full.toBoolean}, ")
          print(f"empty: ${dut.io.empty.toBoolean}, ")
          print(f"dataOut: ${dut.io.dataOut.toInt}%02x, ")
          println()

          // Do nothing
          dut.io.dataIn #= data
          dut.io.write #= false
          dut.io.read #= false
          dut.clockDomain.waitRisingEdge()
*/

          fifoNullOp()
          print(f"count: ${dut.count.toInt}%02x, ")
          print(f"head: ${dut.head.toInt}%02x, ")
          print(f"tail: ${dut.tail.toInt}%02x, ")
          print(f"full: ${dut.io.full.toBoolean}, ")
          print(f"empty: ${dut.io.empty.toBoolean}, ")
          print(f"dataOut: ${dut.io.dataOut.toInt}%02x, ")
          println()

          inCnt = inCnt + 1
        }

        loops += 1
      }

      println("TEST 4: ....")
      loops = 0
      byte = 0
      while (loops < 20) {
        println(f"Writing: ${byte}%02x")
        fifoWriteByte(byte)
        byte += 1
        println(f"Writing: ${byte}%02x")
        fifoWriteByte(byte)
        byte += 1
        println(f"Writing: ${byte}%02x")
        fifoWriteByte(byte)
        byte += 1
        println(f"Writing: ${byte}%02x")
        fifoWriteByte(byte)
        byte += 1
        println(f"Writing: ${byte}%02x")
        fifoWriteByte(byte)
        byte += 1

        while(dut.io.empty.toBoolean == false) {
          fifoNullOp()
          fifoReadByte()
          if (dut.io.empty.toBoolean == false) {
            fifoPrintSignals()
          }
        }

        loops += 1
      }
    }
  }
}
