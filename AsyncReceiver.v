// Generator : SpinalHDL v1.1.5    git head : 0310b2489a097f2b9de5535e02192d9ddd2764ae
// Date      : 12/11/2018, 13:31:54
// Component : AsyncReceiver


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

module AsyncReceiver (
      input   io_enable,
      input   io_mem_valid,
      output reg  io_mem_ready,
      input  [3:0] io_mem_addr,
      output reg [31:0] io_mem_rdata,
      input   io_baudClockX64,
      input   io_rx,
      input   clk,
      input   reset);
  wire [7:0] _zz_2;
  wire  _zz_3;
  wire  _zz_4;
  wire  _zz_5;
  wire  _zz_6;
  wire  _zz_7;
  wire [0:0] _zz_8;
  wire [7:0] _zz_9;
  reg  _zz_1;
  reg [1:0] state;
  reg [5:0] bitTimer;
  reg [2:0] bitCount;
  reg [7:0] shifter;
  reg [4:0] head;
  reg [4:0] tail;
  reg  full;
  reg  empty;
  reg [4:0] headNext;
  reg [4:0] tailNext;
  wire  baudClockEdge;
  reg [1:0] memWaitState;
  reg [7:0] mem_rdata;
  reg [7:0] mem [0:31];
  assign _zz_4 = (io_rx == 1'b1);
  assign _zz_5 = (bitTimer == (6'b000000));
  assign _zz_6 = (! full);
  assign _zz_7 = (io_mem_valid && io_enable);
  assign _zz_8 = (! empty);
  assign _zz_9 = shifter;
  always @ (posedge clk) begin
    if(_zz_1) begin
      mem[head] <= _zz_9;
    end
  end

  assign _zz_2 = mem[tail];
  EdgeDetect_ baudClockX64Edge ( 
    .io_trigger(io_baudClockX64),
    .io_Q(_zz_3),
    .clk(clk),
    .reset(reset) 
  );
  always @ (*) begin
    _zz_1 = 1'b0;
    if(baudClockEdge)begin
      case(state)
        2'b00 : begin
        end
        2'b01 : begin
        end
        2'b10 : begin
        end
        default : begin
          if(_zz_5)begin
            if(_zz_4)begin
              if(_zz_6)begin
                _zz_1 = 1'b1;
              end
            end
          end
        end
      endcase
    end
  end

  assign baudClockEdge = _zz_3;
  always @ (*) begin
    io_mem_rdata = (32'b00000000000000000000000000000000);
    io_mem_ready = 1'b0;
    if(_zz_7)begin
      case(memWaitState)
        2'b00 : begin
        end
        2'b01 : begin
        end
        2'b10 : begin
        end
        default : begin
          io_mem_rdata = {24'd0, mem_rdata};
          io_mem_ready = 1'b1;
        end
      endcase
    end
  end

  always @ (posedge clk or posedge reset) begin
    if (reset) begin
      state <= (2'b00);
      bitTimer <= (6'b000000);
      bitCount <= (3'b000);
      shifter <= (8'b00000000);
      head <= (5'b00000);
      tail <= (5'b00000);
      full <= 1'b0;
      empty <= 1'b1;
      headNext <= (5'b00000);
      tailNext <= (5'b00000);
      memWaitState <= (2'b00);
      mem_rdata <= (8'b00000000);
    end else begin
      headNext <= (head + (5'b00001));
      tailNext <= (tail + (5'b00001));
      if(baudClockEdge)begin
        bitTimer <= (bitTimer - (6'b000001));
        case(state)
          2'b00 : begin
            if((io_rx == 1'b0))begin
              state <= (2'b01);
              bitTimer <= (6'b011111);
            end
          end
          2'b01 : begin
            if((bitTimer == (6'b000000)))begin
              if((io_rx == 1'b0))begin
                bitTimer <= (6'b111111);
                state <= (2'b10);
              end else begin
                state <= (2'b00);
              end
            end
          end
          2'b10 : begin
            if((bitTimer == (6'b000000)))begin
              shifter[bitCount] <= io_rx;
              bitCount <= (bitCount + (3'b001));
              if((bitCount == (3'b111)))begin
                state <= (2'b11);
              end
            end
          end
          default : begin
            if(_zz_5)begin
              if(_zz_4)begin
                if(_zz_6)begin
                  head <= headNext;
                  full <= (headNext == tail);
                  empty <= 1'b0;
                end
              end
              state <= (2'b00);
            end
          end
        endcase
      end
      if(_zz_7)begin
        case(memWaitState)
          2'b00 : begin
            case(io_mem_addr)
              4'b0000 : begin
                if((! empty))begin
                  mem_rdata <= _zz_2;
                  tail <= tailNext;
                  empty <= (tailNext == head);
                  full <= 1'b0;
                end
              end
              4'b0100 : begin
                mem_rdata <= {7'd0, _zz_8};
              end
              default : begin
              end
            endcase
            memWaitState <= (2'b01);
          end
          2'b01 : begin
            memWaitState <= (2'b10);
          end
          2'b10 : begin
            memWaitState <= (2'b11);
          end
          default : begin
            memWaitState <= (2'b00);
          end
        endcase
      end
    end
  end

endmodule

