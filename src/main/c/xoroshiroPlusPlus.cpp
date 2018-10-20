//
// xoroshiro32++ and xoroshiro64++ 
//
// Classes implementing the xoroshiro32++ and xoroshiro64++ PRNG algorthims by David Blackman and Sebastiano Vigna
//
// The xoroshiro32++ is as used in tha Prallax Propeller 2 micro-controller. 
//
// The xoroshiro64++ is included here as a nice long cycle PRNG that needs no multiply.
//
// References:
//
//     Scrambled Linear Pseudorandom Number Generators: https://arxiv.org/pdf/1805.01407.pdf
//
//     The Parallax Forums discussion: http://forums.parallax.com/discussion/166176/random-lfsr-on-p2/p62
//
#include <cstdint>
#include <stdlib.h>
#include <iostream>
#include <iomanip>
#include <bitset>

#include "xoroshiroPlusPlus.h"

#define SAMPLE_SIZE 16

void test32()
{
  Xoroshiro32PlusPlus prng(1, 0);
  uint16_t random;

  std::cout << "First " << std::dec << SAMPLE_SIZE << " outputs of Xoroshiro32PlusPlus:" << std::endl; 

  for (uint64_t i = 0; i < SAMPLE_SIZE; i++)
  {
    random = prng();
    std::cout << std::hex << std::setfill('0') << std::setw(4) << random << std::endl;
//    std::cout << std::bitset<16>(random) << std::endl;
  }
}

void test64()
{
  Xoroshiro64PlusPlus prng(1, 0);
  uint32_t random;

  std::cout << "First " << std::dec << SAMPLE_SIZE << " outputs of Xoroshiro64PlusPlus:" << std::endl; 

  for (uint64_t i = 0; i < SAMPLE_SIZE; i++)
  {
    random = prng();
    std::cout << std::hex << std::setfill('0') << std::setw(8) << random << std::endl;
    //std::cout << std::bitset<32>(random);

  }
  std::cout << std::endl;
}

int main(int argc, char* argv[])
{
  test32();
  std::cout << std::endl;
  test64();
}
