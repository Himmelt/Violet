package org.soraworld.violet.yaml;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.error.YAMLException;

public class IDumperOptions extends DumperOptions {

    private int indicatorIndent = 0;

    public void setIndicatorIndent(int indicatorIndent) {
        if (indicatorIndent < 0) {
            throw new YAMLException("Indicator indent must be non-negative");
        }
        if (indicatorIndent > Emitter.MAX_INDENT - 1) {
            throw new YAMLException("Indicator indent must be at most " + (Emitter.MAX_INDENT - 1));
        }
        this.indicatorIndent = indicatorIndent;
    }

    public int getIndicatorIndent() {
        return this.indicatorIndent;
    }

}
