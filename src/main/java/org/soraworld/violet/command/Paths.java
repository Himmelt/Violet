package org.soraworld.violet.command;

import java.util.StringJoiner;

/**
 * 命令树.
 */
public class Paths {

    private int current;
    private final int length;
    private final boolean keep;
    private final String[] paths;

    /**
     * 实例化命令树.
     * 如果 {@code keep == false} 每个节中的 ' ' 和 ':' 都会被替换成 '_'.
     *
     * @param keep  是否保留原始内容
     * @param paths 命令树
     */
    public Paths(boolean keep, String... paths) {
        for (int i = 0; !keep && i < paths.length; i++) {
            paths[i] = paths[i].replace(' ', '_')
                    .replace(':', '_').toLowerCase();
        }
        this.paths = paths;
        this.length = this.paths.length;
        this.current = 0;
        this.keep = keep;
    }

    /**
     * 实例化命令树.
     * 如果 {@code keep == false} 每个节中的 ':' 都会被替换成 '_'.
     *
     * @param keep 是否保留原始内容
     * @param path 命令树字符串
     */
    public Paths(boolean keep, String path) {
        this(keep, path == null ? new String[]{} : path.split(" "));
    }

    /**
     * 当前位置是否为空.
     *
     * @return 是否为空
     */
    public boolean empty() {
        return current >= length;
    }

    /**
     * 当前位置是否非空.
     *
     * @return 是否非空
     */
    public boolean notEmpty() {
        return current < length;
    }

    /**
     * 游标下移一个.
     *
     * @return 下移后的命令树
     */
    public Paths next() {
        if (current < length) current++;
        return this;
    }

    /**
     * 游标上移一个.
     *
     * @return 上移后的命令树
     */
    public Paths revert() {
        if (current > 0) current--;
        return this;
    }

    /**
     * 当前位置之后的节的个数.
     *
     * @return size
     */
    public int size() {
        return length - current;
    }

    /**
     * 当前位置的第一个节.
     *
     * @return 第一个节
     */
    public String first() {
        if (current >= length) return "";
        return paths[current];
    }

    /**
     * 从当前位置获取索引处的节.
     *
     * @param index 索引
     * @return 节
     */
    public String get(int index) {
        if (current + index >= length) return "";
        return paths[current + index];
    }

    /**
     * 设置从当前位置开始的索引位置的节的内容.
     * 如果 {@code keep == true} 则不会修改.
     * 如果索引位置无效也不会修改.
     *
     * @param index 索引
     * @param value 新值
     */
    public void set(int index, String value) {
        if (!keep && current + index < length)
            paths[current + index] = value.replace(' ', '_')
                    .replace(':', '_').toLowerCase();
    }

    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "{ ", " }");
        for (String arg : paths) joiner.add(arg);
        return joiner.toString();
    }
}
