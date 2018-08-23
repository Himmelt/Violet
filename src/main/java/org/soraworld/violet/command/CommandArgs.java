package org.soraworld.violet.command;

import java.util.StringJoiner;

/**
 * 命令参数封装类.
 */
public final class CommandArgs {

    private final String[] args;
    private final int length;
    private int current;

    /**
     * 实例化命令参数.
     *
     * @param args 实际参数
     */
    public CommandArgs(String[] args) {
        this.args = args;
        this.length = this.args == null ? 0 : this.args.length;
        this.current = 0;
    }

    /**
     * 实例化命令参数.
     * 实际参数必须是以空格隔开的，否则无效.
     *
     * @param args 实际参数
     */
    public CommandArgs(String args) {
        this(args == null ? null : args.split(" "));
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
     * 参数游标下移一个.
     */
    public void next() {
        if (current < length) current++;
    }

    /**
     * 参数游标上移一个.
     *
     * @return 参数封装自身
     */
    public CommandArgs revert() {
        if (current > 0) current--;
        return this;
    }

    /**
     * 当前位置参数个数.
     *
     * @return size
     */
    public int size() {
        return length - current;
    }

    /**
     * 当前位置的第一个参数.
     *
     * @return 参数
     */
    public String first() {
        if (current >= length) return "";
        return args[current];
    }

    /**
     * 从当前位置获取索引处的参数.
     *
     * @param index 索引
     * @return 参数
     */
    public String get(int index) {
        if (current + index >= length) return "";
        return args[current + index];
    }

    public String toString() {
        if (args != null) {
            StringJoiner joiner = new StringJoiner(", ", "{ ", " }");
            for (String arg : args) joiner.add(arg);
            return joiner.toString();
        }
        return "{ null }";
    }
}
