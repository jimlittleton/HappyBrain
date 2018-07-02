package edu.unf.alloway.happybrain;

/**
 * Contains a list of JSON keys returned by requests from the server
 */
public class JsonConstants {
    private JsonConstants() {}

    /**
     * The title of the post
     */
    public static final String TITLE = "Title";
    /**
     * The first bullet point
     */
    public static final String POINT_ONE = "Point_1";
    /**
     * The second bullet point
     */
    public static final String POINT_TWO = "Point_2";
    /**
     * The third bullet point
     */
    public static final String POINT_THREE = "Point_3";
    /**
     * The message of the post (The text under the 'Why' header)
     */
    public static final String MESSAGE = "Message";
    /**
     * Whether this page is a reflection page. This will
     * be a {@code boolean} value
     */
    public static final String REFLECT = "Reflect";
    /**
     * The url of the 'Read the study' link
     */
    public static final String URL = "URL";
    /**
     * The image in the post
     */
    public static final String FILENAME = "Filename";
}
