package Xoroshiro

import spinal.core._
import spinal.sim._
import spinal.core.sim._

import scala.util.Random


// AsyncReceiver testbench
object FifoSim {
  def main(args: Array[String]) {

    val compiled = SimConfig.withWave.compile {
      val dut = new Fifo(8, 64)
      dut.head.simPublic()
      dut.tail.simPublic()
      dut
    }
    compiled.doSim("Fifo") { dut =>

      def fifoWriteByte(byte: Int): Unit@suspendable = {
        println(f"Writing: ${byte}%02x")
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
        if (dut.io.empty.toBoolean == false) {
          print(f"dataOut: ${dut.io.dataOut.toInt}%02x, ")
        }
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


      println("------------------------------------------------")
      println("TEST 1: Write then read one byte to/from fifo at a time...")
      var loops = 0
      while (loops < 256) {
        // Write one byte to fifo
        fifoWriteByte(loops)

        // Read one byte from fifo
        fifoReadByte()

        loops += 1
      }

      println("------------------------------------------------")
      println("TEST 2: Write then read bytes to/from fifo 12 at a time...")
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
      println("TEST 3: Test the empty flag")
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
      println("TEST 4: Read and write simultaneously, starting from not empty FIFO")
      dut.clockDomain.assertReset();
      dut.clockDomain.disassertReset();
      fifoWriteByte(0x10)
      fifoWriteByte(0x11)
      fifoWriteByte(0x12)
      fifoReadWriteByte(0x13)
      fifoReadWriteByte(0x14)
      fifoReadWriteByte( 0x15)
      fifoReadByte()
      fifoReadByte()
      fifoReadByte()

    }
  }
}
