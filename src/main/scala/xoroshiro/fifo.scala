package Xoroshiro

import Xoroshiro._
import spinal.core._
import spinal.lib._


class Fifo(width: Int, depth: Int) extends Component {
  val io = new Bundle {
    val dataIn = in UInt (width bits)
    val dataOut = out UInt (width bits)
    val read = in Bool
    val write = in Bool
    val full = out Bool
    val empty = out Bool
  }

  val addressWidth = (scala.math.log(depth) / scala.math.log(2)).toInt

  val mem = Mem(Bits(width bits), wordCount = depth)
  val head = Reg(UInt (addressWidth  bits )) init (0)
  val tail = Reg(UInt (addressWidth  bits)) init (0)
  val count = Reg(UInt (addressWidth + 1  bits)) init (0)
  val full = Reg(Bool) init False
  val empty = Reg(Bool) init True

  mem.write(head, io.dataIn.asBits, !full & io.write)
  io.dataOut := U(mem.readAsync(tail))

  when (io.write && !io.read) {
    when (count =/= depth) {
      head := head + 1
      count := count + 1
      full := (count === depth - 1)
      empty := False
    }
  }

  when (!io.write && io.read) {
    when (count =/= 0) {
      tail := tail + 1
      count := count - 1
      empty := (count === 1)
      full := False
    }
  }

  when (io.write & io.read) {
    when (full) {
      tail := tail + 1
      count := count - 1
      full := False

    }
    when (empty) {
      head := head + 1
      count := count + 1
      empty := False
    }
    when (!full & !empty) {
      tail := tail + 1
      head := head + 1
    }
  }

  io.empty := empty
  io.full := full
}


