// Generator : SpinalHDL v1.1.5    git head : 0310b2489a097f2b9de5535e02192d9ddd2764ae
// Date      : 13/11/2018, 18:51:12
// Component : Fifo


module Fifo (
      input  [7:0] io_dataIn,
      output [7:0] io_dataOut,
      input   io_read,
      input   io_write,
      output  io_full,
      output  io_empty,
      input   clk,
      input   reset);
  wire [7:0] _zz_1;
  wire [4:0] _zz_2;
  wire [4:0] _zz_3;
  wire [7:0] _zz_4;
  wire  _zz_5;
  reg [7:0] dataOut;
  reg [4:0] head;
  reg [4:0] tail;
  reg  full;
  reg  empty;
  reg [7:0] mem [0:31];
  assign _zz_2 = (head + (5'b00001));
  assign _zz_3 = (tail + (5'b00001));
  assign _zz_4 = io_dataIn;
  assign _zz_5 = ((! full) && io_write);
  always @ (posedge clk) begin
    if(_zz_5) begin
      mem[head] <= _zz_4;
    end
  end

  assign _zz_1 = mem[tail];
  assign io_dataOut = dataOut;
  assign io_empty = empty;
  assign io_full = full;
  always @ (posedge clk or posedge reset) begin
    if (reset) begin
      dataOut <= (8'b00000000);
      head <= (5'b00000);
      tail <= (5'b00000);
      full <= 1'b0;
      empty <= 1'b1;
    end else begin
      dataOut <= _zz_1;
      if((io_write && (! io_read)))begin
        if((! full))begin
          head <= (head + (5'b00001));
          full <= (_zz_2 == tail);
          empty <= 1'b0;
        end
      end
      if(((! io_write) && io_read))begin
        if((! empty))begin
          tail <= (tail + (5'b00001));
          empty <= (_zz_3 == head);
          full <= 1'b0;
        end
      end
      if((io_write && io_read))begin
        if(full)begin
          tail <= (tail + (5'b00001));
          full <= 1'b0;
        end
        if(empty)begin
          head <= (head + (5'b00001));
          empty <= 1'b0;
        end
        if(((! full) && (! empty)))begin
          tail <= (tail + (5'b00001));
          head <= (head + (5'b00001));
        end
      end
    end
  end

endmodule

