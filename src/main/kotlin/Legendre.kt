import java.math.BigInteger
import kotlin.math.abs
import kotlin.math.sqrt

object Legendre {

    fun get(a: BigInteger, p: BigInteger): Int {
        return sJacoby(a, p)
    }

    fun bad(_a: Long, p: Long): Int {
        println("L ($_a, $p)")
        if (p == 0L) {
            return 0
        }
        if (_a == 1L) {
            return 1
        }
        if (_a == -1L || _a % p == p - 1) {
            // -1 pow (p-1)/2
            val p12 = (p - 1) / 2
            return if (p12 % 2 == 0L) 1 else -1
        }
        if (_a == 2L) {
            // -1 pow (p^2-1)/8
            val p218 = ((p * p - 1)) / 8
            return if (p218 % 2 == 0L) 1 else -1
        }
        if (_a < 0) {
            return bad(abs(_a), p) * bad(-1, p)
        }
        val a = _a % p
        val trialDivsFiltered = trialDivs(a).filter { it.second % 2 != 0L }
        var result = 1
        trialDivsFiltered.map { it.first }.forEach {
            val pow1 = if (it % 4 == 3L || p % 4 == 3L) -1 else 1
            result *= pow1 * bad(p, it)
        }
        println("L ($_a, $p) = $result")
        return result

    }

    /**
     * Разложение на простые множители
     * @return List(Pair(prime, power))
     */
    private fun trialDivs(_a: Long): List<Pair<Long, Long>> {
        var p = 2L
        var st = 0L
        var a = _a
        val result = mutableListOf<Pair<Long, Long>>()
        while (!isPrime(a)) {
            while (a % p == 0L) {
                a /= p
                st++
            }
            if (st > 0) {
                result.add(p to st)
                st = 0
            }
            p++
            while (!isPrime(p)) {
                p++
            }
        }
        if (a != 1L) {
            result.add(a to 1L)
        }
        return result
    }

    private fun isPrime(a: Long): Boolean {
        repeat(sqrt(a.toDouble()).toInt() + 1) {
            if (it >= 2) {
                if (a % it == 0L) return false
            }
        }
        return true
    }

    private fun sJacoby(_a: BigInteger, n: BigInteger): Int {
        var a = _a
        if (a == BigInteger.ZERO) {
            return 0
        }
        if (a == BigInteger.ONE) {
            return 1
        }
        if (a < BigInteger.ZERO) {
            return if (n % 2.toBigInteger() == BigInteger.ZERO) {
                sJacoby(-a, n)
            } else {
                -sJacoby(-a, n)
            }
        }
        var t = BigInteger.ZERO
        while (a % 2.toBigInteger() == BigInteger.ZERO) {
            a /= 2.toBigInteger()
            t++
        }
        var pow = -1
        if (t % 2.toBigInteger() == BigInteger.ZERO ||
            n % 8.toBigInteger() == BigInteger.ONE ||
            n % 8.toBigInteger() == 7.toBigInteger()) {
            pow = -pow
        }
        if (n % 4.toBigInteger() == 3.toBigInteger() && a % 4.toBigInteger() == 3.toBigInteger()) {
            pow = -pow
        }
        return if (a == BigInteger.ONE) {
            pow
        } else {
            pow * sJacoby(n % a, a)
        }
    }
}