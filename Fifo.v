// Generator : SpinalHDL v1.1.5    git head : 0310b2489a097f2b9de5535e02192d9ddd2764ae
// Date      : 18/11/2018, 16:11:21
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
  wire [7:0] _zz_2;
  wire  _zz_3;
  reg [4:0] head;
  reg [4:0] tail;
  reg [5:0] count;
  reg  full;
  reg  empty;
  reg [7:0] mem [0:31];
  assign _zz_2 = io_dataIn;
  assign _zz_3 = ((! full) && io_write);
  always @ (posedge clk) begin
    if(_zz_3) begin
      mem[head] <= _zz_2;
    end
  end

  assign _zz_1 = mem[tail];
  assign io_dataOut = _zz_1;
  assign io_empty = empty;
  assign io_full = full;
  always @ (posedge clk or posedge reset) begin
    if (reset) begin
      head <= (5'b00000);
      tail <= (5'b00000);
      count <= (6'b000000);
      full <= 1'b0;
      empty <= 1'b1;
    end else begin
      if((io_write && (! io_read)))begin
        if((count != (6'b100000)))begin
          head <= (head + (5'b00001));
          count <= (count + (6'b000001));
          full <= (count == (6'b011111));
          empty <= 1'b0;
        end
      end
      if(((! io_write) && io_read))begin
        if((count != (6'b000000)))begin
          tail <= (tail + (5'b00001));
          count <= (count - (6'b000001));
          empty <= (count == (6'b000001));
          full <= 1'b0;
        end
      end
      if((io_write && io_read))begin
        if(full)begin
          tail <= (tail + (5'b00001));
          count <= (count - (6'b000001));
          full <= 1'b0;
        end
        if(empty)begin
          head <= (head + (5'b00001));
          count <= (count + (6'b000001));
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

