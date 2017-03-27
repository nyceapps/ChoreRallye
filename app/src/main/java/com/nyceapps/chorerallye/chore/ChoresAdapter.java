package com.nyceapps.chorerallye.chore;

import android.content.ClipData;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nyceapps.chorerallye.R;
import com.nyceapps.chorerallye.main.MainActivity;
import com.nyceapps.chorerallye.main.RallyeData;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.List;

import static com.nyceapps.chorerallye.main.Constants.DISPLAY_MODE_LOG;
import static com.nyceapps.chorerallye.main.Constants.DISPLAY_MODE_RALLYE;

/**
 * Created by lugosi on 06.02.17.
 */
public class ChoresAdapter extends RecyclerView.Adapter<ChoresAdapter.ViewHolder> {
    private RallyeData data;
    private MainActivity callingActivity;

    public ChoresAdapter(RallyeData pData, MainActivity pCallingActivity) {
        data = pData;
        callingActivity = pCallingActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chore_item_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChoreItem chore = data.getChores().get(position);

        holder.nameTextView.setText(chore.getName());

        holder.imageImageView.setImageDrawable(chore.getDrawable(callingActivity.getBaseContext()));
        holder.imageImageView.setTag(chore);
        if (data.getSettings().isRunning()) {
            holder.imageImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new ChoresDragShadowBuilder(v);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        v.startDragAndDrop(data, shadowBuilder, v.getTag(), 0);
                    } else {
                        v.startDrag(data, shadowBuilder, v.getTag(), 0);
                    }
                    return true;
                }
            });
        }

        switch (data.getSettings().getDisplayMode()) {
            case DISPLAY_MODE_RALLYE:
                holder.valueBadgeView.setVisibility(View.VISIBLE);
                holder.valueBadgeView.setText(String.valueOf(chore.getValue()));
                break;
            case DISPLAY_MODE_LOG:
                holder.valueBadgeView.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public void updateList(List<ChoreItem> pChores) {
        if (pChores.size() != data.getChores().size() || !data.getChores().containsAll(pChores)) {
            data.setChores(pChores);
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public ImageView imageImageView;
        public BadgeView valueBadgeView;

        public ViewHolder(View v) {
            super(v);
            nameTextView = (TextView) v.findViewById(R.id.chore_name);
            imageImageView = (ImageView) v.findViewById(R.id.chore_image);
            valueBadgeView = new BadgeView(callingActivity, imageImageView);
            valueBadgeView.setBadgePosition(BadgeView.POSITION_BOTTOM_RIGHT);
            valueBadgeView.setText("0");
            valueBadgeView.show();
        }

    }

    private static class ChoresDragShadowBuilder extends View.DragShadowBuilder {
        private Point dragShadowScaleFactor;

        public ChoresDragShadowBuilder(View v) {
            super(v);
        }

        @Override
        public void onProvideShadowMetrics (Point size, Point touch) {
            int width = getView().getWidth() * 2;
            int height = getView().getHeight() * 2;

            size.set(width, height);
            dragShadowScaleFactor = size;

            touch.set(width / 2, height / 2);
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            float viewWidth = getView().getWidth();
            float viewHeight = getView().getHeight();
            canvas.scale(dragShadowScaleFactor.x / viewWidth, dragShadowScaleFactor.y / viewHeight);
            getView().draw(canvas);
        }
    }

    @Override
    public int getItemCount() {
        return data.getChores().size();
    }
}
