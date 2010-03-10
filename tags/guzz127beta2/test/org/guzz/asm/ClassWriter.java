/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.guzz.asm;

import java.io.IOException;
import java.io.PrintWriter;

import org.guzz.test.Article;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * 
 * 
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class ClassWriter implements ClassVisitor, Opcodes{
	
	public static void main(String[] args) throws IOException{
		ClassWriter cw = new ClassWriter() ;
		TraceClassVisitor trace = new TraceClassVisitor(cw, new PrintWriter(System.out)) ;
		
		ClassReader cr = new ClassReader(Article.class.getName()) ;
		
		cr.accept(trace, 0) ;
	}

	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		System.out.println(name + " extends " + superName + " {");
	}

	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return null;
	}

	public void visitAttribute(Attribute attr) {
		System.out.println(attr.type + attr.isCodeAttribute());
	}

	public void visitEnd() {
		System.out.println("}");
	}

	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		System.out.println(" " + desc + " " + name);
		return null;
	}

	public void visitInnerClass(String name, String outerName, String innerName, int access) {
		
	}

	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		System.out.println(" " + name + desc);
		
//		if("<init>".equals(name)){
			return new MethodVisitor(){
				int i = 0 ;
				public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
					i++ ;
					return null;
				}

				public AnnotationVisitor visitAnnotationDefault() {
					i++ ;
					return null;
				}

				public void visitAttribute(Attribute attr) {
					i++ ;
					
				}

				public void visitCode() {
					i++ ;
					
				}

				public void visitEnd() {
					i++ ;
					
				}

				public void visitFieldInsn(int opcode, String owner, String name, String desc) {
					i++ ;
					
				}

				public void visitFrame(int type, int local, Object[] local2, int stack, Object[] stack2) {
					i++ ;
					
				}

				public void visitIincInsn(int var, int increment) {
					i++ ;
					
				}

				public void visitInsn(int opcode) {
					i++ ;
					
				}

				public void visitIntInsn(int opcode, int operand) {
					i++ ;
				}

				public void visitJumpInsn(int opcode, Label label) {
					i++ ;
				}

				public void visitLabel(Label label) {
					i++ ;
				}

				public void visitLdcInsn(Object cst) {
					i++ ;
				}

				public void visitLineNumber(int line, Label start) {
					i++ ;
				}

				public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
					System.out.println("visit variable :[" + desc + "/" + name + "] at position:" + index) ;
					i++ ;
				}

				public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
					i++ ;
				}

				public void visitMaxs(int maxStack, int maxLocals) {
					i++ ;
				}

				public void visitMethodInsn(int opcode, String owner, String name, String desc) {
					i++ ;
				}

				public void visitMultiANewArrayInsn(String desc, int dims) {
					i++ ;
				}

				public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
					i++ ;
					return null;
				}

				public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
					i++ ;
				}

				public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
					i++ ;
				}

				public void visitTypeInsn(int opcode, String type) {
					i++ ;
				}

				public void visitVarInsn(int opcode, int var) {
					i++ ;
				}
			} ;
//		}
		
//		return null;
	}

	public void visitOuterClass(String owner, String name, String desc) {
		
	}

	public void visitSource(String source, String debug) {
		System.out.println(source + " " + debug);
	}	

}
