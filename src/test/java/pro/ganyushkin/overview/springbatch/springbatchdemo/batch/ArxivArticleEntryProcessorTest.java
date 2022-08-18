package pro.ganyushkin.overview.springbatch.springbatchdemo.batch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.ganyushkin.overview.springbatch.springbatchdemo.CONSTANTS;
import pro.ganyushkin.overview.springbatch.springbatchdemo.Utils;
import pro.ganyushkin.overview.springbatch.springbatchdemo.domain.RawArxivEntry;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArxivArticleEntryProcessorTest {

    @Autowired
    private ArxivArticleEntryProcessor processor;

    @Test
    public void shouldWork() throws Exception {
        var entry = processor.process(
                new RawArxivEntry(1L, CONSTANTS.TEST_DATA_DOI, Utils.getSource(1))
        );

        assertNotNull(entry);
        assertEquals(CONSTANTS.TEST_DATA_DOI, entry.getDoi());
        assertEquals(1L, entry.getRawId());
        assertEquals(CONSTANTS.TEST_DATA_ARXIVID, entry.getArxivId());
        assertEquals(CONSTANTS.TEST_DATA_JOURNALREF, entry.getJournalRef());
        assertEquals(CONSTANTS.TEST_DATA_CATEGORY, entry.getCategories());
    }

    @Test
    public void shouldProcessNullValueForJournalRef() throws Exception {
        var entry = processor.process(
                new RawArxivEntry(2L, CONSTANTS.TEST_DATA_DOI, Utils.getSource(2))
        );

        assertNotNull(entry);
        assertEquals(CONSTANTS.TEST_DATA_ARXIVID, entry.getArxivId());
        assertNull(entry.getJournalRef());
    }
}
