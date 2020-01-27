package org.soraworld.violet.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Himmelt
 */
public class VioletVisitor extends ClassVisitor {

    private String name;
    private int access;
    private int version;
    private final Set<String> annotations = new HashSet<>();

    public VioletVisitor() {
        super(Opcodes.ASM5);
    }

    public ClassInfo getInfo() {
        return new ClassInfo(name, access, version, annotations);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.name = name.replace('/', '.');
        this.access = access;
        this.version = version;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (desc.length() >= 3) {
            annotations.add(desc.substring(1, desc.length() - 1).replace('/', '.'));
        }
        return null;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return null;
    }
}
