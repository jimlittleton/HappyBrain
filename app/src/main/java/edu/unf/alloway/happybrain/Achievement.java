package edu.unf.alloway.happybrain;

/**
 * Used to create an achievement as an object. This way, an
 * achievement can easily be added/removed/displayed/updated
 * using a RecyclerView.
 */
public class Achievement {

    private boolean mIsLocked = true;
    private String mAchievementTitle;
    private String mAchievementDesc;

    // This is the default number of points required
    // to unlock an achievement.
    private static final int DEFAULT_UNLOCK_POINTS = 100;
    private int mUnlockPoints;

    /**
     * Constructs a new achievement with the number of unlock points specified.
     * If you want to use the default number of points,
     * use {@link #Achievement(String, String)}
     *
     * @param achievementTitle The title of the achievement.
     * @param achievementDesc  The description of the achievement
     * @param unlockPoints     The number of points required to unlock this achievement.
     */
    public Achievement(String achievementTitle, String achievementDesc, int unlockPoints) {
        mAchievementTitle = achievementTitle;
        mAchievementDesc = achievementDesc;
        mUnlockPoints = unlockPoints;
    }

    /**
     * Constructs a new achievement where the number of points to unlock it
     * defaults to {@link #DEFAULT_UNLOCK_POINTS}.
     * */
    public Achievement(String achievementTitle, String achievementDesc) {
        this(achievementTitle, achievementDesc, DEFAULT_UNLOCK_POINTS);
    }

    /**
     * Constructs a new achievement with empty properties.
     * */
    public Achievement(){
        this(null, null, 0);
    }

    public boolean isLocked() {
        return mIsLocked;
    }

    public String getTitle() {
        return mAchievementTitle;
    }

    public String getDescription() {
        return mAchievementDesc;
    }

    public int getUnlockPoints() {
        return mUnlockPoints;
    }

    public void setLocked(boolean isLocked) {
        mIsLocked = isLocked;
    }

    public void setTitle(String title) {
        mAchievementTitle = title;
    }

    public void setDescription(String description) {
        mAchievementDesc = description;
    }

    /**
     * Sets the number of points required to unlock the achievement
     * */
    public void setPoints(int unlockPoints) {
        mUnlockPoints = unlockPoints;
    }
}
