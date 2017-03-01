package com.nyceapps.chorerallye;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by lugosi on 06.02.17.
 */
public class RaceAdapter extends RecyclerView.Adapter<RaceAdapter.ViewHolder> implements Constants {
    //private static final String TAG = RaceAdapter.class.getSimpleName();

    private MainActivity callingActivity;

    private RallyeData data;

    private int raceViewWidth;

    private int maxMemberTextWidth;

    public RaceAdapter(RallyeData pData, MainActivity pCallingActivity) {
        data = pData;
        callingActivity = pCallingActivity;

        maxMemberTextWidth = 0;
    }

    public void setMaxMemberTextWidth(int pMaxMemberTextWidth) {
        maxMemberTextWidth = pMaxMemberTextWidth;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.race_item_layout, parent, false);
        raceViewWidth = parent.getWidth();
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

        int onePercent = (raceViewWidth - maxMemberTextWidth) / 100;
        int leftMargin = onePercent * memberPercentage;

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(leftMargin, 0, 0, 0);
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
