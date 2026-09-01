package com.android.systemui.volumetile;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class VolumeTileService extends TileService {

    private static int sSavedMediaVol = -1;
    private static int sSavedRingVol = -1;
    private static int sSavedCallVol = -1;
    private static int sSavedAlarmVol = -1;

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        updateVolumeTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateVolumeTile();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    public void updateVolumeTile() {
        try {
            Tile tile = getQsTile();
            if (tile != null) {
                tile.setState(Tile.STATE_INACTIVE);
                tile.setLabel("Volume");
                tile.updateTile();
            }
        } catch (Throwable ignored) {}
    }

    public static int evaluateActiveMode(AudioManager am) {
        if (am == null) return AudioManager.RINGER_MODE_NORMAL;
        try {
            int ringVol = am.getStreamVolume(AudioManager.STREAM_RING);
            if (ringVol == 0) {
                return AudioManager.RINGER_MODE_SILENT;
            }
            return am.getRingerMode();
        } catch (Throwable ignored) {
            return AudioManager.RINGER_MODE_NORMAL;
        }
    }

    public static void syncRingSeekBar(View dialogView, AudioManager am) {
        if (dialogView == null || am == null) return;
        try {
            SeekBar seekRing = dialogView.findViewById(R.id.seekbar_ring);
            TextView textRingPct = dialogView.findViewById(R.id.text_ring_pct);

            int currentVol = am.getStreamVolume(AudioManager.STREAM_RING);
            int maxVol = am.getStreamMaxVolume(AudioManager.STREAM_RING);

            if (evaluateActiveMode(am) == AudioManager.RINGER_MODE_SILENT) {
                currentVol = 0;
            }

            int pct = (maxVol > 0) ? Math.round((currentVol * 100.0f) / maxVol) : 0;
            pct = Math.max(0, Math.min(100, pct));

            if (seekRing != null) {
                seekRing.setProgress(pct);
            }
            if (textRingPct != null) {
                textRingPct.setText(pct + "%");
            }
        } catch (Throwable ignored) {}
    }

    public static void updateRingerModeUI(View dialogView, int mode) {
        if (dialogView == null) return;
        try {
            Button btnSound = dialogView.findViewById(R.id.btn_mode_sound);
            Button btnVibrate = dialogView.findViewById(R.id.btn_mode_vibrate);
            Button btnSilent = dialogView.findViewById(R.id.btn_mode_silent);

            int colorWhite = Color.parseColor("#FFFFFFFF");
            int colorDim = Color.parseColor("#B0BEC5");

            if (mode == AudioManager.RINGER_MODE_NORMAL) {
                if (btnSound != null) {
                    btnSound.setBackgroundResource(R.drawable.chip_mode_active);
                    btnSound.setTextColor(colorWhite);
                }
                if (btnVibrate != null) {
                    btnVibrate.setBackgroundResource(R.drawable.chip_mode_normal);
                    btnVibrate.setTextColor(colorDim);
                }
                if (btnSilent != null) {
                    btnSilent.setBackgroundResource(R.drawable.chip_mode_normal);
                    btnSilent.setTextColor(colorDim);
                }
            } else if (mode == AudioManager.RINGER_MODE_VIBRATE) {
                if (btnSound != null) {
                    btnSound.setBackgroundResource(R.drawable.chip_mode_normal);
                    btnSound.setTextColor(colorDim);
                }
                if (btnVibrate != null) {
                    btnVibrate.setBackgroundResource(R.drawable.chip_mode_active);
                    btnVibrate.setTextColor(colorWhite);
                }
                if (btnSilent != null) {
                    btnSilent.setBackgroundResource(R.drawable.chip_mode_normal);
                    btnSilent.setTextColor(colorDim);
                }
            } else {
                if (btnSound != null) {
                    btnSound.setBackgroundResource(R.drawable.chip_mode_normal);
                    btnSound.setTextColor(colorDim);
                }
                if (btnVibrate != null) {
                    btnVibrate.setBackgroundResource(R.drawable.chip_mode_normal);
                    btnVibrate.setTextColor(colorDim);
                }
                if (btnSilent != null) {
                    btnSilent.setBackgroundResource(R.drawable.chip_mode_active);
                    btnSilent.setTextColor(colorWhite);
                }
            }
        } catch (Throwable ignored) {}
    }

    @Override
    public void onClick() {
        super.onClick();
        try {
            final AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            LayoutInflater inflater = LayoutInflater.from(this);
            final View dialogView = inflater.inflate(R.layout.dialog_volume, null);

            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(dialogView);
            dialog.setCanceledOnTouchOutside(true);

            Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            if (am != null) {
                int activeMode = evaluateActiveMode(am);
                updateRingerModeUI(dialogView, activeMode);

                // Mode Sound button
                Button btnSound = dialogView.findViewById(R.id.btn_mode_sound);
                if (btnSound != null) {
                    btnSound.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            applyRingerMode(am, dialogView, AudioManager.RINGER_MODE_NORMAL);
                        }
                    });
                }

                // Mode Vibrate button
                Button btnVibrate = dialogView.findViewById(R.id.btn_mode_vibrate);
                if (btnVibrate != null) {
                    btnVibrate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            applyRingerMode(am, dialogView, AudioManager.RINGER_MODE_VIBRATE);
                        }
                    });
                }

                // Mode Silent button
                Button btnSilent = dialogView.findViewById(R.id.btn_mode_silent);
                if (btnSilent != null) {
                    btnSilent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            applyRingerMode(am, dialogView, AudioManager.RINGER_MODE_SILENT);
                        }
                    });
                }

                // 1. Media Volume (STREAM_MUSIC = 3)
                setupContinuousSlider(dialogView, am, AudioManager.STREAM_MUSIC, R.id.seekbar_media, R.id.text_media_pct);

                // 2. Ring & Notification Volume (STREAM_RING = 2)
                setupContinuousSlider(dialogView, am, AudioManager.STREAM_RING, R.id.seekbar_ring, R.id.text_ring_pct);

                // 3. Call Volume (STREAM_VOICE_CALL = 0)
                setupContinuousSlider(dialogView, am, AudioManager.STREAM_VOICE_CALL, R.id.seekbar_call, R.id.text_call_pct);

                // 4. Alarm Volume (STREAM_ALARM = 4)
                setupContinuousSlider(dialogView, am, AudioManager.STREAM_ALARM, R.id.seekbar_alarm, R.id.text_alarm_pct);

                // Quick Mute Buttons
                setupQuickMute(dialogView, am, AudioManager.STREAM_MUSIC, R.id.btn_mute_media, R.id.seekbar_media, R.id.text_media_pct, 1);
                setupQuickMute(dialogView, am, AudioManager.STREAM_RING, R.id.btn_mute_ring, R.id.seekbar_ring, R.id.text_ring_pct, 2);
                setupQuickMute(dialogView, am, AudioManager.STREAM_VOICE_CALL, R.id.btn_mute_call, R.id.seekbar_call, R.id.text_call_pct, 3);
                setupQuickMute(dialogView, am, AudioManager.STREAM_ALARM, R.id.btn_mute_alarm, R.id.seekbar_alarm, R.id.text_alarm_pct, 4);
            }

            // [⚙] Settings button
            Button btnSettings = dialogView.findViewById(R.id.btn_settings);
            if (btnSettings != null) {
                btnSettings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(Settings.ACTION_SOUND_SETTINGS);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            dialog.dismiss();
                        } catch (Throwable ignored) {}
                    }
                });
            }

            // [✕] Close button
            Button btnClose = dialogView.findViewById(R.id.btn_close);
            if (btnClose != null) {
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }

            // More Expand / Collapse toggle
            final Button btnToggleMore = dialogView.findViewById(R.id.btn_toggle_more);
            final View layoutMoreSliders = dialogView.findViewById(R.id.layout_more_sliders);
            if (btnToggleMore != null && layoutMoreSliders != null) {
                btnToggleMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (layoutMoreSliders.getVisibility() == View.VISIBLE) {
                            layoutMoreSliders.setVisibility(View.GONE);
                            btnToggleMore.setText("⌄ MORE SLIDERS");
                        } else {
                            layoutMoreSliders.setVisibility(View.VISIBLE);
                            btnToggleMore.setText("⌃ LESS SLIDERS");
                        }
                    }
                });
            }

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface d) {
                    updateVolumeTile();
                }
            });

            showDialog(dialog);

        } catch (Throwable ignored) {}
    }

    private void setupContinuousSlider(View dialogView, AudioManager am, int streamType, int seekbarId, int textPctId) {
        try {
            SeekBar seekBar = dialogView.findViewById(seekbarId);
            TextView textPct = dialogView.findViewById(textPctId);

            int currentVol = am.getStreamVolume(streamType);
            int maxVol = am.getStreamMaxVolume(streamType);

            if (streamType == AudioManager.STREAM_RING && evaluateActiveMode(am) == AudioManager.RINGER_MODE_SILENT) {
                currentVol = 0;
            }

            int pct = (maxVol > 0) ? Math.round((currentVol * 100.0f) / maxVol) : 0;
            pct = Math.max(0, Math.min(100, pct));

            if (seekBar != null) {
                seekBar.setMax(100);
                seekBar.setProgress(pct);
                seekBar.setOnSeekBarChangeListener(new VolumeSeekBarListener(am, streamType, textPct, maxVol));
            }
            if (textPct != null) {
                textPct.setText(pct + "%");
            }
        } catch (Throwable ignored) {}
    }

    private void setupQuickMute(final View dialogView, final AudioManager am, final int streamType, int btnId, final int seekbarId, final int textPctId, final int slot) {
        Button btnMute = dialogView.findViewById(btnId);
        if (btnMute != null) {
            btnMute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int currentVol = am.getStreamVolume(streamType);
                        int maxVol = am.getStreamMaxVolume(streamType);
                        SeekBar seekBar = dialogView.findViewById(seekbarId);
                        TextView textPct = dialogView.findViewById(textPctId);

                        if (currentVol > 0) {
                            // Save volume and mute
                            setSavedVol(slot, currentVol);
                            am.setStreamVolume(streamType, 0, 0);
                            if (streamType == AudioManager.STREAM_RING) {
                                am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
                                applyRingerMode(am, dialogView, AudioManager.RINGER_MODE_SILENT);
                            }
                            if (seekBar != null) seekBar.setProgress(0);
                            if (textPct != null) textPct.setText("0%");
                        } else {
                            // Restore previous volume
                            int restore = getSavedVol(slot, maxVol);
                            am.setStreamVolume(streamType, restore, 0);
                            if (streamType == AudioManager.STREAM_RING) {
                                am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, restore, 0);
                                applyRingerMode(am, dialogView, AudioManager.RINGER_MODE_NORMAL);
                            }
                            int pct = (maxVol > 0) ? Math.round((restore * 100.0f) / maxVol) : 50;
                            if (seekBar != null) seekBar.setProgress(pct);
                            if (textPct != null) textPct.setText(pct + "%");
                        }
                    } catch (Throwable ignored) {}
                }
            });
        }
    }

    private static void setSavedVol(int slot, int val) {
        if (slot == 1) sSavedMediaVol = val;
        else if (slot == 2) sSavedRingVol = val;
        else if (slot == 3) sSavedCallVol = val;
        else if (slot == 4) sSavedAlarmVol = val;
    }

    private static int getSavedVol(int slot, int maxVol) {
        int val = -1;
        if (slot == 1) val = sSavedMediaVol;
        else if (slot == 2) val = sSavedRingVol;
        else if (slot == 3) val = sSavedCallVol;
        else if (slot == 4) val = sSavedAlarmVol;
        if (val <= 0) return Math.max(1, maxVol / 2);
        return Math.min(maxVol, val);
    }

    private static void applyRingerMode(AudioManager am, View dialogView, int mode) {
        try {
            updateRingerModeUI(dialogView, mode);
            if (mode == AudioManager.RINGER_MODE_SILENT) {
                am.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
                am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            } else if (mode == AudioManager.RINGER_MODE_VIBRATE) {
                am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            } else {
                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                int ringVol = am.getStreamVolume(AudioManager.STREAM_RING);
                if (ringVol == 0) {
                    int maxRing = am.getStreamMaxVolume(AudioManager.STREAM_RING);
                    int half = Math.max(1, maxRing / 2);
                    am.setStreamVolume(AudioManager.STREAM_RING, half, 0);
                    am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, half, 0);
                }
            }
            syncRingSeekBar(dialogView, am);
        } catch (Throwable ignored) {}
    }
}
