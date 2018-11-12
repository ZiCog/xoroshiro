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

  // val buffer = Reg(UInt(8 bits)) init (0)
  val mem = Mem(Bits(8 bits), wordCount = 32)
  val head = Reg(UInt (5 bits )) init (0)
  val tail = Reg(UInt (5 bits)) init (0)
  val full = Reg(Bool) init False
  val empty = Reg(Bool) init True

  val headNext = Reg(UInt (5 bits )) init (0)
  val tailNext = Reg(UInt (5 bits )) init (0)
  headNext := head + 1
  tailNext := tail + 1

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
            // Write to FIFO memory
            when (!full) {
              mem.write(head, shifter.asBits)  //!full & io.write)
              head := headNext
              full := (headNext === tail)
              empty := False
            }
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

  val memWaitState = Reg(UInt (2 bits)) init (0)
  val mem_rdata = Reg(UInt (8 bits )) init (0)

  when(io.mem_valid & io.enable) {
    switch (memWaitState) {
      is (0) {
        switch(io.mem_addr) {
          is(U"0000") {
            // Read from FIFO
            when(!empty) {
              mem_rdata := U(mem.readAsync(tail))
              tail := tailNext
              empty := tailNext === head
              full := False
            }
          }
          is(U"0100") {
            //io.mem_rdata := bufferFull.asUInt.resize(32)
            mem_rdata := (!empty).asUInt.resize(8)
          }
          default {
          }
        }
        memWaitState := 1
      }
      is (1) {
        memWaitState := 2
      }
      is (2) {
        memWaitState := 3
      }
      is (3) {
        io.mem_rdata := mem_rdata.resize(32)
        io.mem_ready := True
        memWaitState := 0
      }
    }
  }
}
