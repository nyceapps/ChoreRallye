package com.nyceapps.chorerallye;

import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static com.nyceapps.chorerallye.Constants.CONTEXT_MENU_ACTION_EDIT;
import static com.nyceapps.chorerallye.Constants.CONTEXT_MENU_ACTION_REMOVE;

/**
 * Created by lugosi on 06.02.17.
 */
public class MembersListAdapter extends RecyclerView.Adapter<MembersListAdapter.ViewHolder> {
    private RallyeData data;
    private MembersListActivity callingActivity;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MembersListAdapter(RallyeData pData, MembersListActivity pCallingActivity) {
        data = pData;
        callingActivity = pCallingActivity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.members_list_item_layout, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MemberItem member = data.getMembers().get(position);

        holder.nameTextView.setText(member.getName());

        holder.imageImageView.setImageDrawable(member.getDrawable(callingActivity.getBaseContext()));
        holder.imageImageView.setTag(member);
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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        // each data item is just a string in this case
        public TextView nameTextView;
        public ImageView imageImageView;

        public ViewHolder(View v) {
            super(v);
            nameTextView = (TextView) v.findViewById(R.id.member_list_name);
            imageImageView = (ImageView) v.findViewById(R.id.member_list_image);
            v.setOnClickListener(this);
            v.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            MemberItem member = (MemberItem) imageImageView.getTag();
            callingActivity.editMember(member);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(nameTextView.getText());
            MenuItem menuItemEdit = menu.add(Menu.NONE, CONTEXT_MENU_ACTION_EDIT, Menu.NONE, R.string.list_context_menu_edit);
            menuItemEdit.setOnMenuItemClickListener(this);
            MenuItem menuItemRemove = menu.add(Menu.NONE, CONTEXT_MENU_ACTION_REMOVE, Menu.NONE, R.string.list_context_menu_remove);
            menuItemRemove.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            MemberItem member = (MemberItem) imageImageView.getTag();
            switch (item.getItemId()) {
                case CONTEXT_MENU_ACTION_EDIT:
                    callingActivity.editMember(member);
                    break;
                case CONTEXT_MENU_ACTION_REMOVE:
                    callingActivity.removeMember(member);
                    break;
                default:
            }
            return true;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return data.getMembers().size();
    }
}
