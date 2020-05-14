package cn.kanyun.calc;

public class Constant {

    /**
     * 单次答题超时时间
     */
    public static final String KEY_TIMEOUT_ANSWER = "key_timeout_answer";


    /**
     * 解锁难度
     */
    public static final String KEY_UNLOCK_DIFFICULTY = "key_unlock_difficulty";

    /**
     * 计算数值上限
     */
    public static String KEY_NUMBER_UPPER_LIMIT = "key_number_upper_limit";

    /**
     * 运算符号
     */
    public static String KEY_OPERATOR_SYMBOLS = "operator_symbol";


    /**
     * sharedprefemces文件名
     */
    public static String SHARED_PREFENCES_NAME = "calc";

    /**
     * 数值上限类型
     * 以参与运算的数值为准
     * 以运算结果的数值为准
     */
    public static String KEY_NUMBER_UPPER_TYPE = "key_number_upper_type";

    /**
     * 背景音乐
     */
    public static String KEY_BACKGROUND_MUSIC = "key_background_music";

    /**
     * 背景音效
     */
    public static String KEY_BACKGROUND_SOUND = "key_background_sound";

    /**
     * 进入QuestionFragment的标志位(判断是从MainFragment进的还是从LoseFragment进的)
     */
    public static String KEY_FLAG_LAST_ERROR = "key_flag_last_error";

    /**
     * 已获得的奖励
     */
    public static String HAS_UNLOCK_REWARD = "has_unlock_reward";

    /**
     * 待切图的Bundle传递时的Key
     * 奖励碎片
     */
    public static String KEY_CHIP_IMG_BUNDLE_NAME = "key_chip_img_bundle_name";

    /**
     * 待切图的Bundle传递时的Key
     * 奖励原图（完整的图）
     */
    public static final String KEY_SRC_IMG_BUNDLE_NAME = "key_src_img_bundle_name";

    /**
     * 待切图的路径传递时的Key
     * 奖励原图（完整的图）
     */
    public static final String KEY_SRC_IMG_BUNDLE_PATH = "key_src_img_bundle_path";

    /**
     * 拼图过程中,点击碎片，碎片被选中的颜色
     */
    public static String KEY_CLICK_IMG_COVER_COLOR = "#93b5cf73";

    /**
     * Activity间传递Bitmap的key
     */
    public static String KEY_ACTIVITY_SRC_IMAGE_BITMAP = "key_activity_src_image_bitmap";


    /**
     * 从PlayActivity跳转到ShowSrcImageViewActivity的请求码
     */
    public static final int KEY_PLAY_ACTIVITY_TO_SHOW_IMAGE_ACTIVITY_REQUEST_CODE = 1001;

}
