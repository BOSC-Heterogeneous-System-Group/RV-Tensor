package SA

import chisel3.{util, _}
import chisel3.util._


class SyncFIFO(width: Int, depth: Int) extends Module {
  val io = IO(new Bundle {
    val enq = Input(Bool())
    val deq = Input(Bool())
    val enqData = Input(UInt(width.W))
    val deqData = Output(UInt(width.W))
  })

  val mem = RegInit(VecInit(Seq.fill(depth)(0.U(width.W))))

  val addr_width = log2Ceil(depth)
  val readPtr = RegInit(0.U((addr_width + 1).W)) // extra bit to indicate full or empty
  val writePtr = RegInit(0.U((addr_width + 1).W))
  val deqDateReg = RegInit(0.U(width.W))

  val isFull = RegInit(false.B)
  val isEmpty = RegInit(false.B)

  isEmpty := readPtr === writePtr
  isFull := readPtr === Cat(~writePtr(addr_width), writePtr(addr_width - 1, 0))

  when(io.enq && !isFull) {
    mem(writePtr) := io.enqData
    writePtr := writePtr + 1.U
  }

  when(io.deq && !isEmpty) {
    deqDateReg := mem(readPtr)
    readPtr := readPtr + 1.U
  }

  io.deqData := deqDateReg

}
