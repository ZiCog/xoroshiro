package Xoroshiro

import Xoroshiro._
import spinal.core._
import spinal.lib._


class Fifo(width: Int, depth: Int) extends Component {

  val addressWidth = (scala.math.log(depth) / scala.math.log(2)).toInt

  val io = new Bundle {
    val dataIn = in UInt (width bits)
    val dataOut = out UInt (width bits)
    val read = in Bool
    val write = in Bool
    val full = out Bool
    val empty = out Bool
  }

  val mem = Mem(Bits(width bits), wordCount = depth)
  val head = Reg(UInt (addressWidth  bits )) init (0)
  val tail = Reg(UInt (addressWidth  bits)) init (0)
  val full = Reg(Bool) init False
  val empty = Reg(Bool) init True

  mem.write(head, io.dataIn.asBits, !full & io.write)
  io.dataOut := U(mem.readAsync(tail))

  when (io.write && !io.read) {
    when (!full) {
      head := head + 1
      full := ((head + 1) === tail)
      empty := False
    }
  }

  when (!io.write && io.read) {
    when (!empty) {
      tail := tail + 1
      empty := (tail + 1  === head)
      full := False
    }
  }

  when (io.write & io.read) {
    when (full) {
      tail := tail + 1
      full := False
    }
    when (empty) {
      head := head + 1
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


