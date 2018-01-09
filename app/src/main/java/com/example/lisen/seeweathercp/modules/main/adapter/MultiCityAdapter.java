package com.example.lisen.seeweathercp.modules.main.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.lisen.seeweathercp.R;
import com.example.lisen.seeweathercp.base.BaseViewHolder;
import com.example.lisen.seeweathercp.common.utils.SharedPreferencesUtil;
import com.example.lisen.seeweathercp.common.utils.Util;
import com.example.lisen.seeweathercp.component.PLog;
import com.example.lisen.seeweathercp.modules.main.domain.Weather;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;

/**
 * Created by lisen on 2017/12/29.
 */

public class MultiCityAdapter extends RecyclerView.Adapter<MultiCityAdapter.MultiCityViewHolder> {

    private Context mContext;
    private List<Weather> mWeatherList;
    private onMultiCityClick mOnMultiCityClick;

    public void setOnMultiCityClick(onMultiCityClick multiCityClick) {
        this.mOnMultiCityClick = multiCityClick;
    }

    public MultiCityAdapter(List<Weather> weatherList) {
        mWeatherList = weatherList;
    }

    @Override
    public MultiCityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new MultiCityViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multi_city, parent, false));
    }

    @Override
    public void onBindViewHolder(MultiCityViewHolder holder, int position) {
        holder.bind(mWeatherList.get(position));
        holder.itemView.setOnLongClickListener(v -> {
            mOnMultiCityClick.longClick(mWeatherList.get(holder.getAdapterPosition()).basic.city);
            return true;
        });
        holder.itemView.setOnClickListener(v -> mOnMultiCityClick.click(mWeatherList.get(holder.getAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return mWeatherList.size();
    }

    public boolean isEmpty() {
        return 0 == mWeatherList.size();
    }

    class MultiCityViewHolder extends BaseViewHolder<Weather> {

        @BindView(R.id.dialog_city)
        TextView mDialogCity;
        @BindView(R.id.dialog_icon)
        ImageView mDialogIcon;
        @BindView(R.id.dialog_temp)
        TextView mDialogTemp;
        @BindView(R.id.cardView)
        CardView mCardView;

        public MultiCityViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bind(Weather weather) {
            try {
                mDialogCity.setText(Util.safeText(weather.basic.city));
                mDialogTemp.setText(String.format("%s℃", weather.now.tmp));
            } catch (NullPointerException e) {
                PLog.e(e.getMessage());
            }

            Glide.with(mContext)
                    .load(SharedPreferencesUtil.getInstance().getInt(weather.now.cond.txt, R.mipmap.none))
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            mDialogIcon.setImageBitmap(resource);
                            mDialogIcon.setColorFilter(Color.WHITE);
                        }
                    });
            int code = Integer.valueOf(weather.now.cond.code);
            new CardCityHelper().applyStatus(code, weather.basic.city, mCardView);
        }
    }

    public interface onMultiCityClick {
        void longClick(String city);

        void click(Weather weather);
    }
}
