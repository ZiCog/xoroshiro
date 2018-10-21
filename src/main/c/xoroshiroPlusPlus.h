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

class Xoroshiro128StarStar
{
  const uint32_t A = 24;
  const uint32_t B = 16;
  const uint32_t C = 37;
  const uint32_t S = 5;
  const uint32_t R = 7;
  const uint32_t T = 9;

  static inline uint64_t rol(uint64_t x, uint64_t k) noexcept
  {
    return (x << k) | (x >> (64 - k));
  }

  uint64_t s0;
  uint64_t s1;

public:
  explicit Xoroshiro128StarStar(uint64_t s0 = 1, uint64_t s1 = 0) noexcept
    : s0(s0), s1(s1)
  {
  }

  inline uint64_t operator()() noexcept
  {
    //  This is the "**" scrambler "rotl(s0 * S, R) * T" done with shift and add.
    uint64_t result = rol((s0 << 2) + s0, R);
    result = (result << 3) + result; 

    s1 ^= s0;
    s0 = rol(s0, A) ^ s1 ^ (s1 << B);
    s1 = rol(s1, C);
    
    return result;
  }
};

class Xoroshiro64PlusPlus
{
  const uint32_t a = 26;
  const uint32_t b = 9;
  const uint32_t c = 13;
  const uint32_t d = 17;

  static inline uint32_t rol(uint32_t x, uint32_t k) noexcept
  {
    return (x << k) | (x >> ((sizeof(x) * 8) - k));
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

  static inline uint16_t rol(uint16_t x, uint16_t k) noexcept
  {
    return (x << k) | (k >> ((sizeof(x) * 8) - k));
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
    uint16_t result = rol(s0 + s1, d) + s0;

    s1 ^= s0;
    s0 = rol(s0, a ) ^ s1 ^ (s1 << b);
    s1 = rol(s1, c);


    return result;
  }
};

