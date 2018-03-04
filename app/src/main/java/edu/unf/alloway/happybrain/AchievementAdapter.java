package edu.unf.alloway.happybrain;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * RecyclerView adapter used in {@link AchievementsFragment}.
 * View {@link R.layout#list_achievements} to see how an
 * achievement is displayed.
 */

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {

    private ArrayList<Achievement> mAchievements;
    private Context mContext;
    private OnClickListener mClickListener;

    public AchievementAdapter(ArrayList<Achievement> achievements, Context context) {
        mAchievements = achievements;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.list_achievements, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String title = mAchievements.get(position).getTitle();
        String description = mAchievements.get(position).getDescription();
        int pointsToUnlock = mAchievements.get(position).getUnlockPoints();
        boolean isLocked = mAchievements.get(position).isLocked();

        holder.achievementTitle.setText(title);
        holder.achievementDesc.setText(description);
        holder.pointsToUnlock.setText(mContext.getString(R.string.achievement_unlock_points, pointsToUnlock));

        // If the achievement is locked, the icon will be a 'locked' image and
        // the text will be greyed out (except for the number of points)
        if (isLocked) {
            holder.status.setImageResource(R.drawable.ic_achievement_locked);
            holder.achievementTitle.setTextColor(
                    ContextCompat.getColor(mContext, R.color.achievementLocked));
            holder.achievementDesc.setTextColor(
                    ContextCompat.getColor(mContext, R.color.achievementLocked));
            holder.imageCard.setCardBackgroundColor(
                    ContextCompat.getColor(mContext, R.color.achievementLocked));
            holder.pointsToUnlock.setTextColor(
                    ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
        }
        // If the achievement is unlocked, the icon will be an 'unlocked' image,
        // the will should be the standard color, and the number of points required
        // will be grey
        else {
            holder.status.setImageResource(R.drawable.ic_achievement_unlocked);
            holder.achievementTitle.setTextColor(
                    ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
            holder.achievementDesc.setTextColor(
                    ContextCompat.getColor(mContext, R.color.colorPrimary));
            holder.imageCard.setCardBackgroundColor(
                    ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
            holder.pointsToUnlock.setTextColor(
                    ContextCompat.getColor(mContext, R.color.achievementLocked)
            );
        }
    }

    @Override
    public int getItemCount() {
        return mAchievements.size();
    }

    /**
     * RecyclerView doesn't have a click listener so we can use this to set
     * a custom one with defined behavior.
     */
    public void setOnClickListener(OnClickListener clickListener) {
        mClickListener = clickListener;
    }

    public interface OnClickListener {
        void onItemClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView container;
        TextView achievementTitle;
        TextView achievementDesc;
        TextView pointsToUnlock;
        ImageView status;
        CardView imageCard;

        public ViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.achievement_list_container);
            container.setOnClickListener(this);
            imageCard = itemView.findViewById(R.id.card_view);
            achievementTitle = itemView.findViewById(R.id.achievement_title);
            achievementDesc = itemView.findViewById(R.id.achievement_description);
            pointsToUnlock = itemView.findViewById(R.id.achievement_unlock_points);
            status = itemView.findViewById(R.id.achievement_status);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(getAdapterPosition());
        }
    }
}
