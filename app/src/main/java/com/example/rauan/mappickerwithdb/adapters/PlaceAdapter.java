package com.example.rauan.mappickerwithdb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.rauan.mappickerwithdb.R;
import com.example.rauan.mappickerwithdb.model.PlacePickModel;

import java.util.List;

/**
 * Created by Rauan on 031 31.03.2017.
 */

public class PlaceAdapter extends BaseAdapter {
    private Context context;
    private List<PlacePickModel> placePickModelList;



    public PlaceAdapter(Context context, List<PlacePickModel> placePickModelList) {
        this.context = context;
        this.placePickModelList = placePickModelList;
    }

    @Override
    public int getCount() {
        return placePickModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return placePickModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.recycleview_row_layout, parent, false);
            holder = new ViewHolder();
            holder.tvName = (TextView)convertView.findViewById(R.id.tvPlaceTitle);
            holder.tvPlaceLatitude = (TextView)convertView.findViewById(R.id.tvLatitudePlace);
            holder.tvPlaceLongitude = (TextView)convertView.findViewById(R.id.tvLongitudePlace);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.tvName.setText(placePickModelList.get(position).getName());
        holder.tvPlaceLatitude.setText(placePickModelList.get(position).getLatitude());
        holder.tvPlaceLongitude.setText(placePickModelList.get(position).getLongitude());

        return convertView;


    }

    private class ViewHolder {
        TextView tvName;
        TextView tvPlaceLatitude;
        TextView tvPlaceLongitude;
    }
}
