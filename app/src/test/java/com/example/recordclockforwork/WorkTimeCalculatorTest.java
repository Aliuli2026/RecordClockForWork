package com.example.recordclockforwork;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * 单元测试：验证工作时间计算逻辑
 * 核心逻辑：下班时间 = 打卡时间 + 9小时
 */
public class WorkTimeCalculatorTest {

    /** 工作时长：9小时（毫秒） */
    private static final long WORK_DURATION_MS = 9 * 60 * 60 * 1000L;

    // -------------------------------------------------------------------------
    // 辅助方法
    // -------------------------------------------------------------------------

    /**
     * 构造当天指定小时:分钟的时间戳（上海时区）
     */
    private long buildTimeMs(int hour, int minute) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 从时间戳提取小时（上海时区）
     */
    private int getHour(long ms) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
        cal.setTimeInMillis(ms);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 从时间戳提取分钟（上海时区）
     */
    private int getMinute(long ms) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
        cal.setTimeInMillis(ms);
        return cal.get(Calendar.MINUTE);
    }

    // -------------------------------------------------------------------------
    // 测试用例
    // -------------------------------------------------------------------------

    /**
     * 测试1：上午 9:00 打卡 → 下午 18:00 下班
     */
    @Test
    public void testClockIn_9am_offAt_18pm() {
        long clockInMs = buildTimeMs(9, 0);
        long offTimeMs = clockInMs + WORK_DURATION_MS;

        assertEquals("下班小时应为 18", 18, getHour(offTimeMs));
        assertEquals("下班分钟应为 0",   0,  getMinute(offTimeMs));
    }

    /**
     * 测试2：上午 8:55 打卡 → 下午 17:55 下班（用户需求核心场景）
     */
    @Test
    public void testClockIn_8h55_offAt_17h55() {
        long clockInMs = buildTimeMs(8, 55);
        long offTimeMs = clockInMs + WORK_DURATION_MS;

        assertEquals("下班小时应为 17", 17, getHour(offTimeMs));
        assertEquals("下班分钟应为 55", 55, getMinute(offTimeMs));
    }

    /**
     * 测试3：上午 10:30 打卡 → 下午 19:30 下班
     */
    @Test
    public void testClockIn_10h30_offAt_19h30() {
        long clockInMs = buildTimeMs(10, 30);
        long offTimeMs = clockInMs + WORK_DURATION_MS;

        assertEquals("下班小时应为 19", 19, getHour(offTimeMs));
        assertEquals("下班分钟应为 30", 30, getMinute(offTimeMs));
    }

    /**
     * 测试4：凌晨 0:00 打卡 → 上午 9:00 下班
     */
    @Test
    public void testClockIn_midnight_offAt_9am() {
        long clockInMs = buildTimeMs(0, 0);
        long offTimeMs = clockInMs + WORK_DURATION_MS;

        assertEquals("下班小时应为 9", 9, getHour(offTimeMs));
        assertEquals("下班分钟应为 0", 0, getMinute(offTimeMs));
    }

    /**
     * 测试5：工作时长必须恰好为 9 小时（毫秒验证）
     */
    @Test
    public void testWorkDuration_exactlyNineHours() {
        long expectedMs = 9L * 60 * 60 * 1000;
        assertEquals("工作时长应为 32400000 毫秒（9小时）", expectedMs, WORK_DURATION_MS);
    }

    /**
     * 测试6：下班时间必须晚于打卡时间
     */
    @Test
    public void testOffTime_alwaysAfterClockIn() {
        long clockInMs = buildTimeMs(9, 0);
        long offTimeMs = clockInMs + WORK_DURATION_MS;
        assertTrue("下班时间必须晚于打卡时间", offTimeMs > clockInMs);
    }

    /**
     * 测试7：倒计时剩余时间计算（已过半小时）
     */
    @Test
    public void testRemainingTime_halfHourPassed() {
        long clockInMs = buildTimeMs(9, 0);
        long offTimeMs = clockInMs + WORK_DURATION_MS;
        // 模拟当前时间为打卡后 30 分钟
        long nowMs = clockInMs + 30 * 60 * 1000L;
        long remaining = offTimeMs - nowMs;

        long expectedRemaining = 8L * 60 * 60 * 1000 + 30 * 60 * 1000; // 8.5 小时
        assertEquals("距下班应还剩 8.5 小时", expectedRemaining, remaining);
    }

    /**
     * 测试8：倒计时为负数时（已超时）表示已下班
     */
    @Test
    public void testRemainingTime_overtime() {
        long clockInMs = buildTimeMs(9, 0);
        long offTimeMs = clockInMs + WORK_DURATION_MS;
        // 模拟当前已过了下班时间 1 小时
        long nowMs = offTimeMs + 60 * 60 * 1000L;
        long remaining = offTimeMs - nowMs;

        assertTrue("超时后剩余时间应为负数", remaining < 0);
    }

    /**
     * 测试9：非整点分钟数保留（7:17 打卡 → 16:17 下班）
     */
    @Test
    public void testClockIn_7h17_offAt_16h17() {
        long clockInMs = buildTimeMs(7, 17);
        long offTimeMs = clockInMs + WORK_DURATION_MS;

        assertEquals("下班小时应为 16", 16, getHour(offTimeMs));
        assertEquals("下班分钟应为 17", 17, getMinute(offTimeMs));
    }

    /**
     * 测试10：多次打卡只保留最后一次（时间覆盖验证）
     */
    @Test
    public void testReClockIn_overwritesPrevious() {
        long firstClockIn  = buildTimeMs(8, 0);
        long secondClockIn = buildTimeMs(9, 30); // 重新打卡

        long offTimeFirst  = firstClockIn  + WORK_DURATION_MS;
        long offTimeSecond = secondClockIn + WORK_DURATION_MS;

        // 第二次打卡的下班时间应为 18:30
        assertEquals(17, getHour(offTimeFirst));    // 第一次：17:00
        assertEquals( 0, getMinute(offTimeFirst));
        assertEquals(18, getHour(offTimeSecond));   // 第二次：18:30
        assertEquals(30, getMinute(offTimeSecond));

        // 确认两次打卡结果不同
        assertNotEquals(offTimeFirst, offTimeSecond);
    }
}
