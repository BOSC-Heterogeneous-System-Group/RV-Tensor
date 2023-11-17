package miniTPU.SystolicArray

import chisel3._


class PE_Control extends Bundle {
  val done = Bool()
  // TODO add bias preload control signals
}

class PE(val IN_WIDTH: Int, val C_WIDTH: Int) extends Module {
  val io = IO(new Bundle {
    val in_control = Input(new PE_Control)
    val in_a = Input(UInt(IN_WIDTH.W))
    val in_b = Input(UInt(IN_WIDTH.W))
    val in_c = Input(UInt(C_WIDTH.W))

    val out_control = Output(new PE_Control)
    val out_a = Output(UInt(IN_WIDTH.W))
    val out_b = Output(UInt(IN_WIDTH.W))
    val out_c = Output(UInt(C_WIDTH.W))
  })

  val a_reg = RegInit(0.U(IN_WIDTH.W))
  val b_reg = RegInit(0.U(IN_WIDTH.W))
  val c_reg = RegInit(0.U(C_WIDTH.W))

  val mac = Module(new MacUnit(IN_WIDTH, C_WIDTH))
  mac.io.in_a := io.in_a
  mac.io.in_b := io.in_b
  mac.io.in_c := c_reg

  a_reg := io.in_a
  b_reg := io.in_b

  c_reg := Mux(io.in_control.done === true.B, io.in_c, mac.io.out_c)

  io.out_a := a_reg
  io.out_b := b_reg
  io.out_c := c_reg

  io.out_control := io.in_control
}