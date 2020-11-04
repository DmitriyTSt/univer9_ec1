fun Long.pow(pow: Long, mod: Long): Long {
    var result = 1L
    for (i in 1 .. pow) {
        result = (result * this) % mod
    }
    return result
}

fun logd(string: String) {
    if (Utils.DEBUG) {
        println(string)
    }
}