package com.example.mumu.warehousecheckcar.utils;

/***
 *created by mumu
 * 对比工具
 *on 2019/11/18
 */
public class CompareUtil {
    /**
     * 比较两个字符串的大小，按字母的ASCII码比较
     * true为pre大
     * false为next大
     *
     * @param pre
     * @param next
     * @return
     */
    public static boolean isMoreThan(String pre, String next) {
        if (null == pre || null == next || "".equals(pre) || "".equals(next)) {
            return false;
        }
        char[] c_pre = pre.toCharArray();
        char[] c_next = next.toCharArray();
        int minSize = Math.min(c_pre.length, c_next.length);
        for (int i = 0; i < minSize; i++) {
            if ((int) c_pre[i] > (int) c_next[i]) {
                return true;
            } else if ((int) c_pre[i] < (int) c_next[i]) {
                return false;
            }
        }
        if (c_pre.length > c_next.length) {
            return true;
        }
        return false;
    }
}
