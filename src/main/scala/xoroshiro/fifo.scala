package Xoroshiro

import Xoroshiro._
import spinal.core._
import spinal.lib._

class Fifo extends Component {
  val io = new Bundle {
    val dataIn = in UInt (8 bits)
    val dataOut = out UInt (8 bits)
    val read = in Bool
    val write = in Bool
    val full = out Bool
    val empty = out Bool
  }

  val mem = Mem(Bits(8 bits), wordCount = 32)
  val head = Reg(UInt (5 bits)) init (0)
  val tail = Reg(UInt (5 bits)) init (0)
  val count = Reg(UInt (5 bits)) init (0)

  mem.write(head, io.dataIn.asBits, (!(count === 31)) & io.write)
  io.dataOut := U(mem.readAsync(tail))

  when (io.write && !io.read) {
    when (!(count === 31)) {
      count := count + 1
      head := head + 1
    }
  }

  when (!io.write && io.read) {
    when (!(count === 0)) {
      count := count - 1
      tail := tail + 1
    }
  }

  when (io.write && io.read) {
    head := head + 1
    tail := tail + 1
  }

  io.empty := (count === 0)
  io.full := (count === 31)
}


