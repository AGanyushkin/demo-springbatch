package pro.ganyushkin.overview.springbatch.springbatchdemo.batch;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import pro.ganyushkin.overview.springbatch.springbatchdemo.domain.ArxivArticleEntry;
import pro.ganyushkin.overview.springbatch.springbatchdemo.domain.RawArxivEntry;

@Slf4j
@Component
public class ArxivArticleEntryProcessor implements ItemProcessor<RawArxivEntry, ArxivArticleEntry> {

    @Override
    public ArxivArticleEntry process(@NonNull RawArxivEntry item) {
        try {
            var obj = new JSONObject(item.getTextJson());
            return ArxivArticleEntry.builder()
                    .rawId(item.getId())
                    .doi(item.getDoi())
                    .arxivId(obj.getString("id"))
                    .journalRef(processNullableValue(obj.getString("journal-ref")))
                    .categories(obj.getString("categories"))
                    .title(obj.getString("title"))
                    .authors(obj.getString("authors"))
                    .abstractText(obj.getString("abstract"))
                    .build();
        } catch (Exception e) {
            log.error("Can't process item", e);
            return null;
        }
    }

    private String processNullableValue(String value) {
        if (value == null) return null;
        if (value.equalsIgnoreCase("null")) return null;
        return value;
    }
}
