package pro.ganyushkin.overview.springbatch.springbatchdemo.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import pro.ganyushkin.overview.springbatch.springbatchdemo.domain.ArxivArticleEntry;

@Repository
public interface ArxivArticleEntryRepository extends PagingAndSortingRepository<ArxivArticleEntry, String> {
}
