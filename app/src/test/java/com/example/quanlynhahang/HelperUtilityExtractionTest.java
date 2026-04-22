package com.example.quanlynhahang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.lang.reflect.Method;

public class HelperUtilityExtractionTest {

    @Test
    public void passwordHelper_hashesWithPrefixAndVerifiesHashedAndLegacyPasswords() throws Exception {
        Class<?> helperClass = loadClass("com.example.quanlynhahang.helper.PasswordHelper");
        Method hashPassword = loadMethod(helperClass, "hashPassword", String.class);
        Method verifyPassword = loadMethod(helperClass, "verifyPassword", String.class, String.class);
        Method isHashedPassword = loadMethod(helperClass, "isHashedPassword", String.class);

        String hashed = (String) hashPassword.invoke(null, "123456");

        assertNotNull(hashed);
        assertTrue(hashed.startsWith("sha256:"));
        assertTrue((Boolean) isHashedPassword.invoke(null, hashed));
        assertTrue((Boolean) verifyPassword.invoke(null, "123456", hashed));
        assertTrue((Boolean) verifyPassword.invoke(null, "123456", "123456"));
        assertFalse((Boolean) verifyPassword.invoke(null, "000000", hashed));
        assertFalse((Boolean) verifyPassword.invoke(null, "123456", (Object) null));
    }

    @Test
    public void moneyUtils_formatsVndAndExtractsDigitsFromPriceStrings() throws Exception {
        Class<?> helperClass = loadClass("com.example.quanlynhahang.helper.MoneyUtils");
        Method formatMoney = loadMethod(helperClass, "dinhDangTienViet", long.class);
        Method parseMoney = loadMethod(helperClass, "tachGiaTienTuChuoi", String.class);

        assertEquals("1.234.567 đ", formatMoney.invoke(null, 1_234_567L));
        assertEquals(120000L, parseMoney.invoke(null, "120.000 đ"));
        assertEquals(45000L, parseMoney.invoke(null, "45,000đ"));
        assertEquals(0L, parseMoney.invoke(null, (Object) null));
        assertEquals(0L, parseMoney.invoke(null, "khong hop le"));
    }

    @Test
    public void dateTimeUtils_formatsCurrentTimeAndParsesKnownTimestamp() throws Exception {
        Class<?> helperClass = loadClass("com.example.quanlynhahang.helper.DateTimeUtils");
        Method formatNow = loadMethod(helperClass, "layThoiGianHienTai");
        Method parseTime = loadMethod(helperClass, "parseDonHangTimeToMillis", String.class);

        String hienTai = (String) formatNow.invoke(null);
        long mocThoiGian = (Long) parseTime.invoke(null, "21/04/2026 13:45");

        assertNotNull(hienTai);
        assertTrue(hienTai.matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}"));
        assertTrue(mocThoiGian > 0L);
        assertEquals(0L, parseTime.invoke(null, "sai dinh dang"));
        assertEquals(0L, parseTime.invoke(null, (Object) null));
    }

    private Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            fail("Missing helper class: " + className);
            return null;
        }
    }

    private Method loadMethod(Class<?> owner, String name, Class<?>... parameterTypes) {
        try {
            return owner.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException ex) {
            fail("Missing helper method: " + owner.getSimpleName() + "." + name);
            return null;
        }
    }
}
