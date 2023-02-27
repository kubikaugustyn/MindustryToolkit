package MindustryToolkit;

import arc.struct.Seq;

import java.util.Iterator;

public class Utils {
    /**
     * !!!Not working function!!!
     * Advanced version of java.lang.String.split
     *
     * @param source    Source string to be split
     * @param splitChar Character(s) to be split by
     * @param avoidChar Character(s) not to split by (e.g. escaped ones)
     * @return Split string
     * <p>
     * Example:
     * String[] result = Utils.advancedSplit("Hello\\, world, I'm fine", ",", "\\,");
     * // Should be result = {"Hello\\, world", " I'm fine"}
     * </p>
     */
    public static String[] advancedSplit(String source, String splitChar, String avoidChar) {
        Seq<String> resultSeq = new Seq<>();

        String[] normalSplit = source.split(splitChar);
        String[] avoidPartsTmp = avoidChar.split(splitChar);
        String[] avoidParts = new String[avoidPartsTmp.length - 1];
        System.arraycopy(avoidPartsTmp, 0, avoidParts, 0, avoidPartsTmp.length - 1);
        String avoidPrefix = String.join(splitChar, avoidParts);
        StringBuilder curr = new StringBuilder(); // Idk IDEA
        for (String part : normalSplit) {
            if (curr.toString().endsWith(avoidPrefix)) {
                curr.append(splitChar);
                curr.append(part);
            } else {
                if (curr.length() > 0) {
                    resultSeq.add(curr.toString());
                    curr = new StringBuilder();
                }
                curr.append(part);
            }
        }
        if (curr.length() > 0) resultSeq.add(curr.toString());


        String[] result = new String[resultSeq.size];
        Iterator<String> iter = resultSeq.iterator();
        for (int i = 0; i < resultSeq.size; i++) result[i] = iter.next();
        return result;
    }
}
