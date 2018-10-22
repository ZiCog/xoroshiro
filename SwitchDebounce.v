// Generator : SpinalHDL v1.1.5    git head : 0310b2489a097f2b9de5535e02192d9ddd2764ae
// Date      : 22/10/2018, 22:08:21
// Component : SwitchDebounce


module SwitchDebounce (
      input   io_D,
      output  io_Q,
      input   clk,
      input   reset);
  reg [15:0] timer;
  reg  q_;
  assign io_Q = q_;
  always @ (posedge clk or posedge reset) begin
    if (reset) begin
      timer <= (16'b0000000000000000);
      q_ <= 1'b0;
    end else begin
      timer <= timer;
      q_ <= q_;
      if(io_D)begin
        if((! (timer == (16'b1111111111111111))))begin
          timer <= (timer + (16'b0000000000000001));
        end else begin
          q_ <= 1'b1;
        end
      end else begin
        if((! (timer == (16'b0000000000000000))))begin
          timer <= (timer - (16'b0000000000000001));
        end else begin
          q_ <= 1'b0;
        end
      end
    end
  end

endmodule

