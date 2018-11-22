package Xoroshiro

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

  val state = Reg(UInt(3 bits)) init (0)
  val next = Reg(UInt(3 bits)) init (0)
  val bitTimer = Reg(UInt(6 bits)) init (0)
  val bitCount = Reg(UInt(3 bits)) init (0)
  val shifter = Reg(UInt(8 bits)) init (0)

  val fifo = new Fifo(8, 32)
  fifo.io.read := False
  fifo.io.write := False
  fifo.io.dataIn := shifter

  val  baudClockX64Sync1 = Reg(Bool) init False
  val  baudClockX64Sync2 = Reg(Bool) init False

  baudClockX64Sync1 := io.baudClockX64
  baudClockX64Sync2 := baudClockX64Sync1

  // Using next to update state ensures Quartus infers the state machine correctly.
  state := next

  val S0 = 0
  val S1 = 1
  val S2 = 2
  val S3 = 3
  val S4 = 4

  // Rx state machine
  when (baudClockX64Sync2.rise) {
    bitTimer := bitTimer - 1
    switch(state) {
      is(S0) {
        // Waiting for falling edge of start bit
        when(io.rx === False) {
          next := S1
          bitTimer := 31
        }
      }
      is(S1) {
        // Check valid start bit
        when(bitTimer === 0) {
          when(io.rx === False) {
            bitTimer := 63
            next := S2
          } otherwise {
            next := S0
          }
        }
      }
      is(S2) {
        // Clock in data bits
        when(bitTimer === 0) {
          shifter(bitCount) := io.rx
          bitCount := bitCount + 1
          when(bitCount === 7) {
            next := S3
          }
        }
      }
      is(S3) {
        // Check stop bit
        when(bitTimer === 0) {
          when(io.rx === True) {
            next := S4
          } otherwise {
            next := S0
          }
        }
      }
      is(4) {
        // Got a byte, write it to FIFO
        when (!fifo.io.full) {
          fifo.io.write := True
        }
        next := S0
      }
      default {
        next := S0
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
        rdata := fifo.io.dataOut
        fifo.io.read := True
      }
      is(U"0100") {
        rdata := (!fifo.io.empty).asUInt.resize(8)
      }
      default {
      }
    }
  }
}