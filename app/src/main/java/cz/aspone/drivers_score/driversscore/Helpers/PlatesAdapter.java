package cz.aspone.drivers_score.driversscore.Helpers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import cz.aspone.drivers_score.driversscore.BO.Plate;
import cz.aspone.drivers_score.driversscore.R;

/**
 *  Created by ondrej.vondra on 18.01.2018.
 */
public class PlatesAdapter extends RecyclerView.Adapter<PlatesAdapter.PlatesViewHolder> {

    private ArrayList<Plate> plates;
    private static ClickListener clickListener;

    public PlatesAdapter(ArrayList<Plate> plates) {
        this.plates = plates;
    }

    public void swap(ArrayList<Plate> plates) {
        if (this.plates != null) {
            this.plates.clear();
            this.plates.addAll(plates);
        } else {
            this.plates = plates;
        }
        notifyDataSetChanged();
    }

    @Override
    public PlatesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());

        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.list_plate_layout, parent, false);
        return new PlatesViewHolder(mainGroup) {
            @Override
            public String toString() {
                return super.toString();
            }
        };
    }

    @Override
    public void onBindViewHolder(PlatesViewHolder holder, int position) {

        Plate plate = plates.get(position);
        holder.ivCar.setImageResource(General.getImageResource(plate.getBodyType()));
        holder.ivCar.setColorFilter(General.parseColor(plate.getCarColor()));
        holder.tvPlate.setText(plate.getPlateNumber());

        if (plate.getScoreAvg() != 0) {
            Map<String, Integer> scores = General.getScoreResources(plate.getScoreAvg());
            holder.ivScore.setImageResource(scores.get("image"));
            holder.ivScore.setColorFilter(scores.get("color"));
        }
        double nMyScore = plate.getMyScore();
        if (nMyScore != 0) {
            Map<String, Integer> myScore = General.getScoreResources(nMyScore);
            holder.ivMyScore.setImageResource(myScore.get("image"));
            holder.ivMyScore.setColorFilter(myScore.get("color"));
        }
        holder.tvMaker.setText(plate.getMaker());

        holder.plate = plate;
    }

    @Override
    public int getItemCount() {
        return (null != plates ? plates.size() : 0);
    }

    class PlatesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView ivCar, ivScore, ivMyScore;
        TextView tvPlate, tvMaker;
        Plate plate;

        PlatesViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            ivCar = (ImageView) itemView.findViewById(R.id.ivCar);
            ivScore = (ImageView) itemView.findViewById(R.id.ivScore);
            tvPlate = (TextView) itemView.findViewById(R.id.tvPlate);
            ivMyScore = (ImageView) itemView.findViewById(R.id.ivMyScore);
            tvMaker = (TextView) itemView.findViewById(R.id.tvMaker);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(this.plate, v);
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onItemLongClick(this.plate, v);
            return true;
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        PlatesAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(Plate plate, View v);
        void onItemLongClick(Plate plate, View v);
    }
}
