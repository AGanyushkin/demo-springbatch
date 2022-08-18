package pro.ganyushkin.overview.springbatch.springbatchdemo.batch;

import org.springframework.lang.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import pro.ganyushkin.overview.springbatch.springbatchdemo.domain.RawArxivEntry;

@Slf4j
@Component
public class RawArxivEntryProcessor implements ItemProcessor<String, RawArxivEntry> {

    @Override
    public RawArxivEntry process(@NonNull String item) {
        try {
            var obj = new JSONObject(item);
            var doi = obj.getString("doi");
            if (doi == null || doi.equalsIgnoreCase("null")) {
                return null;
            }
            return RawArxivEntry.builder()
                    .doi(doi)
                    .textJson(item)
                    .build();
        } catch (Exception e) {
            log.error("Can't process item", e);
            return null;
        }
    }
}
