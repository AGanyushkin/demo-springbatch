package pro.ganyushkin.overview.springbatch.springbatchdemo.batch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.ganyushkin.overview.springbatch.springbatchdemo.CONSTANTS;
import pro.ganyushkin.overview.springbatch.springbatchdemo.Utils;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RawArxivEntryProcessorTest {
    @Autowired
    private RawArxivEntryProcessor processor;

    @Test
    public void shouldWork() throws Exception {
        var entry = processor.process(Utils.getSource(1));

        assertNotNull(entry);
        assertEquals(CONSTANTS.TEST_DATA_DOI, entry.getDoi());
    }

    @Test
    public void shouldReturnNullForIncorrectInput() throws Exception {
        var entry = processor.process(Utils.getSource(3));

        assertNull(entry);
    }

    @Test
    public void shouldReturnNullForIncorrectDoi() throws Exception {
        var entry = processor.process(Utils.getSource(4));

        assertNull(entry);
    }
}