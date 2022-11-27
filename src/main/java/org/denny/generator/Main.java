package org.denny.generator;

import org.denny.generator.convert.java.ClassConvert;
import org.denny.generator.convert.sql.TableConvert;
import org.denny.generator.object.java.ClassObject;
import org.denny.generator.object.sql.TableObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) {

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.137.10?serverTimezone=UTC","testuser","test1234");
            var st = conn.createStatement();
            ResultSet rs = st.executeQuery("SHOW CREATE TABLE game.player_reputation"); // ResultSet은 쿼리문을 보낸후 나온 결과를 가져올 때 사용한다.

            while(rs.next()) {
                var str = rs.getString("Create Table");
                System.out.println(str);

                TableObject tableObject = TableConvert.convertToTableObject(str);
                ClassObject classObject = ClassConvert.convertToClassObject(tableObject);

                var out = new GeneratorDatabaseModel().formatPOJOString(classObject);
                System.out.println(out);



            }
        }catch (Exception e){
            e.printStackTrace();
        }




    }
}
