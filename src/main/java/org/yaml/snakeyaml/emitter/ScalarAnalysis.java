package org.yaml.snakeyaml.emitter;

public class ScalarAnalysis {

    public String scalar;
    public boolean empty;
    public boolean multiline;
    public boolean allowFlowPlain;
    public boolean allowBlockPlain;
    public boolean allowSingleQuoted;
    public boolean allowDoubleQuoted;
    public boolean allowBlock;

    /* Version 1.14 */
    public ScalarAnalysis(String scalar, boolean empty, boolean multiline, boolean allowFlowPlain, boolean allowBlockPlain, boolean allowSingleQuoted, boolean allowBlock) {
    }

    /* Version 1.9 */
    public ScalarAnalysis(String scalar, boolean empty, boolean multiline, boolean allowFlowPlain, boolean allowBlockPlain, boolean allowSingleQuoted, boolean allowDoubleQuoted, boolean allowBlock) {
    }

}
