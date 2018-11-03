package Xoroshiro

import spinal.core._
import spinal.lib._

import scala.util.Random

class  Xoroshiro32PlusPlus extends Component {
  val io = new Bundle {
    val prng = out UInt(16 bits)
    val next = in Bool
  }

  // The PRNG state
  val s0 = Reg(UInt(16 bits)) init(1)
  val s1 = Reg(UInt(16 bits)) init(0)
  val p0 = Reg(UInt(16 bits)) init(0)
  val p1 = Reg(UInt(16 bits)) init(0)

  // The xoroshiro32++ magic numbers
  def a = 13
  def b = 5
  def c = 10
  def d = 9

  // Calculate the next PRNG state.
  val s0_ = s0.rotateLeft(a) ^ (s1 ^ s0) ^ ((s1 ^ s0) |<< b)
  val s1_ = (s1 ^ s0).rotateLeft(c)

  when(io.next) {
    // Update the PRNG state
    s0 := s0_
    s1 := s1_
    p0 := (s0 + s1).rotateLeft(d)
    p1 := s0
  }

  // Deliver the "++" scrambled output
  io.prng := p0 + p1
}

class  Xoroshiro64PlusPlus extends Component {
  val io = new Bundle {
    val prng = out UInt(32 bits)
    val next = in Bool
  }

  // The PRNG state
  val s0 = Reg(UInt(32 bits)) init(1)
  val s1 = Reg(UInt(32 bits)) init(0)

  // The xoroshiro64++ magic numbers
  def a = 26
  def b = 9
  def c = 13
  def d = 17

  // Calculate the next PRNG state.
  val s0_ = s0.rotateLeft(a) ^ (s1 ^ s0) ^ ((s1 ^ s0) |<< b)
  val s1_ = (s1 ^ s0).rotateLeft(c)

  when(io.next) {
    // Update the PRNG state
    s0 := s0_
    s1 := s1_
  }

  // Deliver the "++" scrambled output
  io.prng := (s0_ + s1_).rotateLeft(d) + s0_
}

class  Xoroshiro128StarStar extends Component {
  val io = new Bundle {
    val prngHigh = out UInt(32 bits)
    val prngLow = out UInt(32 bits)
    val next = in Bool
  }

  // The PRNG state
  val s0 = Reg(UInt(64 bits)) init(1)
  val s1 = Reg(UInt(64 bits)) init(0)
  val p1 = Reg(UInt(64 bits)) init(0)

  // The xoroshiro128** magic numbers
  def a = 24
  def b = 16
  def c = 37
  def s = 5
  def r = 7
  def t = 9

  // Calculate the next PRNG state.
  val s0_ = s0.rotateLeft(a) ^ (s1 ^ s0) ^ ((s1 ^ s0) |<< b)
  val s1_ = (s1 ^ s0).rotateLeft(c)

  when(io.next) {
    // Update the PRNG state
    s0 := s0_
    s1 := s1_
    p1 := ((s0 |<< 2) + s0).rotateLeft(r)
  }

  // Deliver the "**" scrambled output: "rotl(s0 * S, R) * T"
  val prng = (p1 |<< 3) + p1
  io.prngHigh := prng(63 downto 32)
  io.prngLow := prng(31 downto 0)
}

class  SwitchDebounce extends Component {
  val io = new Bundle {
    val D = in Bool
    val Q = out Bool
  }

  val timer = Reg(UInt(16 bits)) init 0
  val q_ = Reg(Bool) init False
  
  when (io.D) {
    when (!(timer === 0xffff)) {
      timer := timer + 1
    } otherwise {
      q_ := True
    }
  } otherwise {
    when (!(timer === 0x0000)) {
      timer := timer - 1
    } otherwise {
      q_ := False
    }
  }

  io.Q := q_
}

class  EdgeDetect extends Component {
  val io = new Bundle {
    val trigger = in Bool
    val Q = out Bool
  }
  val oldTrigger = Reg(Bool) init (False)

  // Detect a rising edge on the trigger input
  oldTrigger := io.trigger

  io.Q := io.trigger && !oldTrigger
}

class SlowClock extends Component {
  val io = new Bundle {
    val Q = out Bool
  }
  val count = Reg(UInt(25 bits)) init 0

  count := count + 1

  io.Q := ((count &  0x1000000) === 0)
}

class AsyncReceiver extends Component {
  val io = new Bundle {
    val enable = in Bool
    val mem_valid = in Bool
    val mem_ready = out Bool
    val mem_addr = in UInt (4 bits)
    val mem_rdata = out UInt (32 bits)

    val baudClockX16 = in Bool
    val rx = in Bool
  }

  val state = Reg(UInt(2 bits)) init (0)
  val bitTimeOut = Reg(UInt(5 bits)) init (0)
  val bitCount = Reg(UInt(3 bits)) init (0)
  val shifter = Reg(UInt(8 bits)) init (0)
  val buffer = Reg(UInt(8 bits)) init (0)
  val bufferFull = Reg(Bool) init (False)

  val baudClockX16Edge = new EdgeDetect
  baudClockX16Edge.io.trigger := io.baudClockX16
  val baudClockEdge = Bool
  baudClockEdge := baudClockX16Edge.io.Q

  // Keep the bit timer counting down
  when(baudClockEdge) {
    when(!(bitTimeOut === 0)) {
      bitTimeOut := bitTimeOut - 1
    }
  }

  switch(state) {
    is(0) {
      // Waiting for falling edge of start bit
      when(io.rx === False) {
        state := 1
        bitTimeOut := 8
      }
    }
    is(1) {
      // Check valid start bit
      when(bitTimeOut === 0) {
        when(io.rx === False) {
          bitTimeOut := 16
          state := 2
        } otherwise {
          state := 0
        }
      }
    }
    is(2) {
      // Clock in data bits
      when(bitTimeOut === 0) {
        shifter(bitCount) := io.rx
        when(bitCount === 7) {
          state := 3
        }
        bitCount := bitCount + 1
        bitTimeOut := 16
      }
    }
    is(3) {
      // Check stop bit
      when(bitTimeOut === 0) {
        when(io.rx === True) {
          buffer := shifter
          bufferFull := True
        }
        state := 0
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
        bufferFull := False
      }
      is(U"0100") {
        io.mem_rdata := bufferFull.asUInt.resize(32)
      }
      default {
      }
    }
  }
}

// Generate Verilog
object XoroshiroVerilog {
  def main(args: Array[String]) {
    SpinalVerilog(new Xoroshiro128StarStar)
    SpinalVerilog(new Xoroshiro64PlusPlus)
    SpinalVerilog(new Xoroshiro32PlusPlus)
    SpinalVerilog(new SwitchDebounce)
    SpinalVerilog(new EdgeDetect)
    SpinalVerilog(new SlowClock)
    SpinalVerilog(new AsyncReceiver)
  }
}

// Generate VHDL
object XoroshiroVhdl {
  def main(args: Array[String]) {
    SpinalVhdl(new Xoroshiro128StarStar)
    SpinalVhdl(new Xoroshiro64PlusPlus)
    SpinalVhdl(new Xoroshiro32PlusPlus)
    SpinalVhdl(new SwitchDebounce)
    SpinalVhdl(new EdgeDetect)
    SpinalVhdl(new Xoroshiro32PlusPlus)
  }
}
