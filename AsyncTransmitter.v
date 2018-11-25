// Generator : SpinalHDL v1.1.5    git head : 0310b2489a097f2b9de5535e02192d9ddd2764ae
// Date      : 25/11/2018, 16:26:38
// Component : AsyncTransmitter


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

module AsyncTransmitter (
      input   io_enable,
      input   io_mem_valid,
      output  io_mem_ready,
      input  [3:0] io_mem_addr,
      output [31:0] io_mem_rdata,
      input  [31:0] io_mem_wdata,
      input  [3:0] io_mem_wstrb,
      input   io_baudClockX64,
      output  io_tx,
      input   clk,
      input   reset);
  wire [7:0] _zz_3;
  reg  _zz_4;
  reg  _zz_5;
  wire [7:0] _zz_6;
  wire  _zz_7;
  wire  _zz_8;
  wire  _zz_9;
  wire  _zz_10;
  wire  _zz_11;
  wire [31:0] _zz_12;
  wire [0:0] _zz_13;
  reg [1:0] state;
  reg [1:0] nextState;
  reg [7:0] shifter;
  reg  tx;
  reg [2:0] bitCount;
  wire  rdy;
  reg [5:0] bitTimer;
  reg  baudClockX64Sync1;
  reg  baudClockX64Sync2;
  reg [7:0] rdata;
  reg  ready;
  wire  busCycle;
  reg  _zz_1;
  reg  _zz_2;
  assign _zz_9 = (busCycle && (! _zz_1));
  assign _zz_10 = (baudClockX64Sync2 && (! _zz_2));
  assign _zz_11 = (! _zz_8);
  assign _zz_12 = {24'd0, rdata};
  assign _zz_13 = (! _zz_8);
  Fifo fifo_1 ( 
    .io_dataIn(_zz_3),
    .io_dataOut(_zz_6),
    .io_read(_zz_4),
    .io_write(_zz_5),
    .io_full(_zz_7),
    .io_empty(_zz_8),
    .clk(clk),
    .reset(reset) 
  );
  assign rdy = 1'b0;
  always @ (*) begin
    _zz_4 = 1'b0;
    if(_zz_10)begin
      case(state)
        2'b00 : begin
          if(_zz_11)begin
            _zz_4 = 1'b1;
          end
        end
        2'b01 : begin
        end
        2'b10 : begin
        end
        default : begin
        end
      endcase
    end
  end

  always @ (*) begin
    _zz_5 = 1'b0;
    if(_zz_9)begin
      case(io_mem_addr)
        4'b0000 : begin
          if((io_mem_wstrb == (4'b0001)))begin
            _zz_5 = 1'b1;
          end
        end
        4'b0100 : begin
        end
        default : begin
        end
      endcase
    end
  end

  assign _zz_3 = io_mem_wdata[7:0];
  assign io_tx = tx;
  assign busCycle = (io_mem_valid && io_enable);
  assign io_mem_rdata = (busCycle ? _zz_12 : (32'b00000000000000000000000000000000));
  assign io_mem_ready = (busCycle ? ready : 1'b0);
  always @ (posedge clk or posedge reset) begin
    if (reset) begin
      state <= (2'b00);
      nextState <= (2'b00);
      shifter <= (8'b00000000);
      tx <= 1'b1;
      bitCount <= (3'b000);
      bitTimer <= (6'b000000);
      baudClockX64Sync1 <= 1'b0;
      baudClockX64Sync2 <= 1'b0;
      rdata <= (8'b00000000);
      ready <= 1'b0;
    end else begin
      baudClockX64Sync1 <= io_baudClockX64;
      baudClockX64Sync2 <= baudClockX64Sync1;
      ready <= busCycle;
      if(_zz_9)begin
        case(io_mem_addr)
          4'b0000 : begin
          end
          4'b0100 : begin
            rdata <= {7'd0, _zz_13};
          end
          default : begin
          end
        endcase
      end
      state <= nextState;
      if(_zz_10)begin
        bitTimer <= (bitTimer - (6'b000001));
        case(state)
          2'b00 : begin
            if(_zz_11)begin
              shifter <= _zz_6;
              bitCount <= (3'b111);
              bitTimer <= (6'b111111);
              tx <= 1'b0;
              nextState <= (2'b01);
            end
          end
          2'b01 : begin
            if((bitTimer == (6'b000000)))begin
              bitTimer <= (6'b111111);
              if((! (bitCount == (3'b000))))begin
                tx <= shifter[0];
                shifter <= (shifter >>> (1'b1));
                bitCount <= (bitCount - (3'b001));
              end else begin
                nextState <= (2'b10);
              end
            end
          end
          2'b10 : begin
            if((bitTimer == (6'b000000)))begin
              bitTimer <= (6'b111111);
              tx <= 1'b1;
              nextState <= (2'b11);
            end
          end
          default : begin
            if((bitTimer == (6'b000000)))begin
              nextState <= (2'b00);
            end
          end
        endcase
      end
    end
  end

  always @ (posedge clk) begin
    _zz_1 <= busCycle;
    _zz_2 <= baudClockX64Sync2;
  end

endmodule

