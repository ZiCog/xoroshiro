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
  val bufferFull = Reg(Bool) init (False)

  val fifo = new Fifo(width = 8, depth = 32)
  fifo.io.dataIn := buffer
  fifo.io.write := False

  val baudClockX64Edge = new EdgeDetect_
  baudClockX64Edge.io.trigger := io.baudClockX64
  val baudClockEdge = Bool
  baudClockEdge := baudClockX64Edge.io.Q

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
            when (!bufferFull) {
              buffer := shifter
              bufferFull := True
            }
          }
          state := 0
        }
      }
    }
  }

  when (bufferFull) {
    fifo.io.write := True
    bufferFull := False
  }


  // Bus interface
  // Wire-ORed output bus
  io.mem_rdata := 0
  io.mem_ready := False

  val waitState = Reg(UInt(2 bits)) init (0)

  fifo.io.read := False

  when(io.mem_valid & io.enable) {
    switch(io.mem_addr) {
      is(U"0000") {
        switch (waitState) {
          is (0) {
            // Read from FIFO
            when(!fifo.io.empty) {
              io.mem_rdata := fifo.io.dataOut.resize(32)
              io.mem_ready := True
              waitState := 1
            } otherwise {
              waitState := 2
            }
          }
          is (1) {
            fifo.io.read := True
            waitState := 2
          }
          is (2) {
            waitState := 3
          }
          is (3) {
            waitState := 0
          }
        }
      }
      is(U"0100") {
        io.mem_rdata := (!fifo.io.empty).asUInt.resize(32)
        io.mem_ready := True
      }
    }
  }
}
