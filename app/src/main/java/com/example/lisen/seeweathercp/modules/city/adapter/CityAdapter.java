package com.example.lisen.seeweathercp.modules.city.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lisen.seeweathercp.R;
import com.example.lisen.seeweathercp.base.BaseViewHolder;
import com.example.lisen.seeweathercp.modules.main.adapter.AnimRecyclerViewAdapter;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by lisen on 2018/1/2.
 */

public class CityAdapter extends AnimRecyclerViewAdapter<CityAdapter.CityViewHolder> {

    private Context mContext;
    private ArrayList<String> mList;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public CityAdapter(Context context, ArrayList<String> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CityViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_city, parent, false));
    }

    @Override
    public void onBindViewHolder(CityViewHolder holder, int position) {
        holder.bind(mList.get(position));
        holder.mCardView.setOnClickListener(v -> mOnItemClickListener.onItemClick(v, position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int pos);
    }

    class CityViewHolder extends BaseViewHolder<String> {

        @BindView(R.id.item_city)
        TextView mItemCity;
        @BindView(R.id.cardView)
        CardView mCardView;

        public CityViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bind(String s) {
            mItemCity.setText(s);
        }
    }
}
