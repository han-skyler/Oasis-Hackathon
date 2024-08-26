package com.example.white_butterfly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class HospitalAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<HospitalData> hospital;

    public HospitalAdapter(Context context, ArrayList<HospitalData> data) {
        mContext = context;
        hospital = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return hospital.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public HospitalData getItem(int position) {
        return hospital.get(position);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.item_hospital, null);

        ImageView imageView = view.findViewById(R.id.imageView);
        TextView hospital_name = view.findViewById(R.id.text_name);
        TextView hospital_sub = view.findViewById(R.id.text_sub);
        TextView hospital_adr = view.findViewById(R.id.text_address);

        imageView.setImageResource(hospital.get(position).getImage());
        hospital_name.setText(hospital.get(position).getHospital_name());
        hospital_sub.setText(hospital.get(position).getHospital_sub());
        hospital_adr.setText(hospital.get(position).getHospital_adr());

        return view;
    }
}