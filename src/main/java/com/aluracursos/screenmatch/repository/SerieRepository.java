package com.aluracursos.screenmatch.repository;

import com.aluracursos.screenmatch.dto.EpisodioDTO;
import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

//hay q extender un tipo generico mapeamos la Serie QUE TENEMOS EN EL MODELO y un ID en este caso Long q es el tipo de dato Id en Serie
//antiguamente se ponia aqui @Repository pero ya lo trae implicitamente al extender de JpaRepository
public interface SerieRepository extends JpaRepository<Serie, Long> {

    //CONSULTAS DERIVADAS derived queries
    //crear el texto que JPA va a interpretar para nosotros, esa busqueda
    //utilizar los mismos valores q tenemos dentro de nuestras clases Serie
    Optional<Serie> findByTituloContainsIgnoreCase(String nombreSerie);

    //metodo para traer el top 5
    //Evaluacion aunq este en mayus la E jpa lo entiende q es el atributo de la clase, el Desc es para q lo traiga de mayor a menor
    List<Serie> findTop5ByOrderByEvaluacionDesc();


    //buscar por categorias
    List<Serie> findByGenero(Categoria categoria);  //ese genero es atributo de la clase Serie y aqui en mayus x convencion

    //todas las series que tengan un número máximo de temporadas y una evaluación mínima de tanto
    //List<Serie> findByTotalTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(int totalTemporadas, Double evaluacion);


    //USANDO SQL QUERIES NATIVAS
    //como el metodo de arriba es un asco para crearlo y leerlo vamos a hacer algo parecido
    //pasamos el query y el native query true por q por defecto es false
    //tener en cuenta q la profe hizo la consulta SELECT * FROM series WHERE series.total_temporadas <= 6 AND series.evaluacion >= 7.5, poniendo el nombre de la tabla seguida del punto despues nombre columna, ya se q en sql vale paja no se pone eso

    //@Query( value = "SELECT * FROM series WHERE total_temporadas <= 6 AND evaluacion >= 7.5", nativeQuery = true)
    //List<Serie> seriesPorTemporadaTevaluacion();


    //con JPQL que es el lenguaje de queries nativos de Java
    //Nota como cambiamos el * por una s q es una representacion de la entidad q estoy trabajando en este caso Serie
    //tambien cambiamos elnombre de la tabla de la bd por el nombre de la clase entidad Serie con el alias s
    //en vez de pasarle las temporadas o valores le pasamos despues de los dos puntos : (int totalTemporadas, Double evaluacion) dentro del query

    @Query("SELECT s FROM Serie s WHERE s.totalTemporadas <= :totalTemporadas AND s.evaluacion >= :evaluacion")
    List<Serie> seriesPorTemporadaTevaluacion(int totalTemporadas, Double evaluacion);

    //metodo para buscar episodios por parte del titulo

    //ILIKE permite realizar busquedas de texto sin distinguir entre mayus y minusculas
    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:nombreEpisodio%")  //s.episodios es episodios atributo lista en la clase Serie, e de episodio
    List<Episodio> episodiosPorNombre(String nombreEpisodio);


    //este podria ser un ejm en sql del metodo episodiosPorNombre q esta en jpql
    //@Query(value = "SELECT * FROM episodios WHERE titulo ILIKE %:nombreEpisodio%", nativeQuery = true)
    //List<Episodio> buscarEpisodiosPorTitulo(@Param("nombreEpisodio") String nombreEpisodio);


    //BUSCANDO TOP 5 EPISODIOS DE LA SERIE
    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.evaluacion Desc LIMIT 5")
    List<Episodio> top5Episodios(Serie serie);

    //episodio mas reciente de una serie con JPQL
    @Query("SELECT s FROM Serie s " + "JOIN s.episodios e " + "GROUP BY s " + "ORDER BY MAX(e.fechaDeLanzamiento) DESC LIMIT 5")
    List<Serie> lanzamientosMasRcientes();


    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s.id = :id AND e.temporada = :numeroTemporada")
    List<Episodio> obtenerTemporadasPorNumero(Long id, Long numeroTemporada);
}
