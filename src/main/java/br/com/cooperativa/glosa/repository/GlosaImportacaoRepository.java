package br.com.cooperativa.glosa.repository;
import br.com.cooperativa.glosa.domain.GlosaImportacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface GlosaImportacaoRepository extends JpaRepository<GlosaImportacao, Long> {
    List<GlosaImportacao> findAllByOrderByDataImportacaoDesc();
}
