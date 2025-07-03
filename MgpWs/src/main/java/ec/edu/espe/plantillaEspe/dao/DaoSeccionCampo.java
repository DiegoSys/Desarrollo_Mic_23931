package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.Campo;
import ec.edu.espe.plantillaEspe.model.SeccionCampo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para operaciones de acceso a datos de la entidad {@link SeccionCampo}.
 * Permite buscar relaciones entre secciones y campos por código.
 *
 * @author ITS
 */
public interface DaoSeccionCampo extends JpaRepository<SeccionCampo, String> {
    /**
     * Busca una relación sección-campo por su código.
     *
     * @param codigo Código de la relación.
     * @return Un Optional con la relación encontrada, si existe.
     */
    Optional<SeccionCampo> findByCodigo(String codigo);

    /**
     * Busca todas las relaciones de una sección específica con paginación.
     *
     * @param codigoSeccion Código de la sección.
     * @param pageable Parámetros de paginación.
     * @return Página con las relaciones sección-campo encontradas.
     */
    Page<SeccionCampo> findBySeccionCodigo(String codigoSeccion, Pageable pageable);

    /**
     * Busca todas las relaciones de una sección específica.
     *
     * @param codigoSeccion Código de la sección.
     * @return Lista de relaciones sección-campo.
     */
    List<SeccionCampo> findBySeccionCodigo(String codigoSeccion);

    /**
     * Busca todas las relaciones de un campo específico con paginación.
     *
     * @param codigoCampo Código del campo.
     * @param pageable Parámetros de paginación.
     * @return Página con las relaciones sección-campo encontradas.
     */
    Page<SeccionCampo> findByCampoCodigo(String codigoCampo, Pageable pageable);

    /**
     * Busca todas las relaciones de un campo específico.
     *
     * @param codigoCampo Código del campo.
     * @return Lista de relaciones sección-campo.
     */
    List<SeccionCampo> findByCampoCodigo(String codigoCampo);

    /**
     * Elimina todas las relaciones de una sección específica.
     *
     * @param campo Campo del cual se eliminarán las relaciones sección-campo.
     */
    void deleteByCampo(Campo campo);

}