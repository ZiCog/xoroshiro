package XoroshiroPlusPlus

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

  // The xoroshiro32++ magic numbers
  val a = 13
  val b = 5
  val c = 10
  val d = 9

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

class  Xoroshiro64PlusPlus extends Component {
  val io = new Bundle {
    val prng = out UInt(32 bits)
    val next = in Bool
  }

  // The PRNG state
  val s0 = Reg(UInt(32 bits)) init(1)
  val s1 = Reg(UInt(32 bits)) init(0)

  // The xoroshiro64++ magic numbers
  val a = 26
  val b = 9
  val c = 13
  val d = 17

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

class  SwitchDebounce extends Component {
  val io = new Bundle {
    val D = in Bool
    val Q = out Bool
  }

  val timer = Reg(UInt(16 bits)) init 0
  val q_ = Reg(Bool) init False
  timer := timer
  q_ := q_

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

class  Monostable extends Component {
  val io = new Bundle {
    val trigger = in Bool
    val Q = out Bool
  }
  val oldTrigger = Reg(Bool) init (False)

  // Detect a rising edge on the trigger input
  oldTrigger := io.trigger

  io.Q := io.trigger && !oldTrigger
}

// Generate Verilog
object XororshiroPluPlusVerilog {
  def main(args: Array[String]) {
    SpinalVerilog(new Xoroshiro64PlusPlus)
    SpinalVerilog(new Xoroshiro32PlusPlus)
    SpinalVerilog(new SwitchDebounce)
    SpinalVerilog(new Monostable)
    SpinalVerilog(new Xoroshiro32PlusPlus)
  }
}

// Generate VHDL
object XororshiroPluPlusVhdl {
  def main(args: Array[String]) {
    SpinalVhdl(new Xoroshiro64PlusPlus)
    SpinalVhdl(new Xoroshiro32PlusPlus)
    SpinalVhdl(new SwitchDebounce)
    SpinalVhdl(new Monostable)
    SpinalVhdl(new Xoroshiro32PlusPlus)
  }
}
