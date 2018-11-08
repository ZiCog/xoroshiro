package Xoroshiro

import Xoroshiro._
import spinal.core._

class AsyncReceiver extends Component {
  val io = new Bundle {
    val enable = in Bool
    val mem_valid = in Bool
    val mem_ready = out Bool
    val mem_addr = in UInt (4 bits)
    val mem_rdata = out UInt (32 bits)

    val baudClockX64 = in Bool
    val rx = in Bool
  }

  val state = Reg(UInt(2 bits)) init (0)
  val bitTimer = Reg(UInt(6 bits)) init (0)
  val bitCount = Reg(UInt(3 bits)) init (0)
  val shifter = Reg(UInt(8 bits)) init (0)
  val buffer = Reg(UInt(8 bits)) init (0)
  val bufferFull = Reg(Bool) init False

  val baudClockX64Edge = new EdgeDetect_
  baudClockX64Edge.io.trigger := io.baudClockX64
  val baudClockEdge = Bool
  baudClockEdge := baudClockX64Edge.io.Q

  val fifo = new Fifo(width = 8, depth = 32)
  fifo.io.dataIn := buffer
  fifo.io.write := False
  fifo.io.read := False


  // Rx state machine
  when (baudClockEdge) {
    bitTimer := bitTimer - 1
    switch(state) {
      is(0) {
        // Waiting for falling edge of start bit
        when(io.rx === False) {
          state := 1
          bitTimer := 31
        }
      }
      is(1) {
        // Check valid start bit
        when(bitTimer === 0) {
          when(io.rx === False) {
            bitTimer := 63
            state := 2
          } otherwise {
            state := 0
          }
        }
      }
      is(2) {
        // Clock in data bits
        when(bitTimer === 0) {
          shifter(bitCount) := io.rx
          bitCount := bitCount + 1
          when(bitCount === 7) {
            state := 3
          }
        }
      }
      is(3) {
        // Check stop bit
        when(bitTimer === 0) {
          when(io.rx === True) {
            buffer := shifter      // FIXME: Why do we need buffer here to make this work?
            bufferFull := True
            fifo.io.write := True
          }
          state := 0
        }
      }
    }
  }

  // Bus interface
  // Wire-ORed output bus
  io.mem_rdata := 0
  io.mem_ready := False

  when(io.mem_valid & io.enable) {
    io.mem_ready := True
    switch(io.mem_addr) {
      is(U"0000") {
        io.mem_rdata := buffer.resize(32)
        fifo.io.read := True
      }
      is(U"0100") {
        io.mem_rdata := bufferFull.asUInt.resize(32)
        bufferFull := False
      }
      default {
      }
    }
  }
}
