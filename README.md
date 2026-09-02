# 🎛️ Cyber HUD Volume Tile

[![Android 9+](https://img.shields.io/badge/Android-9%2B%20%7C%2014%20%7C%2015%20%7C%2016-00E5FF?style=for-the-badge&logo=android)](https://github.com/amintum)
[![License: CC BY-NC-SA 4.0](https://img.shields.io/badge/License-CC%20BY--NC--SA%204.0%20%2B%20Attribution-blue?style=for-the-badge)](LICENSE)

A futuristic **Cyberpunk HUD Volume Control Quick Settings Tile** for Android. Controls Media, Ring, Notifications, Calls, and Alarm volume with smooth continuous 0%–100% precision, 1-tap quick-mute toggles, sound mode switcher chips, and a smoked obsidian glass aesthetic.

---

## 🎨 Visual Preview

<img width="979" height="908" alt="image" src="https://github.com/user-attachments/assets/f62a1c6e-9144-4d04-a46a-548c558fc8a3" />


---

## ⚡ Features
* 🌌 **Cyber HUD Obsidian Glass**: Translucent light black tint backdrop with crisp neon Cyber Cyan borders and glow.
* 🎚️ **Continuous 0%–100% Sliders**: Fluid, high-precision volume sliding without chunky 14% discrete jumps.
* 🔇 **Interactive Quick-Mute**: 1-tap `[🔇]` button to mute any stream instantly to 0% and tap again to restore the exact previous volume level.
* 🔊 **Ringer Mode Chips**: `[ SOUND ]`, `[ VIBRATE ]`, `[ SILENT ]` one-touch sound profile switcher.
* 📞 **Expandable Stream Deck**: Smooth collapsible section for **Calls** and **Alarm** volume.
* ⚙️ **Quick Access Shortcuts**: Dedicated Settings `[⚙]` and Close `[✕]` buttons on the top bar.

---

## 📥 Installation & Usage

### 🔹 Option 1: Standalone Sideload (Without ROM Modification)
You can install and use this tile on any standard Android phone:

1. **Install the APK**:
   ```bash
   adb install releases/VolumeTile.apk
   ```
2. **Add the Tile**:
   * Pull down your Quick Settings panel and tap **Edit (Pencil icon)**.
   * Drag **Volume** into your active tiles.
   * Tap the tile anytime to open the **Cyber HUD Volume Dialog**!

---

### 🔹 Option 2: Full ROM / GSI Integration (For ROM Builders)
To bake `VolumeTile` into your Android ROM or GSI system image:

1. **Place APK into App Partition**:
   ```text
   /system/app/VolumeTile/VolumeTile.apk
   # OR
   /system/product/priv-app/VolumeTile/VolumeTile.apk
   ```
2. **Place Permissions Whitelist**:
   Copy `permissions/privapp-permissions-volumetile.xml` to:
   ```text
   /system/product/etc/permissions/privapp-permissions-volumetile.xml
   ```
3. **(Optional) Set as Default QS Tile**:
   Add `custom(com.android.systemui.volumetile/.VolumeTileService)` to default quick settings tiles string in `SystemUI`.

---

## 🛠️ Building from Source
This repository contains complete Java source code and resources. Build with Android SDK toolchain:
```bash
python build_apk.py
```

---

## 📜 License & Mandatory Credit
This project is licensed under **CC BY-NC-SA 4.0 with Mandatory Attribution**.
* **Credit**: If you use, fork, or integrate this app/code into any ROM or project, you **MUST** credit **Amintum / BestGSI** prominently.
* Commercial use requires explicit permission.

Developed with ❤️ by **[Amintum](https://github.com/amintum)** for **BestGSI**.
