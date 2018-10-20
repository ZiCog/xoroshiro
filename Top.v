
// What is this for ?!
// `include "inc/timescale.vh"

module Top (input clock_50, input[1:0] key, output[7:0] led);

    // Remove reset after 100 clocks
    reg reset = 1;
    reg [7:0] resetCount = 0;
    always @(posedge clock_50)
    begin
        resetCount <= resetCount + 1;
        if (resetCount == 100) reset <= 0;
    end

	 wire [31:0] prng;
	 assign led[7:0] = prng[31:24];
	 
    wire nextKey = !key[0];
	 wire debouncedNextKey;

	 
	 wire next;
	 	 
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
		  .io_trigger(debouncedNextKey),
		  .io_Q(next)
	 );
	 
	 
	 // Our Pseudo Random Number Generator
    Xoroshiro64PlusPlus xoroshiro64PlusPlus (
        .clk(clock_50),
        .reset(reset),
		  .io_next(next),
		  .io_prng(prng)
    );

endmodule
