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

  val state = Reg(UInt(2 bits)) init (0)
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

  val  rxSync1 = Reg(Bool) init False
  //val  rxSync2 = Reg(Bool) init False

  rxSync1 := io.rx
  //rxSync2 := rxSync1

  val S0 = 0
  val S1 = 1
  val S2 = 2
  val S3 = 3

  // Maintain the bit timer
  when (baudClockX64Sync2.rise) {
    bitTimer := bitTimer - 1
  }

  // Rx state machine
  switch(state) {
    is(S0) {
      state := S0
      // Waiting for falling edge of start bit
      when(rxSync1.fall) {
        state := S1
        bitTimer := 31
      }
    }
    is(S1) {
      state := S1
      // Check valid start bit
      when(bitTimer === 0) {
        when(rxSync1 === False) {
          bitTimer := 63
          state := S2
        } otherwise {
          state := S0
        }
      }
    }
    is(S2) {
      state := S2
      when (baudClockX64Sync2.rise) {
        // Clock in data bits
        when (bitTimer === 0) {
          shifter(bitCount) := rxSync1
          bitCount := bitCount + 1
          when(bitCount === 7) {
            state := S3
          }
        }
      }
    }
    is(S3) {
      state := S3
      // Check stop bit
      when(bitTimer === 0) {
        when(rxSync1 === True) {
          when(!fifo.io.full) {
            fifo.io.write := True
          }
        }
        state := S0
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