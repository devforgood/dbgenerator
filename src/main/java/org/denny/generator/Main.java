package org.denny.generator;

import org.denny.generator.convert.java.ClassConvert;
import org.denny.generator.convert.sql.TableConvert;
import org.denny.generator.object.java.ClassObject;
import org.denny.generator.object.sql.TableObject;

public class Main {
    private static String testDDLStr = "CREATE TABLE `t_building` (\n" +
            "  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary key ID',\n" +
            "  `name` varchar(50) NOT NULL COMMENT 'name',\n" +
            "  `sale_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Sales status 1-on sale, 2-for sale, 3-sales',\n" +
            "  `floor_space` bigint(11) DEFAULT '0' COMMENT 'Site: Units of square decimeter',\n" +
            "  `card_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'certification time',\n" +
            "  PRIMARY KEY (`id`)\n" +
            "ENGINE=InnoDB AUTO_INCREMENT=197 DEFAULT CHARSET=utf8 COMMENT='Property Master';";

    public static void main(String[] args) {

        TableObject tableObject = TableConvert.convertToTableObject(testDDLStr);
        ClassObject classObject = ClassConvert.convertToClassObject(tableObject);

    }
}
