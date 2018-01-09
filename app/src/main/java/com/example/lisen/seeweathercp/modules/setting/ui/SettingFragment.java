package com.example.lisen.seeweathercp.modules.setting.ui;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.lisen.seeweathercp.R;
import com.example.lisen.seeweathercp.base.BaseApplication;
import com.example.lisen.seeweathercp.common.C;
import com.example.lisen.seeweathercp.common.utils.FileSizeUtil;
import com.example.lisen.seeweathercp.common.utils.FileUtil;
import com.example.lisen.seeweathercp.common.utils.RxUtil;
import com.example.lisen.seeweathercp.common.utils.SharedPreferencesUtil;
import com.example.lisen.seeweathercp.component.ImageLoader;
import com.example.lisen.seeweathercp.component.RxBus;
import com.example.lisen.seeweathercp.modules.main.domain.ChangeCityEvent;
import com.example.lisen.seeweathercp.modules.main.ui.MainActivity;
import com.example.lisen.seeweathercp.modules.service.AutoUpdateService;
import com.hugo.watcher.Watcher;
import com.squareup.haha.perflib.Main;

import org.w3c.dom.Text;

import java.io.File;
import java.util.Locale;

import io.reactivex.Observable;

/**
 * Created by lisen on 2018/1/8.
 */

public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    public static String TAG = SettingFragment.class.getSimpleName();
    private SharedPreferencesUtil mSharedPreferencesUtil;
    private Preference mChangeIcons;
    private Preference mChangeUpdate;
    private Preference mClearCache;
    private CheckBoxPreference mNotificationType;
    private CheckBoxPreference mAnimationOnOff;
    private CheckBoxPreference mWatcherSwitch;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);
        mSharedPreferencesUtil = SharedPreferencesUtil.getInstance();
        
        mChangeIcons = findPreference(SharedPreferencesUtil.CHANGE_ICONS);
        mChangeUpdate = findPreference(SharedPreferencesUtil.AUTO_UPDATE);
        mClearCache = findPreference(SharedPreferencesUtil.CLEAR_CACHE);
        
        mAnimationOnOff = (CheckBoxPreference) findPreference(SharedPreferencesUtil.ANIM_START);
        mNotificationType = (CheckBoxPreference) findPreference(SharedPreferencesUtil.NOTIFICATION_MODEL);
        mWatcherSwitch = (CheckBoxPreference) findPreference(SharedPreferencesUtil.WATCHER);
        
        mNotificationType.setChecked(SharedPreferencesUtil.getInstance().getNotificationModel() == Notification.FLAG_ONGOING_EVENT);
        mAnimationOnOff.setChecked(SharedPreferencesUtil.getInstance().getMainAnim());
        mWatcherSwitch.setChecked(SharedPreferencesUtil.getInstance().getWatcherSwitch());
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(getContext())) {
            mWatcherSwitch.setEnabled(false);
        }
        mChangeIcons.setSummary(getResources().getStringArray(R.array.icons)[mSharedPreferencesUtil.getIconType()]);
        mChangeUpdate.setSummary(mSharedPreferencesUtil.getAutoUpdate() == 0 ? "禁止刷新" : "每" + mSharedPreferencesUtil.getAutoUpdate() + "小时更新");
        mClearCache.setSummary(FileSizeUtil.getAutoFileOrFileSize(C.NET_CACHE));
        
        mChangeIcons.setOnPreferenceClickListener(this);
        mChangeUpdate.setOnPreferenceClickListener(this);
        mClearCache.setOnPreferenceClickListener(this);
        
        mNotificationType.setOnPreferenceChangeListener(this);
        mAnimationOnOff.setOnPreferenceChangeListener(this);
        mWatcherSwitch.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        
        if (mChangeIcons == preference) {
            showIconDialog();
        } else if (mClearCache == preference) {
            ImageLoader.clear(getActivity());
            Observable.just(FileUtil.delete(new File(C.NET_CACHE)))
                    .filter(aBoolean -> aBoolean)
                    .compose(RxUtil.io())
                    .doOnNext(aBoolean -> {
                        mClearCache.setSummary(FileSizeUtil.getAutoFileOrFileSize(C.NET_CACHE));
                        Snackbar.make(getView(), "缓存已清除", Snackbar.LENGTH_SHORT).show();
                    })
                    .subscribe();
        } else if (mChangeUpdate == preference) {
            showUpdateDialog();
        } else if (mWatcherSwitch == preference) {
            if (mWatcherSwitch.isChecked()) {
                Watcher.getInstance().start(BaseApplication.getAppContext());
            }
        }
        
        return true;
    }

    private void showIconDialog() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.dialog_icon, (ViewGroup) getActivity().findViewById(R.id.dialog_root));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(dialogLayout);
        final AlertDialog alertDialog = builder.create();

        LinearLayout layoutTypeOne = (LinearLayout) dialogLayout.findViewById(R.id.layout_one);
        layoutTypeOne.setClickable(true);
        RadioButton radioTypeOne = (RadioButton) dialogLayout.findViewById(R.id.radio_one);
        radioTypeOne.setClickable(false);
        LinearLayout layoutTypeTwo = (LinearLayout) dialogLayout.findViewById(R.id.layout_two);
        layoutTypeTwo.setClickable(true);
        RadioButton radioTypeTwo = (RadioButton) dialogLayout.findViewById(R.id.radio_two);
        radioTypeTwo.setClickable(false);
        TextView done = (TextView) dialogLayout.findViewById(R.id.done);

        alertDialog.show();

        switch (mSharedPreferencesUtil.getIconType()) {
            case 0:
                radioTypeOne.setChecked(true);
                radioTypeTwo.setChecked(false);
                break;
            case 1:
                radioTypeOne.setChecked(false);
                radioTypeTwo.setChecked(true);
        }

        layoutTypeOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioTypeOne.setChecked(true);
                radioTypeTwo.setChecked(false);
            }
        });

        layoutTypeTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioTypeOne.setChecked(false);
                radioTypeTwo.setChecked(true);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSharedPreferencesUtil.setIconType(radioTypeOne.isChecked() ? 0 : 1);
                String[] iconsText = getResources().getStringArray(R.array.icons);
                mChangeIcons.setSummary(radioTypeOne.isChecked() ? iconsText[0] : iconsText[1]);
                alertDialog.dismiss();
                Snackbar.make(getView(), "切换成功, 重启应用生效", Snackbar.LENGTH_INDEFINITE).setAction("重启", v1 -> {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                    RxBus.getDefault().post(new ChangeCityEvent());
                }).show();
            }
        });

    }

    private void showUpdateDialog() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.dialog_update, (ViewGroup) getActivity().findViewById(R.id.dialog_root));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(dialogLayout);
        final AlertDialog alertDialog = builder.create();

        final SeekBar mSeekBar = (SeekBar) dialogLayout.findViewById(R.id.time_seekbar);
        final TextView tvShowHour = (TextView) dialogLayout.findViewById(R.id.tv_showhour);
        TextView tvDone = (TextView) dialogLayout.findViewById(R.id.done);

        mSeekBar.setMax(24);
        mSeekBar.setProgress(mSharedPreferencesUtil.getAutoUpdate());
        tvShowHour.setText(String.format("每%s小时", mSeekBar.getProgress()));
        alertDialog.show();

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvShowHour.setText(String.format("每%s小时", mSeekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSharedPreferencesUtil.setAutoUpdate(mSeekBar.getProgress());
                mChangeUpdate.setSummary(mSharedPreferencesUtil.getAutoUpdate() == 0 ? "禁止刷新" : String.format(Locale.CHINA, "每%s小时更新", mSharedPreferencesUtil.getAutoUpdate()));
                getActivity().startService(new Intent(getActivity(), AutoUpdateService.class));
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mAnimationOnOff) {
            SharedPreferencesUtil.getInstance().setMainAnim((Boolean) newValue);
        } else if (mNotificationType == preference) {
            SharedPreferencesUtil.getInstance().setNotificationModel((boolean) newValue ? Notification.FLAG_ONGOING_EVENT : Notification.FLAG_AUTO_CANCEL);
        }
        return true;
    }
}
