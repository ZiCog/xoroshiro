package Xoroshiro

import spinal.core._

class AsyncTransmitter extends Component {
  val io = new Bundle {
    val enable = in Bool
    val mem_valid = in Bool
    val mem_ready = out Bool
    val mem_addr = in UInt (4 bits)
    val mem_rdata = out UInt (32 bits)
    val mem_wdata = in UInt (32 bits)
    val mem_wstrb = in UInt (4 bits)

    val baudClockX64 = in Bool
    val tx = out Bool
  }

  val state = Reg(UInt (2 bits )) init 0
  val nextState = Reg(UInt (2 bits )) init 0
  val shifter = Reg(UInt (8 bits )) init 0
  val tx = Reg(Bool) init True
  val bitCount = Reg(UInt (3 bits )) init 0
  val rdy = Reg(Bool) init False
  val bitTimer = Reg(UInt(6 bits)) init (0)

  val fifo = new Fifo(8, 32)
  fifo.io.read := False
  fifo.io.write := False
  fifo.io.dataIn := io.mem_wdata.resize(8)

  val  baudClockX64Sync1 = Reg(Bool) init False
  val  baudClockX64Sync2 = Reg(Bool) init False

  baudClockX64Sync1 := io.baudClockX64
  baudClockX64Sync2 := baudClockX64Sync1

  io.tx := tx

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
        when (io.mem_wstrb(0)) {
          fifo.io.write := True
        }
      }
      is(U"0100") {
        rdata := (!fifo.io.empty).asUInt.resize(8)
      }
      default {
      }
    }
  }

  // TX State machine
  val IDLE = 0
  val TX_BITS = 1
  val STOP_BIT = 2
  val DONE = 3

  // Using nextState to update state ensures Quartus infers the state machine correctly.
  state := nextState

  when (baudClockX64Sync2.rise()) {
    bitTimer := bitTimer - 1
    switch (state) {
      is (IDLE) {
        when (!fifo.io.empty) {
          shifter := fifo.io.dataOut
          fifo.io.read := True
          bitCount := 7
          bitTimer := 63
          tx := False
          nextState := TX_BITS
        }
      }
      is (TX_BITS) {
        when (bitTimer === 0) {
          bitTimer := 63
          when(!(bitCount === 0)) {
            tx := shifter(0)
            shifter := shifter >> U(1)
            bitCount := bitCount - 1
          } otherwise {
            nextState := STOP_BIT
          }
        }
      }
      is (STOP_BIT) {
        when (bitTimer === 0) {
          bitTimer := 63
          tx := True
          nextState := DONE
        }
      }
      is (DONE) {
        when (bitTimer === 0) {
          nextState := IDLE
        }
      }
    }
  }
}


