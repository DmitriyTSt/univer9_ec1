import java.math.BigInteger
import kotlin.math.abs
import kotlin.math.sqrt

object Legendre {

    /**
     * Вычисление символа Лежандра через символ Якоби
     */
    fun get(a: BigInteger, p: BigInteger): Int {
        return sJacoby(a, p)
    }

    /**
     * Вычисление символа Якоби
     */
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