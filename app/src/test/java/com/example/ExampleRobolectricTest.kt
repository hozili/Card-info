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

    val formatted = com.example.util.BankUtils.formatCardNumber("6037991234567890")
    assertEquals("6037  -  9912  -  3456  -  7890", formatted)
  }
}
