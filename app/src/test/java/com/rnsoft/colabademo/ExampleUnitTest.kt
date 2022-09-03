package com.rnsoft.colabademo

import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun validateEmail(){

    }

    @RunWith(JUnit4::class)
    class ValidatorTest{

        @Test
        fun whenInputIsValid(){
            val amount = 100
            val desc = "Some random desc"
            //val result = Validator.validateInput(amount, desc)
            //assertThat(result).isEqualTo(true)
        }

        @Test
        fun whenInputIsInvalid(){
            val amount = 0
            val desc = ""
            //val result = Validator.validateInput(amount, desc)
            //assertThat(result).isEqualTo(false)
        }

    }

}