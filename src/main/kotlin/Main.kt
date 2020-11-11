import org.knowm.xchart.*
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle
import java.math.BigInteger
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
    showChart(result)
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
        val p: BigInteger,
        val b: BigInteger,
        val q: Point,
        val r: BigInteger
) {
    override fun toString(): String {
        return "p = $p\nb = $b\nq = $q\nr = $r"
    }
}

fun solve(p: BigInteger?, m: Int): Result? {
    if (p == null) {
        println("Для заданного l решение не найдено")
        exitProcess(0)
    }
    logd("p = $p")
    val cd = CDSqr.get(p, 3.toBigInteger()) ?: return null
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
        if (pp % r == BigInteger.ONE) {
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
            1.toBigInteger() -> !Utils.isSqrResidue(b, n) && Utils.isCubeResidue(b, r) == false
            // проверка b - квадратичный и кубический вычет для n == 6r
            6.toBigInteger() -> Utils.isSqrResidue(b, n) && Utils.isCubeResidue(b, r) == true
            // проверка b - квадратичный НЕвычет и кубический вычет для n == 2r
            2.toBigInteger() -> !Utils.isSqrResidue(b, n) && Utils.isCubeResidue(b, r) == true
            // проверка b - квадратичный вычет и кубический НЕвычет для n == 3r
            3.toBigInteger() -> Utils.isSqrResidue(b, r) &&
                    Utils.isSqrResidue(b, 3.toBigInteger()) && Utils.isCubeResidue(b, r) == false
            else -> false
        }
        if (checkB) {
            // проверка N * point = P(inf) (знаменатель углового коэф в формуле сложения обращается в 0)
            val newP = point * n
            logd("n * point = $newP")
            if (newP.inf) {
                val q = point * (n / r)
                result = Result(p, b, q, r)
            }
        }
    }

    return result
}

fun showChart(result: Result) {
    if (result.p.bitLength() < 20) {
        var start = result.q
        val xData = mutableListOf<Long>()
        val yData = mutableListOf<Long>()
        do {
            xData.add(start.x.toLong())
            yData.add(start.y.toLong())
            start += result.q
        } while (start != result.q)

        val chart: XYChart = XYChartBuilder()
                .width(1300)
                .height(900)
                .title("Elliptic")
                .xAxisTitle("X")
                .yAxisTitle("Y")
                .build()
        chart.styler.apply {
            defaultSeriesRenderStyle = XYSeriesRenderStyle.Scatter
            isChartTitleVisible = false
            isLegendVisible = false
            markerSize = 2
        }
        chart.addSeries("Elliptic 1", xData, yData)
        SwingWrapper(chart).displayChart()
    } else {
        println("p = ${result.p}\nСлишком большое, чтобы нарисовать график, график рисуется для l < 20")
    }
}