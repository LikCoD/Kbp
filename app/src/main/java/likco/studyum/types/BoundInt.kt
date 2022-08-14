package likco.studyum.types

import kotlin.math.abs
import kotlin.reflect.KProperty


open class BoundInt(value: Int, val max: Int, val min: Int)  {

    var value: Int = value
    set(value) {
        field = add(value, max, min)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Int = value

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        this.value = value
    }

    override fun toString(): String = "$value"

    companion object {
        fun add(value: Int, max: Int, min: Int = 0): Int {
            return abs(value + min) % max + min
        }
    }
}

fun boundInt(i: Int, max: Int, min: Int = 0) = BoundInt(i, max, min)