package pro.ganyushkin.overview.springbatch.springbatchdemo;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

public class Utils {

    public static String getSource(int index) throws IOException {
        Resource source = new ClassPathResource("item-"+index+".json");
        return new String(source.getInputStream().readAllBytes());
    }
}
