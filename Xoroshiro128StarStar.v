// Generator : SpinalHDL v1.1.5    git head : 0310b2489a097f2b9de5535e02192d9ddd2764ae
// Date      : 01/12/2018, 09:11:40
// Component : Xoroshiro128StarStar


module Xoroshiro128StarStar (
      output [31:0] io_prngHigh,
      output [31:0] io_prngLow,
      input   io_next,
      input   clk,
      input   reset);
  wire [63:0] _zz_3;
  wire [63:0] _zz_4;
  wire [63:0] _zz_5;
  reg [63:0] s0;
  reg [63:0] s1;
  reg [63:0] p1;
  wire [63:0] s0_;
  wire [63:0] _zz_1;
  wire [63:0] s1_;
  wire [63:0] _zz_2;
  wire [63:0] prng;
  assign _zz_3 = ((s1 ^ s0) <<< 16);
  assign _zz_4 = (s0 <<< 2);
  assign _zz_5 = (p1 <<< 3);
  assign s0_ = (({s0[39 : 0],s0[63 : 40]} ^ (s1 ^ s0)) ^ _zz_3);
  assign _zz_1 = (s1 ^ s0);
  assign s1_ = {_zz_1[26 : 0],_zz_1[63 : 27]};
  assign _zz_2 = (_zz_4 + s0);
  assign prng = (_zz_5 + p1);
  assign io_prngHigh = prng[63 : 32];
  assign io_prngLow = prng[31 : 0];
  always @ (posedge clk or posedge reset) begin
    if (reset) begin
      s0 <= (64'b0000000000000000000000000000000000000000000000000000000000000001);
      s1 <= (64'b0000000000000000000000000000000000000000000000000000000000000000);
      p1 <= (64'b0000000000000000000000000000000000000000000000000000000000000000);
    end else begin
      if(io_next)begin
        s0 <= s0_;
        s1 <= s1_;
        p1 <= {_zz_2[56 : 0],_zz_2[63 : 57]};
      end
    end
  end

endmodule

