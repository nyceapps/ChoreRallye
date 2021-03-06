package com.nyceapps.chorerallye.member;

import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nyceapps.chorerallye.R;
import com.nyceapps.chorerallye.chore.ChoreItem;
import com.nyceapps.chorerallye.main.MainActivity;
import com.nyceapps.chorerallye.main.RallyeData;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.List;

import static com.nyceapps.chorerallye.main.Constants.DISPLAY_MODE_LOG;
import static com.nyceapps.chorerallye.main.Constants.DISPLAY_MODE_RALLYE;

/**
 * Created by lugosi on 06.02.17.
 */
public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {
    private RallyeData data;
    private MainActivity callingActivity;

    public MembersAdapter(RallyeData pData, MainActivity pCallingActivity) {
        data = pData;
        callingActivity = pCallingActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MemberItem member = data.getMembers().get(position);

        holder.nameTextView.setText(member.getName());

        holder.imageImageView.setImageDrawable(member.getDrawable(callingActivity.getBaseContext()));
        holder.imageImageView.setTag(member);
        if (data.getSettings().isRunning()) {
            holder.imageImageView.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            break;
                        case DragEvent.ACTION_DRAG_ENTERED:
                            break;
                        case DragEvent.ACTION_DRAG_EXITED:
                            break;
                        case DragEvent.ACTION_DROP:
                            MemberItem member = (MemberItem) v.getTag();
                            ChoreItem chore = (ChoreItem) event.getLocalState();

                            callingActivity.updatePoints(member, chore);

                            break;
                        case DragEvent.ACTION_DRAG_ENDED:
                            break;
                        default:
                            break;
                    }

                    return true;
                }
            });
        }

        switch (data.getSettings().getDisplayMode()) {
            case DISPLAY_MODE_RALLYE:
                holder.valueBadgeView.setVisibility(View.VISIBLE);
                int memberPoints = data.getRace().getPoints(member);
                holder.valueBadgeView.setText(String.valueOf(memberPoints));
                break;
            case DISPLAY_MODE_LOG:
                holder.valueBadgeView.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public boolean updateList(List<MemberItem> pMembers) {
        if (pMembers.size() != data.getMembers().size() || !data.getMembers().containsAll(pMembers)) {
            data.setMembers(pMembers);
            notifyDataSetChanged();
            return true;
        }

        return false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public ImageView imageImageView;
        public BadgeView valueBadgeView;

        public ViewHolder(View v) {
            super(v);
            nameTextView = (TextView) v.findViewById(R.id.member_name);
            imageImageView = (ImageView) v.findViewById(R.id.member_image);
            valueBadgeView = new BadgeView(callingActivity, imageImageView);
            valueBadgeView.setBadgePosition(BadgeView.POSITION_BOTTOM_RIGHT);
            valueBadgeView.setText("0");
            valueBadgeView.show();
        }
    }

    @Override
    public int getItemCount() {
        return data.getMembers().size();
    }
}
