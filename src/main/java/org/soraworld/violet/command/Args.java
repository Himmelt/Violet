package org.soraworld.violet.command;

import java.util.StringJoiner;

/**
 * 命令参数树
 */
public class Args {

    private int current;
    private final int length;
    private final String origin;
    private final String[] paths;

    /**
     * 实例化参数树.
     *
     * @param paths 参数树
     */
    public Args(String... paths) {
        this.paths = paths;
        this.length = this.paths.length;
        this.current = 0;
        StringJoiner joiner = new StringJoiner(" ", "", "");
        for (String s : paths) joiner.add(s);
        this.origin = joiner.toString();
    }

    /**
     * 实例化参数树.
     * 以 空格 分隔
     *
     * @param path 参数树原始字符串
     */
    public Args(String path) {
        this.paths = path == null || path.isEmpty() || path.equals(" ") ? new String[]{} : path.trim().split("[ ]+");
        this.length = this.paths.length;
        this.current = 0;
        this.origin = path;
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
     * 是否还有下一个路径段.
     *
     * @return 是否还有下一个
     */
    public boolean hasNext() {
        return current < length - 1;
    }

    /**
     * 游标下移一个.
     *
     * @return 下移后的参数树
     */
    public Args next() {
        if (current < length) current++;
        return this;
    }

    /**
     * 游标上移一个.
     *
     * @return 上移后的参数树
     */
    public Args revert() {
        if (current > 0) current--;
        return this;
    }

    /**
     * 获取 当前位置 之后路径段的个数.
     *
     * @return 当前大小
     */
    public int size() {
        return length - current;
    }

    /**
     * 获取 当前位置 的路径段.
     *
     * @return 当前路径段
     */
    public String first() {
        if (current >= length) return "";
        return paths[current];
    }

    /**
     * 获取 当前位置的索引位置 的路径段.
     *
     * @param index 索引位置
     * @return 路径段
     */
    public String get(int index) {
        if (current + index >= length) return "";
        return paths[current + index];
    }

    /**
     * 获取原始字符串.
     *
     * @return 原始字符串
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * 获取后续内容.
     *
     * @return 后续内容
     */
    public String getContent() {
        StringJoiner joiner = new StringJoiner(" ", "", "");
        for (int i = current; i < length; i++) joiner.add(paths[i]);
        return joiner.toString();
    }

    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "{ ", " }");
        for (String arg : paths) joiner.add(arg);
        return joiner.toString();
    }
}
