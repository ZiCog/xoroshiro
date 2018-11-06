// Generator : SpinalHDL v1.1.5    git head : 0310b2489a097f2b9de5535e02192d9ddd2764ae
// Date      : 06/11/2018, 13:03:02
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
  reg [7:0] _zz_2;
  wire [7:0] _zz_3;
  wire  _zz_4;
  reg [4:0] head;
  reg [4:0] tail;
  reg  empty;
  reg  full;
  reg [4:0] count;
  reg [7:0] dataOut;
  wire  _zz_1;
  reg [7:0] mem [0:31];
  assign _zz_3 = io_dataIn;
  assign _zz_4 = 1'b1;
  always @ (posedge clk) begin
    if(_zz_4) begin
      mem[head] <= _zz_3;
    end
  end

  always @ (posedge clk) begin
    if(_zz_1) begin
      _zz_2 <= mem[tail];
    end
  end

  assign io_dataOut = dataOut;
  assign io_empty = empty;
  assign io_full = full;
  assign _zz_1 = 1'b1;
  always @ (posedge clk or posedge reset) begin
    if (reset) begin
      head <= (5'b00000);
      tail <= (5'b00000);
      empty <= 1'b1;
      full <= 1'b0;
      count <= (5'b00000);
      dataOut <= (8'b00000000);
    end else begin
      if((io_write && (! io_read)))begin
        if((! (count == (5'b11111))))begin
          count <= (count + (5'b00001));
          head <= (head + (5'b00001));
          empty <= 1'b0;
        end else begin
          full <= 1'b1;
        end
      end
      if(((! io_write) && io_read))begin
        if((! (count == (5'b00000))))begin
          dataOut <= _zz_2;
          count <= (count - (5'b00001));
          tail <= (tail - (5'b00001));
        end else begin
          empty <= 1'b1;
          dataOut <= (8'b00000000);
        end
      end
    end
  end

endmodule

