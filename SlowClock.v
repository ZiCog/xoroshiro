// Generator : SpinalHDL v1.1.5    git head : 0310b2489a097f2b9de5535e02192d9ddd2764ae
// Date      : 18/11/2018, 16:11:21
// Component : SlowClock


module SlowClock (
      output  io_Q,
      input   clk,
      input   reset);
  reg [24:0] count;
  assign io_Q = ((count & (25'b1000000000000000000000000)) == (25'b0000000000000000000000000));
  always @ (posedge clk or posedge reset) begin
    if (reset) begin
      count <= (25'b0000000000000000000000000);
    end else begin
      count <= (count + (25'b0000000000000000000000001));
    end
  end

endmodule

