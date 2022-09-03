                                    package com.rnsoft.colabademo



import com.google.common.truth.Truth
import org.junit.Test

/**
 * Unit tests for the EmailValidator logic.
 */
class LoginUtilTest {
    @Test
    fun `return null if valid email`() {
        val result = LoginUtil.isValidEmail("itsmeadil2@hotmail.com")
        Truth.assertThat(result).isNull()
    }


    @Test
    fun `return string if empty`() {
        Truth.assertThat(LoginUtil.isValidEmail("")).isEqualTo("Empty Email, Please try again…")
    }

    @Test
    fun `return string if invalid email`() {
        Truth.assertThat(LoginUtil.isValidEmail("bing@easdf")).isEqualTo("Invalid Email, Please try again…")
        Truth.assertThat(LoginUtil.isValidEmail("@stest.com")).isEqualTo("Invalid Email, Please try again…")
        Truth.assertThat(LoginUtil.isValidEmail("123@st@est.com.pk.ujk.lioin")).isEqualTo("Invalid Email, Please try again…")
    }

    @Test
    fun `return null if password is acceptable`() {
        Truth.assertThat(LoginUtil.checkPasswordLength("bing@easdf")).isEqualTo(null)
    }

    @Test
    fun `return empty string if password is empty or blank`() {
        Truth.assertThat(LoginUtil.checkPasswordLength("")).isEqualTo("Password Empty, Please try again…")
        Truth.assertThat(LoginUtil.checkPasswordLength(" ")).isEqualTo("Password Empty, Please try again…")
        Truth.assertThat(LoginUtil.checkPasswordLength("    ")).isEqualTo("Password Empty, Please try again…")
    }

    @Test
    fun `return string if password contains space`() {
        Truth.assertThat(LoginUtil.checkPasswordLength("binas ads")).isEqualTo("Password can not contain space…")
    }

    /*
    @Test
    fun emailValidator_InvalidEmailNoTld_ReturnsFalse() {
        assertFalse(EmailValidator.isValidEmail("name@email"))
    }

    @Test
    fun emailValidator_InvalidEmailDoubleDot_ReturnsFalse() {
        assertFalse(EmailValidator.isValidEmail("name@email..com"))
    }

    @Test
    fun emailValidator_InvalidEmailNoUsername_ReturnsFalse() {
        assertFalse(EmailValidator.isValidEmail("@email.com"))
    }

    @Test
    fun emailValidator_EmptyString_ReturnsFalse() {
        assertFalse(EmailValidator.isValidEmail(""))
    }

    @Test
    fun emailValidator_NullEmail_ReturnsFalse() {
        assertFalse(EmailValidator.isValidEmail(null))
    }


     */
}