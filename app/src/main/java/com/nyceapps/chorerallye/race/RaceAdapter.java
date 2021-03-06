package com.nyceapps.chorerallye.race;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nyceapps.chorerallye.R;
import com.nyceapps.chorerallye.main.MainActivity;
import com.nyceapps.chorerallye.main.RallyeData;
import com.nyceapps.chorerallye.main.Utils;
import com.nyceapps.chorerallye.member.MemberItem;

/**
 * Created by lugosi on 06.02.17.
 */
public class RaceAdapter extends RecyclerView.Adapter<RaceAdapter.ViewHolder> {
    private static final String TAG = RaceAdapter.class.getSimpleName();

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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MemberItem member = data.getMembers().get(position);

        int totalPoints = data.getRace().getTotalPoints();
        int memberPoints = data.getRace().getPoints(member);
        int memberPercentage = Utils.calculatePercentage(memberPoints, totalPoints);
        String memberText = String.format("%s (%d%%)", member.getName(), memberPercentage);

        holder.raceNameTextView.setText(memberText);

        int onePercent = (raceViewWidth - maxMemberTextWidth) / 100;
        final int newleftMargin = onePercent * memberPercentage;

        final LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.raceImageImageView.getLayoutParams();
        final int oldLeftMargin = layoutParams.leftMargin;

        final int leftMarginDiff = newleftMargin - oldLeftMargin;

        Animation raceAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                int animationStep = (int) (leftMarginDiff * interpolatedTime);
                int animationLeftMargin = oldLeftMargin + animationStep;
                layoutParams.leftMargin = animationLeftMargin;
                holder.raceImageImageView.setLayoutParams(layoutParams);
            }
        };
        raceAnimation.setDuration(500);
        holder.raceImageImageView.startAnimation(raceAnimation);
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
