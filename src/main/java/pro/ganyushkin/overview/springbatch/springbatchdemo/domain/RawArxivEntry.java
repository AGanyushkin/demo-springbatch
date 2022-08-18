package pro.ganyushkin.overview.springbatch.springbatchdemo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class RawArxivEntry {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String doi;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String textJson;
}
