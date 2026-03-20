# ⏰ RecordClockForWork

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-brightgreen?logo=android" />
  <img src="https://img.shields.io/badge/minSdk-24%20(Android%207.0)-blue" />
  <img src="https://img.shields.io/badge/targetSdk-33%20(Android%2013)-blue" />
  <img src="https://img.shields.io/badge/Language-Java-orange?logo=java" />
  <img src="https://img.shields.io/badge/Build-Gradle%207.5-yellow?logo=gradle" />
  <img src="https://img.shields.io/github/actions/workflow/status/Aliuli2026/RecordClockForWork/build.yml?label=CI&logo=githubactions" />
</p>

<p align="center">
  <b>一款智能上班打卡应用 —— 自动计算工作满 9 小时后提醒你下班！</b><br/>
  <i>A smart work clock-in app that automatically reminds you to leave after 9 hours of work!</i>
</p>

---

## 📱 应用简介

**RecordClockForWork** 是一款 Android 打卡应用。你只需在上班时选择打卡时间，应用会自动计算工作满 9 小时的下班时刻，并在时间到达时通过系统闹钟、震动和全屏通知提醒你下班。

### 使用场景举例

| 打卡时间 | 自动计算下班时间 |
|:--------:|:--------------:|
| 09:00    | 18:00          |
| 08:55    | 17:55          |
| 10:30    | 19:30          |
| 07:17    | 16:17          |

> **规则**：下班时间 = 打卡时间 + 9 小时，精确到分钟。

---

## ✨ 功能特性

- 🕘 **灵活打卡**：点击"立即打卡"按钮，弹出时间选择器自由设置上班时间
- ⏱️ **实时倒计时**：精确显示距下班还剩 XX 小时 XX 分 XX 秒
- 🔔 **准时提醒**：工作满 9 小时后触发系统闹钟铃声 + 手机震动
- 📢 **全屏通知**：锁屏状态下也能显示"下班时间到了！"通知
- 🔄 **重新打卡**：随时修改打卡时间，自动更新下班提醒
- ❌ **取消提醒**：一键取消已设置的下班闹钟
- 💾 **状态持久化**：App 重启后自动恢复打卡状态，倒计时继续运行
- 🌏 **上海时区**：所有时间计算均基于 `Asia/Shanghai` 时区

---

## 📸 界面预览

```
┌─────────────────────────────┐
│       ⏰ 上班打卡            │
│    工作满9小时自动提醒下班    │
├─────────────────────────────┤
│   当前时间：09:12:35         │
├─────────────────────────────┤
│   打卡时间：09:00            │
│   ─────────────────         │
│   下班时间：18:00            │
├─────────────────────────────┤
│  距下班还有：08小时47分25秒  │
├─────────────────────────────┤
│  ✅ 已打卡，等待下班提醒...  │
│                              │
│   [ 重新打卡 ]               │
│   [ 取消下班提醒 ]           │
│                              │
│ 💡 打卡后自动计算工作9小时   │
└─────────────────────────────┘
```

---

## 🏗️ 项目结构

```
RecordClockForWork/
├── app/
│   └── src/
│       ├── main/
│       │   ├── java/com/example/recordclockforwork/
│       │   │   ├── MainActivity.java       # 主界面：打卡、倒计时、闹钟设置
│       │   │   └── AlarmReceiver.java      # 广播接收器：触发通知+震动
│       │   ├── res/
│       │   │   ├── layout/
│       │   │   │   └── activity_main.xml   # 主界面布局（卡片式设计）
│       │   │   ├── values/
│       │   │   │   ├── strings.xml         # 字符串资源
│       │   │   │   ├── colors.xml          # 颜色资源
│       │   │   │   └── themes.xml          # 主题样式
│       │   │   └── drawable/
│       │   │       └── ic_launcher.xml     # 应用图标（矢量时钟）
│       │   └── AndroidManifest.xml         # 权限声明 & 组件注册
│       └── test/
│           └── java/com/example/recordclockforwork/
│               └── WorkTimeCalculatorTest.java  # 10个单元测试
├── .github/
│   └── workflows/
│       └── build.yml                       # GitHub Actions CI/CD
├── gradle/wrapper/
│   └── gradle-wrapper.properties
├── build.gradle                            # 根构建配置
├── app/build.gradle                        # 模块构建配置
└── settings.gradle
```

---

## 🔧 技术栈

| 技术 | 说明 |
|------|------|
| **语言** | Java 8 |
| **最低 Android 版本** | Android 7.0 (API 24) |
| **目标 Android 版本** | Android 13 (API 33) |
| **构建工具** | Gradle 7.5 |
| **核心组件** | AlarmManager、NotificationManager、CountDownTimer |
| **UI 组件** | MaterialCardView、TimePickerDialog |
| **数据存储** | SharedPreferences（轻量本地持久化）|
| **测试框架** | JUnit 4.13.2 + Mockito 4.11 |
| **CI/CD** | GitHub Actions（Ubuntu Latest）|

---

## 🚀 快速开始

### 环境要求

- Android Studio Flamingo（或更高版本）
- JDK 11+
- Android SDK (API 24 ~ 33)

### 克隆并运行

```bash
# 1. 克隆仓库
git clone https://github.com/Aliuli2026/RecordClockForWork.git
cd RecordClockForWork

# 2. 使用 Android Studio 打开项目
# File → Open → 选择项目根目录

# 3. 命令行编译（可选）
./gradlew assembleDebug

# 4. 安装到连接的设备/模拟器
./gradlew installDebug
```

### 直接下载 APK

前往 [GitHub Actions Artifacts](https://github.com/Aliuli2026/RecordClockForWork/actions) 下载最新构建的 APK：

- `RecordClockForWork-debug.apk` — 调试版（约 4.3 MB）
- `RecordClockForWork-release-unsigned.apk` — 发布版（约 3.5 MB）

---

## 📋 权限说明

| 权限 | 用途 |
|------|------|
| `SET_ALARM` | 设置下班提醒闹钟 |
| `SCHEDULE_EXACT_ALARM` | Android 12+ 精确闹钟 |
| `USE_EXACT_ALARM` | Android 13+ 精确闹钟 |
| `VIBRATE` | 下班时震动提醒 |
| `POST_NOTIFICATIONS` | 发送下班通知（Android 13+）|
| `RECEIVE_BOOT_COMPLETED` | 手机重启后恢复闹钟 |

> **注意**：Android 12（API 31）及以上设备需在系统设置中授予"精确闹钟"权限，以确保提醒准时触发。路径：设置 → 应用 → RecordClockForWork → 闹钟和提醒 → 允许

---

## 🧪 单元测试

共 **10 个** JUnit 测试，覆盖所有核心时间计算场景：

```bash
./gradlew test
```

| # | 测试用例 | 验证内容 |
|---|---------|---------|
| 1 | `testClockIn_9am_offAt_18pm` | 09:00 打卡 → 18:00 下班 |
| 2 | `testClockIn_8h55_offAt_17h55` | **08:55 打卡 → 17:55 下班**（核心需求）|
| 3 | `testClockIn_10h30_offAt_19h30` | 10:30 打卡 → 19:30 下班 |
| 4 | `testClockIn_midnight_offAt_9am` | 00:00 打卡 → 09:00 下班 |
| 5 | `testWorkDuration_exactlyNineHours` | 工作时长精确为 32400000ms |
| 6 | `testOffTime_alwaysAfterClockIn` | 下班时间 > 打卡时间 |
| 7 | `testRemainingTime_halfHourPassed` | 打卡后30分钟，剩余8.5小时 |
| 8 | `testRemainingTime_overtime` | 超时后剩余时间为负数 |
| 9 | `testClockIn_7h17_offAt_16h17` | 非整点分钟精确保留 |
| 10 | `testReClockIn_overwritesPrevious` | 重新打卡覆盖上次记录 |

---

## ⚙️ CI/CD 流程

每次推送到 `main` 分支自动触发 GitHub Actions：

```
推送代码
  └─► Checkout & JDK 11 配置
        └─► Gradle Wrapper 重生成
              └─► 单元测试 (./gradlew test)
                    └─► Lint 静态分析 (./gradlew lint)
                          └─► 编译 Debug APK
                                └─► 编译 Release APK
                                      └─► 上传产物（APK + 测试报告 + Lint 报告）
```

---

## 📄 许可证

```
MIT License

Copyright (c) 2026 Aliuli2026

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
```

---

## 🙋 联系 & 贡献

欢迎提交 Issue 和 Pull Request！

- 📬 GitHub：[@Aliuli2026](https://github.com/Aliuli2026)
- 🐛 Bug 报告：[Issues](https://github.com/Aliuli2026/RecordClockForWork/issues)

---

<p align="center">Made with ❤️ for every 9-to-6 worker · 献给每一位努力工作的打工人</p>
