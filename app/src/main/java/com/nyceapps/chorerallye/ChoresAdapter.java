package com.nyceapps.chorerallye;

import android.content.ClipData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lugosi on 06.02.17.
 */
public class ChoresAdapter extends RecyclerView.Adapter<ChoresAdapter.ViewHolder> implements Constants {
    private RallyeData data;
    private MainActivity callingActivity;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ChoresAdapter(RallyeData pData, MainActivity pCallingActivity) {
        data = pData;
        callingActivity = pCallingActivity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chore_item_layout, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChoreItem chore = data.getChores().get(position);

        String choreText = chore.getName();
        if (chore.getValue() > 0) {
            choreText += " (" + chore.getValue() + ")";
        }
        holder.nameTextView.setText(choreText);

        holder.imageImageView.setImageDrawable(chore.getDrawable(callingActivity.getBaseContext()));
        holder.imageImageView.setTag(chore);
        holder.imageImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    v.startDragAndDrop(data, shadowBuilder, v.getTag(), 0);
                } else {
                    v.startDrag(data, shadowBuilder, v.getTag(), 0);
                }
                return true;
            }
        });
    }

    public void updateList(List<ChoreItem> pChores) {
        if (pChores.size() != data.getChores().size() || !data.getChores().containsAll(pChores)) {
            data.setChores(pChores);
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
            nameTextView = (TextView) v.findViewById(R.id.chore_name);
            imageImageView = (ImageView) v.findViewById(R.id.chore_image);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return data.getChores().size();
    }
}
