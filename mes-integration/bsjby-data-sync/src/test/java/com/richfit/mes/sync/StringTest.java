package com.richfit.mes.sync;

/**
 * @author gaol
 * @date 2023/2/14
 * @apiNote
 */
public class StringTest {

    public static void main(String args[]) {

        String s = "BOMCO_JM\\AH160101010901\\合格证\\2064010726.jpg";
        s = s.replace("\\", "\\\\");

        System.out.println(s);

    }

}
