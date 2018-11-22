// Generator : SpinalHDL v1.1.5    git head : 0310b2489a097f2b9de5535e02192d9ddd2764ae
// Date      : 22/11/2018, 11:10:51
// Component : EdgeDetect_


module EdgeDetect_ (
      input   io_trigger,
      output  io_Q,
      input   clk,
      input   reset);
  reg  oldTrigger;
  assign io_Q = (io_trigger && (! oldTrigger));
  always @ (posedge clk or posedge reset) begin
    if (reset) begin
      oldTrigger <= 1'b0;
    end else begin
      oldTrigger <= io_trigger;
    end
  end

endmodule

