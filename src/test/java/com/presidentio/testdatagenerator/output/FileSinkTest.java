package com.presidentio.testdatagenerator.output;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.presidentio.testdatagenerator.cons.PathProviderConst;
import com.presidentio.testdatagenerator.cons.PropConst;

public class FileSinkTest {

    @Test // TDG-AC-TB12
    @Ignore("this test fails:enable when fixed")
    public void create_file_with_relative_path() throws Exception {
        create_file("a.sql", PathProviderConst.CONST);
    }

    @Test // TDG-AC-TB12 - this test passes
    public void create_file_with_dot_in_relative_path() {
        create_file("./target/a.sql", PathProviderConst.CONST);
    }

    @Test // TDG-AC-TB13
    public void create_file_with_folders() {
        String newFolder = "./target/this/folder/does/not/exist/";
        create_file(newFolder + "a.sql", PathProviderConst.CONST);
        assertTrue(Files.exists(Paths.get(newFolder)));
    }
    
    @Test // TDG-AC-TB14
    public void create_file_with_time_based_folder() {
        String filePath = create_file("./target/a", PathProviderConst.TIME);
        Date now = new Date();
        assertEquals(new SimpleDateFormat("/yyyy/MM/dd/HH/").format(now), filePath);
    }

    private String create_file(String fileName, String pathProvider) {
        FileSink fileSink = new FileSink();
        try{
            Map<String, String> props = new HashMap<>();
            props.put(PropConst.PATH_PROVIDER, pathProvider);
            props.put(PropConst.FILE, fileName);
            
            fileSink.init(props);
            
            fileSink.write(fileName, "INSERT INTO TABLE VALUES('VAL');");
            
            return (String) fileSink.getPartition(null, null);
        }
        finally {
            fileSink.close();
        }
    }
    
}