package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("کیف کارت امن", appName)
  }

  @Test
  fun `detect bank from card number`() {
    val melli = com.example.util.BankUtils.detectBankFromCardNumber("6037991234567890")
    assertEquals("بانک ملی ایران", melli.name)

    val mellat = com.example.util.BankUtils.detectBankFromCardNumber("6104331234567890")
    assertEquals("بانک ملت", mellat.name)

    val chunks = com.example.util.BankUtils.getCardNumberChunks("1705606118280062")
    assertEquals(listOf("1705", "6061", "1828", "0062"), chunks)

    val plainFormatted = com.example.util.BankUtils.formatCardNumberPlain("1705606118280062")
    assertEquals("1705 - 6061 - 1828 - 0062", plainFormatted)

    // Test bank resolution by name
    val pasargad = com.example.util.BankUtils.getBankByName("پاسارگاد")
    assertEquals("502229", pasargad.code)

    val blu = com.example.util.BankUtils.getBankByName("بلو")
    assertEquals("861980", blu.code)
  }
}
