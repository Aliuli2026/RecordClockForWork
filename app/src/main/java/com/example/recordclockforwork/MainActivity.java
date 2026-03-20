package com.example.recordclockforwork;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private static final String PREF_NAME = "ClockPrefs";
    private static final String KEY_CLOCK_IN_TIME = "clock_in_time";
    private static final String KEY_IS_CLOCKED_IN = "is_clocked_in";
    public static final String CHANNEL_ID = "work_alarm_channel";

    private TextView tvCurrentTime;
    private TextView tvClockInTime;
    private TextView tvOffTime;
    private TextView tvCountDown;
    private TextView tvStatus;
    private Button btnClockIn;
    private Button btnCancelAlarm;

    private CountDownTimer countDownTimer;
    private SharedPreferences sharedPreferences;

    // 工作时长：9小时（毫秒）
    private static final long WORK_DURATION_MS = 9 * 60 * 60 * 1000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // 初始化通知渠道
        createNotificationChannel();

        // 绑定视图
        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvClockInTime = findViewById(R.id.tv_clock_in_time);
        tvOffTime = findViewById(R.id.tv_off_time);
        tvCountDown = findViewById(R.id.tv_count_down);
        tvStatus = findViewById(R.id.tv_status);
        btnClockIn = findViewById(R.id.btn_clock_in);
        btnCancelAlarm = findViewById(R.id.btn_cancel_alarm);

        // 实时更新当前时间
        updateCurrentTime();

        // 恢复已打卡状态
        restoreClockInState();

        // 打卡按钮点击 -> 弹出时间选择器
        btnClockIn.setOnClickListener(v -> showTimePickerDialog());

        // 取消闹钟按钮
        btnCancelAlarm.setOnClickListener(v -> cancelAlarm());
    }

    /**
     * 显示时间选择器，让用户选择打卡时间（默认当前时间）
     */
    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfHour) -> {
                    // 用户选择了打卡时间
                    doClockIn(hourOfDay, minuteOfHour);
                },
                hour, minute, true); // true = 24小时制

        dialog.setTitle("选择打卡时间（上海时间）");
        dialog.show();
    }

    /**
     * 执行打卡操作
     * @param hour 打卡小时（0-23）
     * @param minute 打卡分钟
     */
    private void doClockIn(int hour, int minute) {
        // 计算今天的打卡时刻（上海时区）
        Calendar clockInCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
        clockInCal.set(Calendar.HOUR_OF_DAY, hour);
        clockInCal.set(Calendar.MINUTE, minute);
        clockInCal.set(Calendar.SECOND, 0);
        clockInCal.set(Calendar.MILLISECOND, 0);

        long clockInMs = clockInCal.getTimeInMillis();

        // 计算下班时间 = 打卡时间 + 9小时
        long offTimeMs = clockInMs + WORK_DURATION_MS;

        // 保存打卡时间
        sharedPreferences.edit()
                .putLong(KEY_CLOCK_IN_TIME, clockInMs)
                .putBoolean(KEY_IS_CLOCKED_IN, true)
                .apply();

        // 更新 UI
        updateClockInUI(clockInMs, offTimeMs);

        // 设置闹钟
        scheduleAlarm(offTimeMs);

        String timeStr = String.format(Locale.CHINA, "%02d:%02d", hour, minute);
        Toast.makeText(this, "打卡成功！" + timeStr + " 已记录，下班提醒已设置", Toast.LENGTH_LONG).show();
    }

    /**
     * 更新打卡相关 UI 显示
     */
    private void updateClockInUI(long clockInMs, long offTimeMs) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        tvClockInTime.setText("打卡时间：" + sdf.format(clockInMs));
        tvOffTime.setText("下班时间：" + sdf.format(offTimeMs));
        tvStatus.setText("✅ 已打卡，等待下班提醒...");
        btnCancelAlarm.setVisibility(View.VISIBLE);
        btnClockIn.setText("重新打卡");

        // 启动倒计时
        startCountDown(offTimeMs);
    }

    /**
     * 启动倒计时显示
     */
    private void startCountDown(long offTimeMs) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        long now = System.currentTimeMillis();
        long remaining = offTimeMs - now;

        if (remaining <= 0) {
            tvCountDown.setText("下班时间已到！🎉");
            tvStatus.setText("🎉 已下班！");
            return;
        }

        countDownTimer = new CountDownTimer(remaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long hours = millisUntilFinished / (1000 * 60 * 60);
                long minutes = (millisUntilFinished % (1000 * 60 * 60)) / (1000 * 60);
                long seconds = (millisUntilFinished % (1000 * 60)) / 1000;
                tvCountDown.setText(String.format(Locale.CHINA,
                        "距下班还有：%02d小时 %02d分 %02d秒", hours, minutes, seconds));
                // 同时刷新当前时间
                updateCurrentTime();
            }

            @Override
            public void onFinish() {
                tvCountDown.setText("下班时间到了！🎉");
                tvStatus.setText("🎉 已下班！可以离开啦！");
            }
        }.start();
    }

    /**
     * 设置精确闹钟
     */
    private void scheduleAlarm(long triggerTimeMs) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction("com.example.recordclockforwork.ALARM_ACTION");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12+ 需要检查精确闹钟权限
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP, triggerTimeMs, pendingIntent);
                } else {
                    // 降级使用非精确闹钟
                    alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP, triggerTimeMs, pendingIntent);
                    Toast.makeText(this, "提示：请在设置中允许精确闹钟权限，以确保准时提醒", Toast.LENGTH_LONG).show();
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, triggerTimeMs, pendingIntent);
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP, triggerTimeMs, pendingIntent);
            }
        }
    }

    /**
     * 取消闹钟
     */
    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction("com.example.recordclockforwork.ALARM_ACTION");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }

        // 取消倒计时
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // 清除保存状态
        sharedPreferences.edit()
                .putBoolean(KEY_IS_CLOCKED_IN, false)
                .apply();

        tvClockInTime.setText("打卡时间：未打卡");
        tvOffTime.setText("下班时间：--");
        tvCountDown.setText("等待打卡...");
        tvStatus.setText("⏰ 请点击打卡按钮记录上班时间");
        btnCancelAlarm.setVisibility(View.GONE);
        btnClockIn.setText("立即打卡");

        Toast.makeText(this, "已取消下班提醒", Toast.LENGTH_SHORT).show();
    }

    /**
     * 恢复之前已打卡状态（App 重启后）
     */
    private void restoreClockInState() {
        boolean isClockedIn = sharedPreferences.getBoolean(KEY_IS_CLOCKED_IN, false);
        if (isClockedIn) {
            long clockInMs = sharedPreferences.getLong(KEY_CLOCK_IN_TIME, 0);
            if (clockInMs > 0) {
                long offTimeMs = clockInMs + WORK_DURATION_MS;
                updateClockInUI(clockInMs, offTimeMs);
            }
        }
    }

    /**
     * 更新当前时间显示（上海时区）
     */
    private void updateCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        String timeStr = sdf.format(System.currentTimeMillis());
        tvCurrentTime.setText("当前时间：" + timeStr);
    }

    /**
     * 创建通知渠道（Android 8.0+）
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "下班提醒";
            String description = "工作满9小时后的下班提醒通知";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 200, 500, 200, 500});

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
