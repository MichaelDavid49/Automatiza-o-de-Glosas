package br.com.cooperativa.glosa.repository;
import br.com.cooperativa.glosa.domain.GlosaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface GlosaItemRepository extends JpaRepository<GlosaItem, Long> {
    List<GlosaItem> findByImportacaoId(Long importacaoId);
}
