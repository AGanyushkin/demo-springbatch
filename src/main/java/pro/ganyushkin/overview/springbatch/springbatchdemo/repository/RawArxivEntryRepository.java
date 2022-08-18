package pro.ganyushkin.overview.springbatch.springbatchdemo.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import pro.ganyushkin.overview.springbatch.springbatchdemo.domain.RawArxivEntry;

@Repository
public interface RawArxivEntryRepository extends PagingAndSortingRepository<RawArxivEntry, Long> {
}
