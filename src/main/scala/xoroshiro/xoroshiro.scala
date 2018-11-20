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

class  EdgeDetect_ extends Component {
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


// Generate Verilog
object XoroshiroVerilog {
  def main(args: Array[String]) {
    SpinalVerilog(new Xoroshiro128StarStar)
    SpinalVerilog(new Xoroshiro64PlusPlus)
    SpinalVerilog(new Xoroshiro32PlusPlus)
    SpinalVerilog(new SwitchDebounce)
    SpinalVerilog(new EdgeDetect_)
    SpinalVerilog(new SlowClock)
    SpinalVerilog(new AsyncReceiver).printPruned()
    SpinalVerilog(new Fifo(8, 32))
  }
}

// Generate VHDL
object XoroshiroVhdl {
  def main(args: Array[String]) {
    SpinalVhdl(new Xoroshiro128StarStar)
    SpinalVhdl(new Xoroshiro64PlusPlus)
    SpinalVhdl(new Xoroshiro32PlusPlus)
    SpinalVhdl(new SwitchDebounce)
    SpinalVhdl(new EdgeDetect_)
    SpinalVhdl(new Xoroshiro32PlusPlus)
  }
}
