import java.math.BigInteger
import kotlin.random.Random

object Utils {
    const val DEBUG = false
    private val MOD = 6.toBigInteger()
    private val random = java.util.Random()

    fun generatePrimeP(bits: Int, lastBad: BigInteger?): BigInteger? {
        var start = 2.toBigInteger()
        start = start.pow((bits - 1))
        val end = start * 2.toBigInteger()
        while (start % MOD != BigInteger.ONE) {
            start++
        }
        while (!isPrime(start) || start <= lastBad ?: BigInteger.ZERO) {
            start += MOD
        }
        if (start >= end) {
            return null
        } else {
            return start
        }
    }

    private fun isPrime(p: BigInteger): Boolean {
        return p.isProbablePrime(128)
    }

    fun getCD(p: BigInteger): Pair<BigInteger, BigInteger>? {
        val sqrtP = p / 2.toBigInteger()
        var c = BigInteger.ONE
        while (c < sqrtP) {
            var d = BigInteger.ONE
            while (d < sqrtP) {
                if (c * c + 3.toBigInteger() * d * d == p) {
                    return c to d
                }
                d++
            }
            c++
        }
        return null
    }

    fun getNR(p: BigInteger, c: BigInteger, d: BigInteger): Pair<BigInteger, BigInteger>? {
        val tArr = listOf(
                c + 3.toBigInteger() * d,
                c - 3.toBigInteger() * d,
                2.toBigInteger() * c
        ).flatMap { listOf(it, -it) }
        logd("T = $tArr")
        val div = listOf(1, 2, 3, 6).map { it.toBigInteger() }
        val nr = tArr.flatMap { t ->
            val n = p + BigInteger.ONE + t
            div.filter { n % it == BigInteger.ZERO && isPrime(n / it) }.map { n to (n / it) }
        }
        return nr.firstOrNull()
    }

    fun generatePoint(p: BigInteger): Point {
        var x0 = Random.nextBigInteger(p)
        while (x0 == BigInteger.ZERO) {
            x0 = Random.nextBigInteger(p)
        }
        var y0 = Random.nextBigInteger(p)
        while (y0 == BigInteger.ZERO) {
            y0 = Random.nextBigInteger(p)
        }
        return Point(x0, y0, p)
    }

    /**
     * Вычисление символа Лежандра
     */
    private fun L(a: Long, p: Long): Int {
        return Legendre.get(a, p)
    }

//    fun isSqrResidue(b: Long, p: Long): Boolean? {
//        return when (L(b, p)) {
//            1 -> true
//            -1 -> false
//            else -> null
//        }
//    }

    fun isSqrResidue(b: BigInteger, p: BigInteger): Boolean {
        val pow = b.modPow((p - 1.toBigInteger()) / 2.toBigInteger(), p)
        return pow == 1.toBigInteger()
    }

    fun isCubeResidue(b: BigInteger, p: BigInteger): Boolean? {
        if ((p - 1.toBigInteger()) % 3.toBigInteger() == BigInteger.ZERO) {
            return b.modPow((p - 1.toBigInteger()) / 3.toBigInteger(), p) % p == 1.toBigInteger()
        } else if ((p - 1.toBigInteger()) % 3.toBigInteger() == 2.toBigInteger()) {
            return true
        } else {
            return null
        }
    }

    private fun Random.Default.nextBigInteger(p: BigInteger): BigInteger {
        return BigInteger(p.bitLength(), random) % p
    }
}