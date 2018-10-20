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
//     Scrambled Linear Pseudorandom Number Generators: https://arxiv.org/pdf/1805.01407.pdf
//     The Parallax Forums discussion: 
//

#include <cstdint>
#include <stdlib.h>

class Xoroshiro64PlusPlus
{
  const uint32_t a = 26;
  const uint32_t b = 9;
  const uint32_t c = 13;
  const uint32_t d = 17;

  static inline uint32_t rol(uint32_t x, uint32_t bits) noexcept
  {
    return (x << bits) | (x >> ((sizeof(x) * 8) - bits));
  }

  uint32_t s0;
  uint32_t s1;

public:
  explicit Xoroshiro64PlusPlus(uint32_t s0 = 1, uint32_t s1 = 0) noexcept
    : s0(s0), s1(s1)
  {
  }

  inline uint32_t operator()() noexcept
  {
    s1 ^= s0;
    s0 = rol(s0, a ) ^ s1 ^ (s1 << b);
    s1 = rol(s1, c);

    uint32_t result = rol(s0 + s1, d) + s0;

    return result;
  }
};


class Xoroshiro32PlusPlus
{
  const uint16_t a = 13;
  const uint16_t b = 5;
  const uint16_t c = 10;
  const uint16_t d = 9;

  static inline uint16_t rol(uint16_t x, uint16_t bits) noexcept
  {
    return (x << bits) | (x >> ((sizeof(x) * 8) - bits));
  }

  uint16_t s0;
  uint16_t s1;

public:
  explicit Xoroshiro32PlusPlus(uint16_t s0 = 1, uint16_t s1 = 0) noexcept
    : s0(s0), s1(s1)
  {
  }

  inline uint16_t operator()() noexcept
  {
    s1 ^= s0;
    s0 = rol(s0, a ) ^ s1 ^ (s1 << b);
    s1 = rol(s1, c);

    uint16_t result = rol(s0 + s1, d) + s0;

    return result;
  }
};

