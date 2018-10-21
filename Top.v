
// What is this for ?!
// `include "inc/timescale.vh"

module Top (input clock_50, input[1:0] key, output[7:0] led, output P0, output P2);


    // Remove reset after 100 clocks
    reg reset = 1;
    reg [7:0] resetCount = 0;
    always @(posedge clock_50)
    begin
        resetCount <= resetCount + 1;
        if (resetCount == 100) reset <= 0;
    end

	 wire [15:0] prng;
	 assign led[7:0] = prng[15:8];
	 
    wire nextKey = !key[0];
	 wire debouncedNextKey;

	 
	 wire next;
	 	 

    wire sc;	 
    wire scdb;	 
    SlowClock slowClock (
        .clk(clock_50),
        .reset(reset),
        .io_Q(sc)
	 );
	 assign P0 = sc;
	 assign P2 = scdb;
	 

		 
	 SwitchDebounce slowClockDebounce (
        .clk(clock_50),
        .reset(reset),
        .io_D(sc),
        .io_Q(scdb)
    );

	 
	 // Debounce key 0 input into "next"
	 SwitchDebounce switchDebounce (
        .clk(clock_50),
        .reset(reset),
        .io_D(nextKey),
        .io_Q(debouncedNextKey)
    );

	 Monostable monostable (
        .clk(clock_50),
        .reset(reset),
		  .io_trigger(scdb),
		  .io_Q(next)
	 );
	 
	 
	 // Our Pseudo Random Number Generator
    Xoroshiro32PlusPlus xoroshiro32PlusPlus (
        .clk(clock_50),
        .reset(reset),
		  .io_next(next),
		  .io_prng(prng)
    );

endmodule
