package org.denny.generator;

import com.mysql.cj.util.StringUtils;
import org.denny.generator.object.java.AnnotationObject;
import org.denny.generator.object.java.ClassObject;
import org.denny.generator.object.java.MemberObject;

import java.util.*;

public class GeneratorDatabaseModel {

    public String formatPOJOString(ClassObject classObject) {

        return String.format(
                "%s\n"
                        + "\n/**"
                        + "\n * %s"
                        + "\n */"
                        + "%s\n"
                        + "public class %s {\n%s}"
                , formatImportPackage(classObject)
                , classObject.getComment()
                , formatClassAnnotation(classObject.getAnnotationList())
                , classObject.getName()
                , formatBody(classObject)
        );
    }

    private String formatImportPackage(ClassObject classObject) {
        Set<String> importSet = new HashSet<>();

        setImportSetByAnnotationList(importSet, classObject.getAnnotationList());
        setImportSetByMemberType(importSet, classObject);

        setImportSetByMemberAnnotationList(importSet, classObject.getMemberObjectList());

        List<String> sortedList = new ArrayList<>(importSet);
        Collections.sort(sortedList);

        StringBuilder sb = new StringBuilder();
        sortedList.forEach(importStr -> {
            sb.append(String.format("\nimport %s;"
                            , importStr
                    )
            );
        });
        return sb.toString();
    }

    private StringBuilder formatClassAnnotation(List<AnnotationObject> annotationObjectList) {
        StringBuilder stringBuilder = new StringBuilder();
        annotationObjectList.forEach(annotation -> {
            stringBuilder.append("\n@").append(annotation.getName());//@javax.persistence.EntityListeners
            stringBuilder.append(getAnnotationValue(annotation));
        });
        return stringBuilder;
    }

    private StringBuilder formatMemberAnnotation(List<AnnotationObject> annotationObjectList) {
        StringBuilder stringBuilder = new StringBuilder();
        annotationObjectList.forEach(annotation -> {
            stringBuilder.append("\n\t@").append(annotation.getName());//@javax.persistence.EntityListeners
            stringBuilder.append(getAnnotationValue(annotation));
        });
        return stringBuilder;
    }

    private StringBuilder getAnnotationValue(AnnotationObject annotation) {
        Map<String, Object> valueMap = annotation.getValue();
        StringBuilder stringBuilder = new StringBuilder();
        if (valueMap == null) {
            return stringBuilder;
        }
        stringBuilder.append("(");//@javax.persistence.EntityListeners(
        valueMap.forEach((key, value) -> {
            if (!StringUtils.isNullOrEmpty(key)) {
                stringBuilder.append(key).append(" = ");//@javax.persistence.EntityListeners(name =
            }

            String valueString;
            if (value instanceof String) {
                valueString = "\"" + value.toString() + "\"";
            } else {
                valueString = value.getClass().getName() + "." + value;
            }
            stringBuilder.append(valueString);//@javax.persistence.EntityListeners(name = "t_zibo_building"

        });
        stringBuilder.append(")");//@javax.persistence.EntityListeners(name = "t_zibo_building")
        return stringBuilder;
    }

    private StringBuilder formatBody(ClassObject classObject) {
        StringBuilder stringBuilder = new StringBuilder();
        //Member
        classObject.getMemberObjectList().forEach(memberObject -> {
            stringBuilder.append(formatMenber(memberObject));
        });
        //Getter And Settter
        classObject.getMemberObjectList().forEach(memberObject -> {
            stringBuilder.append(formatGetterAndSetter(memberObject));
        });
        //default constructor
        stringBuilder.append(formatConstructor(classObject));
        return stringBuilder;
    }

    private String formatMenber(MemberObject memberObject) {
        return String.format(
                "\n\t/**" +
                        "\n\t * %s" +
                        "\n\t */" +
                        "%s" +
                        "\n\tprivate %s %s;" +
                        "\n"
                , memberObject.getComment()
                , formatMemberAnnotation(memberObject.getAnnotationList())
                , getMemberType(memberObject)
                , memberObject.getName()
        );
    }

    private String formatGetterAndSetter(MemberObject memberObject) {
        return String.format(
                "\n\tpublic %s get%s() {" +
                        "\n\t\treturn %s;" +
                        "\n\t}\n"
                , getMemberType(memberObject)
                , getMethodName(memberObject)
                , memberObject.getName()
        ) +
                String.format("\n\tpublic void set%s(%s %s) {\n\t\tthis.%s = %s;\n\t}\n"
                        , getMethodName(memberObject)
                        , getMemberType(memberObject)
                        , getMethodParmName(memberObject)
                        , memberObject.getName()
                        , getMethodParmName(memberObject)
                );
    }

    private String formatConstructor(ClassObject classObject) {
        return String.format(
                "\n\tpublic %s() {" +
                        "%s" +
                        "\n\t}\n", classObject.getName(), getDefaultMember(classObject)
        );
    }

    private String getDefaultMember(ClassObject classObject) {
        StringBuilder sb = new StringBuilder();
        classObject.getMemberObjectList().forEach(memberObject -> {
            sb.append(String.format("\n\t\tthis.%s = %s;", memberObject.getName(), memberObject.getDefaultValue()));
        });
        return sb.toString();
    }

    private String getMemberType(MemberObject memberObject) {
        //如果 Member 的类型需要引入包名
        String typeName = memberObject.getTypeName();
        if (!typeName.contains(".")) {
            return typeName;
        }
        return typeName.substring(typeName.lastIndexOf(".") + 1);
    }

    private String getMethodParmName(MemberObject memberObject) {
        return Util.toLowCamelCase(memberObject.getName());
    }

    private String getMethodName(MemberObject memberObject) {
        String methodName = Util.toUpperCamelCase(memberObject.getName());
        String typeName = memberObject.getTypeName();
        if ("Boolean".equals(typeName)
                && methodName.substring(0, 2).equalsIgnoreCase("is")
                && Character.isUpperCase(methodName.charAt(2))) {
            methodName = methodName.substring(2);
        }
        return methodName;
    }

    private void setImportSetByMemberAnnotationList(Set<String> importSet, List<MemberObject> memberList) {
        if (memberList == null) {
            return;
        }
        memberList.forEach(memberObject -> {
            List<AnnotationObject> menberAnnotationList = memberObject.getAnnotationList();
            setImportSetByAnnotationList(importSet, menberAnnotationList);
        });
    }

    private void setImportSetByMemberType(Set<String> importSet, ClassObject classObject) {
        if (classObject == null) {
            return;
        }
        classObject.getMemberObjectList().forEach(memberObject -> {
            String typeName = memberObject.getTypeName();
            if (!typeName.contains(".")) {
                return;
            }
            importSet.add(typeName);
        });
    }

    private void setImportSetByAnnotationList(Set<String> importSet, List<AnnotationObject> anotationList) {
        if (anotationList == null) {
            return;
        }
        anotationList.forEach(annotationObject -> {
            String fullName = annotationObject.getFullName();
            if (!fullName.contains(".")) {
                return;
            }
            importSet.add(fullName);
        });
    }
}
