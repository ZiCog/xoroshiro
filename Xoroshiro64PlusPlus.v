// Generator : SpinalHDL v1.1.5    git head : 0310b2489a097f2b9de5535e02192d9ddd2764ae
// Date      : 11/11/2018, 19:09:15
// Component : Xoroshiro64PlusPlus


module Xoroshiro64PlusPlus (
      output [31:0] io_prng,
      input   io_next,
      input   clk,
      input   reset);
  wire [31:0] _zz_3;
  reg [31:0] s0;
  reg [31:0] s1;
  wire [31:0] s0_;
  wire [31:0] _zz_1;
  wire [31:0] s1_;
  wire [31:0] _zz_2;
  assign _zz_3 = ((s1 ^ s0) <<< 9);
  assign s0_ = (({s0[5 : 0],s0[31 : 6]} ^ (s1 ^ s0)) ^ _zz_3);
  assign _zz_1 = (s1 ^ s0);
  assign s1_ = {_zz_1[18 : 0],_zz_1[31 : 19]};
  assign _zz_2 = (s0_ + s1_);
  assign io_prng = ({_zz_2[14 : 0],_zz_2[31 : 15]} + s0_);
  always @ (posedge clk or posedge reset) begin
    if (reset) begin
      s0 <= (32'b00000000000000000000000000000001);
      s1 <= (32'b00000000000000000000000000000000);
    end else begin
      if(io_next)begin
        s0 <= s0_;
        s1 <= s1_;
      end
    end
  end

endmodule

