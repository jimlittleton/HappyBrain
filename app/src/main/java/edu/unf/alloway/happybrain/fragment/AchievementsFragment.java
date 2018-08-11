package edu.unf.alloway.happybrain.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import edu.unf.alloway.happybrain.Achievement;
import edu.unf.alloway.happybrain.AchievementAdapter;
import edu.unf.alloway.happybrain.R;

/**
 * Created by CamTheHam on 3/3/2018
 */

public class AchievementsFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    ArrayList<Achievement> achievements;
    AchievementAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_achievements, container, false);
        recyclerView = view.findViewById(R.id.achievement_recycler_view);

        // Adding dummy achievements. Note that this could all be done in one line
        // i.e. achievements.add(new Achievement(String, String, int));.
        // This is just an example of how an achievements can be created.
        Achievement achievementOne = new Achievement("Sample Achievement #1", "Yay! This has been unlocked!");
        Achievement achievementTwo = new Achievement("Sample Achievement #2", "Much achievement, so points, wow...", 200);
        Achievement achievementThree = new Achievement("Sample Achievement #3", "You must feel proud of yourself", 300);
        Achievement achievementFour = new Achievement("Sample Achievement #4", "You deserve some ice cream, or a day off!", 400);
        Achievement achievementFive = new Achievement("Sample Achievement #5", "High five", 500);
        Achievement achievementSix = new Achievement("Sample Achievement #6", "Unlocked when a condition is reached");
        Achievement achievementSeven = new Achievement("Sample Achievement #7", "Not enough points");
        Achievement achievementEight = new Achievement("Sample Achievement #8", "Try a little harder next time");
        Achievement achievementNine = new Achievement("Sample Achievement #9", "So close, yet so far...");

        // An example of how an empty achievement can be created.
        Achievement achievementTen = new Achievement();

        // An example of how an achievement can be set to unlocked
        // after an achievement object is created.
        achievementOne.setLocked(false);
        achievementTwo.setLocked(false);
        achievementThree.setLocked(false);
        achievementFour.setLocked(false);
        achievementFive.setLocked(false);

        // An example of how the number of points
        // can be set after an achievement object is created
        achievementSix.setPoints(600);
        achievementSeven.setPoints(700);
        achievementEight.setPoints(800);
        achievementNine.setPoints(900);

        // An example of how an empty achievement can be modified
        achievementTen.setTitle("Sample Achievement #10");
        achievementTen.setDescription("ALL YOUR BASE ARE BELONG TO US!");
        achievementTen.setPoints(1000);

        // Add the achievements to an ArrayList so they
        // can be displayed with a RecyclerView
        achievements = new ArrayList<>();
        achievements.add(achievementOne);
        achievements.add(achievementTwo);
        achievements.add(achievementThree);
        achievements.add(achievementFour);
        achievements.add(achievementFive);
        achievements.add(achievementSix);
        achievements.add(achievementSeven);
        achievements.add(achievementEight);
        achievements.add(achievementNine);
        achievements.add(achievementTen);

        LinearLayoutManager manager = new LinearLayoutManager(
                view.getContext(), LinearLayoutManager.VERTICAL, false);

        adapter = new AchievementAdapter(achievements, view.getContext());
        // Define the behavior for clicking on an achievement
        adapter.setOnClickListener(new AchievementAdapter.OnClickListener() {
            @Override
            public void onItemClick(int position) {
                // Here is an example usage of the AchievementAdapter's click listener.
                // Here, clicking on an achievement changes whether or not is unlocked.
                boolean isLocked = achievements.get(position).isLocked();

                if (isLocked) {
                    Toast.makeText(view.getContext(), "Achievement #" + (position + 1) + " has been unlocked",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(view.getContext(), "Achievement #" + (position + 1) + " has been locked",
                            Toast.LENGTH_SHORT).show();
                }
                achievements.get(position).setLocked(!isLocked);
                // Tell the RecyclerView that it needs to re-draw the achievement
                adapter.notifyItemChanged(position);
            }
        });
        // This stretches the layout to make sure that the entire
        // description is shown if it's more than one line
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        return view;
    }
}
