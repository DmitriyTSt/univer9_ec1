import kotlin.system.exitProcess

/**
 * Генерация эллиптической кривой с j = 0
 */
fun main() {
    println("Введите l")
    val l = readInt()
    println("Введите m")
    val m = readInt()
    val timeStart = System.currentTimeMillis()
    var p = Utils.generatePrimeP(l, null)
    var result = solve(p, m)
    while (result == null) {
        p = Utils.generatePrimeP(l, p)
        result = solve(p, m)
    }
    val timeAll = System.currentTimeMillis() - timeStart
    println("Time spent: %d.%03d s".format(timeAll / 1000, timeAll % 1000))
    println("Result:\n$result")
}

private fun readInt(): Int {
    var int = readLine()?.toIntOrNull()
    while (int == null) {
        println("Введите число")
        int = readLine()?.toIntOrNull()
    }
    return int
}

class Result(
        val p: Long,
        val b: Long,
        val q: Point,
        val r: Long
) {
    override fun toString(): String {
        return "p = $p\nb = $b\nq = $q\nr = $r"
    }
}

fun solve(p: Long?, m: Int): Result? {
    if (p == null) {
        println("Для заданного l решение не найдено")
        exitProcess(0)
    }
    logd("p = $p")
    val cd = Utils.getCD(p) ?: return null
    val c = cd.first
    val d = cd.second
    logd("$p = ($c)^2 + 3*($d)^2,    c = $c, d = $d")
    val nr = Utils.getNR(p, c, d) ?: return null
    val n = nr.first
    val r = nr.second
    logd("N = $n, r = $r")
    if (p == r) return null
    var pp = p
    repeat(m) {
        if (pp % r == 1L) {
            return null
        }
        pp *= p
    }
    logd("p != r, p^i != 1 (mod r)")
    var result: Result?  = null
    while (result == null) {
        val point = Utils.generatePoint(p)
        val x0 = point.x
        val y0 = point.y
        val b = ((y0 * y0 % n - (x0 * x0 * x0) % n) + n) % n
        logd("($x0, $y0) b = $b")
        val checkB = when (n / r) {
            // проверка b - квадратичный и кубический НЕвычет для n == r
            1L -> Utils.isSqrResidue(b, n) == false && Utils.isCubeResidue(b, r) == false
            // проверка b - квадратичный и кубический вычет для n == 6r
            6L -> Utils.isSqrResidue(b, n) == true && Utils.isCubeResidue(b, r) == true
            // проверка b - квадратичный НЕвычет и кубический вычет для n == 2r
            2L -> Utils.isSqrResidue(b, n) == false && Utils.isCubeResidue(b, r) == true
            // проверка b - квадратичный вычет и кубический НЕвычет для n == 3r
            3L -> Utils.isSqrResidue(b, r) == true && Utils.isSqrResidue(b, 3) == true && Utils.isCubeResidue(b, r) == false
            else -> false
        }
        if (checkB) {
            // проверка N * point = P(inf) (знаменатель углового коэф в формуле сложения обращается в 0)
            val newP = point * n
            logd("n * point = $newP")
            // иначе generatePoint()

            val q = point * (n / r)

            result = Result(p, b, q, r)
        }
    }

    return result
}