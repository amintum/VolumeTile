package com.android.systemui.volumetile;

import android.media.AudioManager;
import android.widget.SeekBar;
import android.widget.TextView;

public class VolumeSeekBarListener implements SeekBar.OnSeekBarChangeListener {

    private final AudioManager mAudioManager;
    private final int mStreamType;
    private final TextView mPercentText;
    private final int mMaxVolume;

    public VolumeSeekBarListener(AudioManager audioManager, int streamType, TextView percentText, int maxVolume) {
        this.mAudioManager = audioManager;
        this.mStreamType = streamType;
        this.mPercentText = percentText;
        this.mMaxVolume = maxVolume;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }
        try {
            // Update percentage text smoothly and continuously (0% to 100%)
            if (mPercentText != null) {
                mPercentText.setText(progress + "%");
            }

            if (mAudioManager != null && mMaxVolume > 0) {
                // Map continuous 0..100 slider progress to discrete hardware volume index
                int targetVolume = Math.round((progress * (float) mMaxVolume) / 100.0f);
                targetVolume = Math.max(0, Math.min(mMaxVolume, targetVolume));

                mAudioManager.setStreamVolume(mStreamType, targetVolume, 0);

                // If stream is Ring (2), sync Notification (5) and RingerMode
                if (mStreamType == AudioManager.STREAM_RING) {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, targetVolume, 0);
                    if (targetVolume == 0) {
                        try {
                            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        } catch (Throwable ignored) {}
                    } else {
                        try {
                            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        } catch (Throwable ignored) {}
                    }
                }
            }
        } catch (Throwable ignored) {}
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}
