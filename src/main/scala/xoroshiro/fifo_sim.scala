package Xoroshiro

import spinal.core._
import spinal.sim._
import spinal.core.sim._

import scala.util.Random


// AsyncReceiver testbench
object FifoSim {
  def main(args: Array[String]) {

    val compiled = SimConfig.withWave.compile {
      val dut = new Fifo(8, 32)
      dut.head.simPublic()
      dut.tail.simPublic()
      dut
    }
    compiled.doSim("Fifo") { dut =>

      def fifoTick(): Unit@suspendable = {
        dut.clockDomain.waitRisingEdge()
      }

      def fifoWriteByte(byte: Int): Unit@suspendable = {
        println(f"Writing: ${byte}%02x")
        dut.io.dataIn #= byte
        dut.io.write #= true
        dut.io.read #= false
        fifoTick()
      }

      def fifoReadByte(): Unit@suspendable = {
        while (dut.io.empty.toBoolean) {
          dut.io.dataIn #= 0x66
          dut.io.read #= false
          dut.io.write #= false
          fifoTick()
          fifoPrintSignals("Empty: ")
        }
        dut.io.dataIn #= 0x66
        dut.io.read #= true
        dut.io.write #= false
        fifoTick()
        fifoPrintSignals("Reading: ")
      }

      def fifoReadWriteByte(byte: Int): Unit@suspendable = {
        dut.io.dataIn #= byte
        dut.io.read #= true
        dut.io.write #= true
        fifoTick()
        fifoPrintSignals("Read/Write: ")
      }

      def fifoNullOp(): Unit@suspendable = {
        dut.io.dataIn #= 0x66
        dut.io.read #= false
        dut.io.write #= false
        fifoTick()
        fifoPrintSignals("")
      }

      def fifoPrintSignals(title: String): Unit = {
        print(title)
        print(f"head: ${dut.head.toInt}%02x, ")
        print(f"tail: ${dut.tail.toInt}%02x, ")
        print(f"full: ${dut.io.full.toBoolean}, ")
        print(f"empty: ${dut.io.empty.toBoolean}, ")
        print(f"dataOut: ${dut.io.dataOut.toInt}%02x, ")
        println()
      }



      // Fork a process to generate the reset and the clock on the dut
      dut.clockDomain.forkStimulus(period = 10)

      println("TEST 0: Write 40 bytes and see what happens")
      var clocks = 0
      while (clocks < 40) {
        dut.io.dataIn #= clocks
        dut.io.read #= false
        dut.io.write #= true
        fifoTick()
        clocks += 1
        fifoPrintSignals(f"${clocks}%02d) ")
      }


      println("------------------------------------------------")
      println("TEST 1: Read 40 bytes and see what happens")
      clocks = 0
      while (clocks < 40) {
        dut.io.dataIn #= 0xaa
        dut.io.read #= true
        dut.io.write #= false
        fifoTick()
        clocks += 1
        fifoPrintSignals(f"${clocks}%02d) ")
      }

      println("------------------------------------------------")
      println("TEST 2: Write then read one byte to/from fifo at a time...")
      var loops = 0
      while (loops < 256) {
        fifoWriteByte(loops)
        fifoReadByte()
        loops += 1
      }

      println("------------------------------------------------")
      println("TEST 3: Write then read bytes to/from fifo 12 at a time...")
      loops = 0
      var byte = 0
      while (loops < 5) {
        // Write 12 bytes to fifo
        var bytes = 0
        while (bytes < 12) {
          fifoWriteByte(byte)
          byte += 1
          bytes += 1
        }

        // Read 12 bytes from fifo
        println(f"Reading 12 bytes:")
        bytes = 0
        while (bytes < 12) {
          fifoReadByte()
          bytes += 1
        }
        loops += 1
      }

      println("------------------------------------------------")
      println("TEST 4: Test the empty flag")
      loops = 0
      while (loops < 20) {
        byte = 0
        while (byte < 5) {
          fifoWriteByte(byte)
          byte += 1
        }

        while(dut.io.empty.toBoolean == false) {
          fifoReadByte()
        }
        loops += 1
      }

      println("------------------------------------------------")
      println("TEST 5: Read and write simultaneously, starting from an empty FIFO")
      fifoNullOp()
      fifoWriteByte(0x10)
      fifoWriteByte(0x11)
      fifoWriteByte(0x12)
      fifoReadWriteByte(0x13)
      fifoReadWriteByte(0x14)
      fifoReadWriteByte( 0x15)
      fifoReadByte()
      fifoReadByte()
      fifoReadByte()
      fifoNullOp()
    }
  }
}
