package com.nyceapps.chorerallye;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lugosi on 06.02.17.
 */
public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {
    private static final float CORNER_RADIUS_FOR_MEMBER_IMAGE = 45;

    private RallyeData data;
    private MainActivity callingActivity;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MembersAdapter(RallyeData pData, MainActivity pCallingActivity) {
        data = pData;
        callingActivity = pCallingActivity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item_layout, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MemberItem member = data.getMembers().get(position);

        holder.nameTextView.setText(member.getName());

        holder.imageImageView.setImageDrawable(member.getDrawable(callingActivity.getBaseContext()));
        holder.imageImageView.setTag(member);
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

    public void updateList(List<MemberItem> pMembers) {
        if (pMembers.size() != data.getMembers().size() || !data.getMembers().containsAll(pMembers)) {
            data.setMembers(pMembers);
            notifyDataSetChanged();
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView nameTextView;
        public ImageView imageImageView;

        public ViewHolder(View v) {
            super(v);
            nameTextView = (TextView) v.findViewById(R.id.member_name);
            imageImageView = (ImageView) v.findViewById(R.id.member_image);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return data.getMembers().size();
    }
}
