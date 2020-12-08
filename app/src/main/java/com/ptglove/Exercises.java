package com.ptglove;

enum Positions {
    STRAIGHT  (new boolean[]{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false}, "straight_pos",
               new boolean[]{ true, true, true, true, true, true, true, true, true, true, true, true, true, true,false}, R.string.ex_straight),

    TABLETOP  (new boolean[]{false,false,false,false, true,false,false, true,false,false, true,false,false, true,false}, "tabletop_pos",
               new boolean[]{false,false, true, true, true, true, true, true, true, true, true, true, true, true,false}, R.string.ex_tabletop),

    HOOK      (new boolean[]{false,false, true, true,false, true, true,false, true, true,false, true, true,false,false}, "hook_pos",
               new boolean[]{false,false, true, true, true, true, true, true, true, true, true, true, true, true,false}, R.string.ex_hook),

    FLAT_FIST (new boolean[]{false,false,false, true, true,false, true, true,false, true, true,false, true, true,false}, "flat_fist_pos",
               new boolean[]{false,false, true, true, true, true, true, true, true, true, true, true, true, true,false}, R.string.ex_flat),

    FIST      (new boolean[]{false,false, true, true, true, true, true, true, true, true, true, true, true, true,false}, "fist_pos",
               new boolean[]{false,false, true, true, true, true, true, true, true, true, true, true, true, true,false}, R.string.ex_fist),

    THUMB_INDEX  (new boolean[]{ true, true, true, true, true,false,false,false,false,false,false,false,false,false,false}, "thumb_ind_pos",
                  new boolean[]{ true, true, true, true, true,false,false,false,false,false,false,false,false,false,false}, R.string.ex_t_ind),

    THUMB_MIDDLE (new boolean[]{ true, true,false,false,false, true, true, true,false,false,false,false,false,false,false}, "thumb_mid_pos",
                  new boolean[]{ true, true,false,false,false, true, true, true,false,false,false,false,false,false,false}, R.string.ex_t_mid),

    THUMB_RING   (new boolean[]{ true, true,false,false,false,false,false,false, true, true, true,false,false,false,false}, "thumb_ring_pos",
                  new boolean[]{ true, true,false,false,false,false,false,false, true, true, true,false,false,false,false}, R.string.ex_t_ring),

    THUMB_PINKIE (new boolean[]{ true, true, true,false,false,false,false,false,false,false,false, true, true, true,false}, "thumb_pink_pos",
                  new boolean[]{ true, true,false,false,false,false,false,false,false,false,false, true, true, true,false}, R.string.ex_t_pink),

    THUMB_PALM   (new boolean[]{ true, true, true,false,false,false,false,false,false,false,false, true, true, true,false}, "thumb_palm_pos",
                  new boolean[]{ true, true,false,false,false,false,false,false,false,false,false,false,false,false,false}, R.string.ex_t_palm),

    WRIST_F_FIST (new boolean[]{ true, true, true, true, true, true, true, true, true, true, true, true, true, true, true}, "wrist_f_fist_pos",
                  new boolean[]{false,false, true, true, true, true, true, true, true, true, true, true, true, true, true}, R.string.ex_w_f_fist),

    WRIST_B_FIST (new boolean[]{ true, true, true, true, true, true, true, true, true, true, true, true, true, true,false}, "wrist_b_fist_pos",
                  new boolean[]{false,false, true, true, true, true, true, true, true, true, true, true, true, true, true}, R.string.ex_w_b_fist),

    WRIST_F_STR  (new boolean[]{false,false,false,false,false,false,false,false,false,false,false,false,false,false, true}, "wrist_f_str_pos",
                  new boolean[]{ true, true, true, true, true, true, true, true, true, true, true, true, true, true, true}, R.string.ex_w_f_str),

    WRIST_B_STR  (new boolean[]{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false}, "wrist_b_str_pos",
                  new boolean[]{ true, true, true, true, true, true, true, true, true, true, true, true, true, true,false}, R.string.ex_w_b_str);

    public final boolean[] wantMoreBend, careAbout;
    public final String name;
    public final int strRes;

    private Positions(boolean[] moreBend, String in, boolean[] care, int str) {
        wantMoreBend = moreBend;
        name = in;
        careAbout = care;
        strRes = str;
    }
}

public enum Exercises {
    FOUR_POSITION  (new Positions[]{Positions.STRAIGHT, Positions.TABLETOP, Positions.STRAIGHT, Positions.HOOK,
                                    Positions.STRAIGHT, Positions.FLAT_FIST, Positions.STRAIGHT, Positions.FIST},
            new int[]{R.raw.fist_out, R.raw.tabletop_in, R.raw.tabletop_out, R.raw.hook_in,
                    R.raw.hook_out, R.raw.flatfist_in, R.raw.flatfist_out, R.raw.fist_out}, "four_pos_enable"),

    WRIST_FLEX  (new Positions[]{Positions.FIST, Positions.WRIST_F_FIST, Positions.WRIST_B_FIST, Positions.FIST,
            Positions.STRAIGHT, Positions.WRIST_F_STR, Positions.WRIST_B_STR, Positions.STRAIGHT},
            new int[]{R.raw.wrist_fist_start, R.raw.wrist_fist_f, R.raw.wrist_fist_b, R.raw.wrist_fist_end,
                    R.raw.wrist_str_start, R.raw.wrist_str_f, R.raw.wrist_str_b, R.raw.wrist_str_end}, "wrist_enable"),

    THUMB_TOUCH  (new Positions[]{Positions.THUMB_INDEX, Positions.THUMB_MIDDLE, Positions.THUMB_RING, Positions.THUMB_PINKIE, Positions.THUMB_PALM},
            new int[]{R.raw.finger_index, R.raw.finger_middle, R.raw.finger_ring, R.raw.finger_pinkie, R.raw.finger_thumb}, "thumb_enable");

    public final Positions[] positions;
    public final String name;
    public final int[] animations;

    private Exercises(Positions[] pos, int[] res, String name_in) {
        positions = pos;
        name = name_in;
        animations = res;
    }

    public Positions getStart() {
        return positions[0];
    }

    public int getLength() {
        return positions.length;
    }
}
