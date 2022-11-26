package org.denny.generator.convert.java;

import org.denny.generator.Util;
import org.denny.generator.object.java.AnnotationObject;
import org.denny.generator.object.java.ClassObject;
import org.denny.generator.object.java.MemberObject;
import org.denny.generator.object.sql.TableObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClassConvert {

    public static ClassObject convertToClassObject(TableObject tableObject) {
        ClassObject mClassObject = new ClassObject();
        //set comment
        mClassObject.setComment(tableObject.getComment());
        //set default class annotation
        ArrayList<AnnotationObject> defaultAnnotationList = new ArrayList<>();
        getDefualtClassAnnotation(tableObject, defaultAnnotationList);
        mClassObject.setAnnotationList(defaultAnnotationList);
        //set class name
        mClassObject.setName(Util.toUpperCamelCase(tableObject.getName()));
        //set class member list
        ArrayList<MemberObject> memberObjectList = new ArrayList<>();
        tableObject.getColumnObjectList().forEach(columnObject -> {
            MemberObject memberObject = MemberConvert.convertMemberObject(columnObject);
            memberObjectList.add(memberObject);
        });
        mClassObject.setMemberObjectList(memberObjectList);

        return mClassObject;
    }

    /**
     * Get default Member Annotation
     */
    private static void getDefualtClassAnnotation(TableObject tableObject, ArrayList<AnnotationObject> defaultAnnotationList) {
        AnnotationObject entity = new AnnotationObject();
        entity.setFullName("javax.persistence.Entity");
        entity.setName("Entity");
        defaultAnnotationList.add(entity);

        Map<String, Object> tableMap = new HashMap<>();
        tableMap.put("name", tableObject.getName());

        AnnotationObject table = new AnnotationObject();
        table.setFullName("javax.persistence.Table");
        table.setName("Table");
        table.setValue(tableMap);
        defaultAnnotationList.add(table);
    }
}
