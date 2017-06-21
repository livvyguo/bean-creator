package org.lvy.crobot

import java.math.BigDecimal
import kotlin.reflect.KProperty

/**
 * @author guozheng
 * @date 2017/06/09
 */

fun main(args: Array<String>) {

    println("hhhh");

    println(sum(10, 11))

    println(add(1, 2))


    val a: Int = 100


    vv("livvy")


    for (i in 1 .. 10 step 2) {
        print("val is $i   ")
    }

    for (x in 9 downTo 0 step 3) {
        print("down is $x  ")
    }

    val lists = listOf("apple", "banana", "kivi")

    for (item in lists) {
        println(item)
    }

    println("apple" in lists)

    lists.filter { it.contains("a") }
            .map { it.toUpperCase() }
            .forEach { println(it) }


    var stu = Student("livvy", "hebei")

    println(stu)

    stu.address = "xingtai"

    println(stu)


    val l = mutableListOf<Int>(1, 2, 3)

    l.swap(0, 2)

    println(l)





    foo()

    val sum1 = sum(1, 10)
    println(sum1)


    val zz = 1..10

    zz.forEach { println(it) }
    println(zz)

    println(10-9.6)
    val z1 = BigDecimal("10")
    val z2 = BigDecimal("9.6")

    println(z1 - z2)



}

val sum = {x:Int,y:Int-> x + y }



fun foo(a: Int = 10) {
    println(a)
}
/*
*
* jjsk/*ksaksa*/
*
* */

fun sum(a :Int,b :Int):Int {
    return a + b;
}


fun add(a: Int, b: Int) = a + b;

fun vv(name: String): Unit {
    println("hello $name ")
    val bbb = """
aaaaa
akkal
    """;

    println(bbb)


}


data class Student(var name: String, var address: String)


open class Human(id:Int)

class Person(
        id: Int,
        name: String,
        surname: String
) : Human(id) {
    init {

    }
}


fun MutableList<Int>.swap(index1: Int, index2: Int) {
    val temp = this[index1]
    this[index1] = this[index2]
    this[index2] = temp
}


class Delegate {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "$thisRef, thank you for delegating '${property.name}' to me!"
    }
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) { println("$value has been assigned to '${property.name} in $thisRef.'")
    }
}
