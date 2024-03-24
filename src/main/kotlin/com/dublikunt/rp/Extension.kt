package com.dublikunt.rp

import java.util.concurrent.ThreadLocalRandom

fun IntRange.localRandom(): Int {
    return ThreadLocalRandom.current().nextInt((endInclusive + 1) - start) + start
}
