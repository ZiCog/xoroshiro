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

  // Rx state machine
  when (io.baudClockX64.rise) {
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
            buffer := shifter
            bufferFull := True
          }
          state := 0
        }
      }
    }
  }

  // Bus interface
  // Wire-ORed output bus
  val rdata = Reg(UInt(8 bits)) init (0)
  val ready = Reg(Bool) init (False)
  val busCycle = io.mem_valid & io.enable
  ready := busCycle
  io.mem_rdata := Mux(busCycle, rdata.resize(32), U(0))
  io.mem_ready := Mux(busCycle, ready, False)

  when(busCycle.rise) {
    switch(io.mem_addr) {
      is(U"0000") {
        rdata := buffer
        bufferFull := False
      }
      is(U"0100") {
        rdata := bufferFull.asUInt.resize(8)
      }
      default {
      }
    }
  }
}