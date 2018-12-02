// Generator : SpinalHDL v1.1.5    git head : 0310b2489a097f2b9de5535e02192d9ddd2764ae
// Date      : 02/12/2018, 10:37:58
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
  wire [7:0] _zz_5;
  wire [4:0] _zz_6;
  wire [4:0] _zz_7;
  wire [7:0] _zz_8;
  wire  _zz_9;
  reg [4:0] head;
  reg [4:0] tail;
  reg  full;
  reg  empty;
  reg  _zz_1;
  reg  _zz_2;
  reg  _zz_3;
  reg  _zz_4;
  reg [7:0] mem [0:31];
  assign _zz_6 = (head + (5'b00001));
  assign _zz_7 = (tail + (5'b00001));
  assign _zz_8 = io_dataIn;
  assign _zz_9 = ((! full) && io_write);
  always @ (posedge clk) begin
    if(_zz_9) begin
      mem[head] <= _zz_8;
    end
  end

  assign _zz_5 = mem[tail];
  assign io_dataOut = _zz_5;
  assign io_empty = empty;
  assign io_full = full;
  always @ (posedge clk or posedge reset) begin
    if (reset) begin
      head <= (5'b00000);
      tail <= (5'b00000);
      full <= 1'b0;
      empty <= 1'b1;
    end else begin
      if(((io_write && (! _zz_1)) && (! io_read)))begin
        if((! full))begin
          head <= (head + (5'b00001));
          full <= (_zz_6 == tail);
          empty <= 1'b0;
        end
      end
      if(((! io_write) && (io_read && (! _zz_2))))begin
        if((! empty))begin
          tail <= (tail + (5'b00001));
          empty <= (_zz_7 == head);
          full <= 1'b0;
        end
      end
      if(((io_write && (! _zz_3)) && (io_read && (! _zz_4))))begin
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

  always @ (posedge clk) begin
    _zz_1 <= io_write;
    _zz_2 <= io_read;
    _zz_3 <= io_write;
    _zz_4 <= io_read;
  end

endmodule

