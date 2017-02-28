package com.nyceapps.chorerallye;

import android.content.ClipData;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.readystatesoftware.viewbadger.BadgeView;

import java.util.List;

/**
 * Created by lugosi on 06.02.17.
 */
public class RaceAdapter extends RecyclerView.Adapter<RaceAdapter.ViewHolder> implements Constants {
    private RallyeData data;
    private MainActivity callingActivity;

    public RaceAdapter(RallyeData pData, MainActivity pCallingActivity) {
        data = pData;
        callingActivity = pCallingActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.race_item_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MemberItem member = data.getMembers().get(position);

        int totalPoints = data.getRace().getTotalPoints();
        int memberPoints = data.getRace().getPoints(member);
        int memberPercentage = Utils.calculatePercentage(memberPoints, totalPoints);
        String memberText = String.format("%s (%d%%)", member.getName(), memberPercentage);

        holder.raceNameTextView.setText(memberText);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(memberPercentage, 0, 0, 0);
        holder.raceImageImageView.setLayoutParams(lp);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView raceNameTextView;
        public ImageView raceImageImageView;

        public ViewHolder(View v) {
            super(v);
            raceNameTextView = (TextView) v.findViewById(R.id.race_member_name);
            raceImageImageView = (ImageView) v.findViewById(R.id.race_member_image);
        }

    }

    @Override
    public int getItemCount() {
        return data.getMembers().size();
    }
}
