package likco.studyum.types

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BoundIntTest {
    @Test
    fun `9+21=0`() {
        var boundInt by boundInt(9, 10)
        boundInt += 21

        assertEquals(0, boundInt)
    }

    @Test
    fun `9+1=0`() {
        var boundInt by boundInt(9, 10)
        boundInt += 1

        assertEquals(0, boundInt)
    }

    @Test
    fun `5+10=5`() {
        var boundInt by boundInt(5, 10, 5)
        boundInt += 10

        assertEquals(5, boundInt)
    }
    @Test
    fun `5-10=5`() {
        var boundInt by boundInt(5, 10, 5)
        boundInt -= 10

        assertEquals(5, boundInt)
    }

    @Test
    fun `0-1=1`() {
        var boundInt by boundInt(0, 2)
        boundInt -= 1

        assertEquals(1, boundInt)
    }
}