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

// Generate the MyTopLevel's Verilog
object XororshiroPluPlusVerilog {
  def main(args: Array[String]) {
    SpinalVerilog(new Xoroshiro64PlusPlus)
    //SpinalVerilog(new Xoroshiro32PlusPlus)
  }
}

