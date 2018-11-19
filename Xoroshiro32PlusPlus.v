// Generator : SpinalHDL v1.1.5    git head : 0310b2489a097f2b9de5535e02192d9ddd2764ae
// Date      : 19/11/2018, 10:39:08
// Component : Xoroshiro32PlusPlus


module Xoroshiro32PlusPlus (
      output [15:0] io_prng,
      input   io_next,
      input   clk,
      input   reset);
  wire [15:0] _zz_3;
  reg [15:0] s0;
  reg [15:0] s1;
  reg [15:0] p0;
  reg [15:0] p1;
  wire [15:0] s0_;
  wire [15:0] _zz_1;
  wire [15:0] s1_;
  wire [15:0] _zz_2;
  assign _zz_3 = ((s1 ^ s0) <<< 5);
  assign s0_ = (({s0[2 : 0],s0[15 : 3]} ^ (s1 ^ s0)) ^ _zz_3);
  assign _zz_1 = (s1 ^ s0);
  assign s1_ = {_zz_1[5 : 0],_zz_1[15 : 6]};
  assign _zz_2 = (s0 + s1);
  assign io_prng = (p0 + p1);
  always @ (posedge clk or posedge reset) begin
    if (reset) begin
      s0 <= (16'b0000000000000001);
      s1 <= (16'b0000000000000000);
      p0 <= (16'b0000000000000000);
      p1 <= (16'b0000000000000000);
    end else begin
      if(io_next)begin
        s0 <= s0_;
        s1 <= s1_;
        p0 <= {_zz_2[6 : 0],_zz_2[15 : 7]};
        p1 <= s0;
      end
    end
  end

endmodule

