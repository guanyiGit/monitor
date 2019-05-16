package com.soholy.cb;

import com.soholy.cb.utils.ByteUtils;

public class Main {

    public static void main(String[] args) throws Exception {
        byte[] bytes = ByteUtils.int2byte(1200);
        System.out.println(ByteUtils.byteTohex(bytes));

        byte[] out = new byte[2];
        ByteUtils.intTobyte2(1200, out,0 );
        System.out.println(ByteUtils.byteTohex(out));
    }
}
