package com.example.annotations;


import com.google.auto.service.AutoService;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;
import org.springframework.javapoet.JavaFile;
import org.springframework.javapoet.MethodSpec;
import org.springframework.javapoet.TypeName;
import org.springframework.javapoet.TypeSpec;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@AutoService(Process.class)
@SupportedAnnotationTypes({"com.example.annotations.CalTime"})
public class CalTimeProcessor extends AbstractProcessor {

    private JavacTrees trees;

    private TreeMaker treeMaker;

    private Names names;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        trees = JavacTrees.instance(this.processingEnv);
        this.names = Names.instance(((JavacProcessingEnvironment) this.processingEnv).getContext());
        this.treeMaker = TreeMaker.instance(((JavacProcessingEnvironment) this.processingEnv).getContext());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("开始处理CalTime注解");
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "hello ，workd 123");
        // 获取被该类注解的类
        for (Element element : roundEnv.getElementsAnnotatedWith(CalTime.class)) {
            if (element.getKind() == ElementKind.METHOD) {
                ExecutableElement methodElement = (ExecutableElement) element;
                String methodName = methodElement.getSimpleName().toString();
                List<? extends VariableElement> parameters = methodElement.getParameters();
                TypeMirror returnType = methodElement.getReturnType();
                generateSourceFile(methodName, parameters, returnType);
            }
        }
        return false;
    }

    private void generateSourceFile(String methodName, List<? extends VariableElement> parameters, TypeMirror returnType) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName + "_generated")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.get(returnType));
        for(VariableElement parameter : parameters) {
            methodBuilder.addParameter(TypeName.get(parameter.asType()), parameter.getSimpleName().toString());
        }
        methodBuilder.addStatement("$T.out.println($S)", System.class, "hello");
        StringBuilder statementBuidler = new StringBuilder();
        statementBuidler.append("$T result = $N(");
        for (int i = 0;i<parameters.size();i++) {
            if (i>0) statementBuidler.append(", ");
            statementBuidler.append(parameters.get(i).getSimpleName().toString());
        }
        statementBuidler.append(")");
        methodBuilder.addStatement(statementBuidler.toString(), TypeName.get(returnType), methodName);
        methodBuilder.addStatement("$T,out.pringtln($S)", System.class, "end");
        methodBuilder.addStatement("return result");
        // 构建方法规范
        MethodSpec methodSpec = methodBuilder.build();
        // 创建一个类规范，指定类的名称，修饰符，包含生成的方法
        TypeSpec typeSpec = TypeSpec.classBuilder(methodName + "_Wrapper")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodSpec)
                .build();
        // 创建一个Java文件对象，指定包名和类规范
        JavaFile javaFile = JavaFile.builder("com.example", typeSpec)
                .build();
        // 将Java文件对象写入到文件系统中
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
