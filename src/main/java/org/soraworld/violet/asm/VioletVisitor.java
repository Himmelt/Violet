package org.soraworld.violet.asm;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;

/**
 * @author Himmelt
 */
public final class VioletVisitor extends ClassVisitor {

    private String name;
    private int access;
    private int version;
    private final HashMap<String, HashMap<String, Object>> annotations = new HashMap<>();

    public VioletVisitor() {
        super(Opcodes.ASM5);
    }

    public @NotNull ClassInfo getInfo() {
        return new ClassInfo(name, access, version, annotations);
    }

    @Override
    public void visit(int version, int access, @NotNull String name, String signature, String superName, String[] interfaces) {
        this.name = name.replace('/', '.');
        this.access = access;
        this.version = version;
    }

    @Override
    public @Nullable AnnotationVisitor visitAnnotation(@NotNull String desc, boolean visible) {
        String clazzName = null;
        if (desc.length() >= 3) {
            clazzName = desc.substring(1, desc.length() - 1).replace('/', '.');
        }
        if (clazzName != null && !clazzName.isEmpty()) {
            HashMap<String, Object> annotation = new HashMap<>();
            annotations.put(clazzName, annotation);
            return new AnnotationVisitor(Opcodes.ASM5) {
                @Override
                public void visit(String name, Object value) {
                    annotation.put(name, value);
                }
            };
        }
        return null;
    }

    @Override
    public @Nullable FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return null;
    }
}
