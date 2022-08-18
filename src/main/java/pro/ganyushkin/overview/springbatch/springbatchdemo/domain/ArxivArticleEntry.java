package pro.ganyushkin.overview.springbatch.springbatchdemo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ArxivArticleEntry {

    @Id
    @Column(nullable = false)
    private String doi;

    @Column(nullable = false, unique = true)
    private Long rawId;

    @Column(nullable = false, unique = true)
    private String arxivId;

    @Column(columnDefinition = "TEXT")
    private String journalRef;

    @Column(columnDefinition = "TEXT")
    private String categories;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String authors;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String abstractText;
}
