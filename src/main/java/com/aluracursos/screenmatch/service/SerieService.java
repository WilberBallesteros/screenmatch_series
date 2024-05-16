package com.aluracursos.screenmatch.service;

import com.aluracursos.screenmatch.dto.EpisodioDTO;
import com.aluracursos.screenmatch.dto.SerieDTO;
import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.Serie;
import com.aluracursos.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//esta clase se encargara de la logica de negocio de transformar
//de Serie a SerieDTO, que va a llamar directamente a nuestro repository
//las reglas de negocio engloba el accesar a la base de datps

@Service
public class SerieService {

    @Autowired
    private SerieRepository repository; //inyeccion de dependencias

    public List<SerieDTO> obtenerTodasLasSeries() {
        return convierteDatos(repository.findAll());
    }

    public List<SerieDTO> obtenerTop5() {
        return convierteDatos(repository.findTop5ByOrderByEvaluacionDesc());  //findTop5ByOrderByEvaluacionDesc metodo esta en SerieRepository
    }

    public List<SerieDTO> obtenerLanzamientosMasRecientes() {
        return convierteDatos(repository.lanzamientosMasRcientes());
    }

    public List<SerieDTO> convierteDatos(List<Serie> serie) {
        return serie.stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getEvaluacion(), s.getPoster(), //en el mismo orden q los tenemos en SerieDTO
                        s.getGenero(), s.getActores(), s.getSinopsis()))
                .collect(Collectors.toList()); //Collectors.toList() es para convertir a una lista de tipo de dato SerieDt
    }

    //obtener datos por id
    public SerieDTO obtenerPorId(Long id) {
        Optional<Serie> serie =  repository.findById(id);
        if (serie.isPresent()) {
            Serie s = serie.get();
            return new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getEvaluacion(), s.getPoster(), //en el mismo orden q los tenemos en SerieDTO
                    s.getGenero(), s.getActores(), s.getSinopsis());
        } else {
            return null;
        }
    }

    //obtiene todas las temporadas
    public List<EpisodioDTO> obtenerTodasLasTemporadas(Long id) {
        Optional<Serie> serie =  repository.findById(id);
        if (serie.isPresent()) {
            Serie s = serie.get();
            return s.getEpisodios().stream().map(e -> new EpisodioDTO(e.getTemporada(), e.getTitulo(),
                    e.getNumeroEpisodio())).collect(Collectors.toList());
        }
        return null;
    }

    //obtiene la temporada elegida
    public List<EpisodioDTO> obtenerTemporadasPorNumero(Long id, Long numeroTemporada) {
        return repository.obtenerTemporadasPorNumero(id, numeroTemporada).stream()
                .map(e -> new EpisodioDTO(e.getTemporada(), e.getTitulo(),
                        e.getNumeroEpisodio())).collect(Collectors.toList());
    }

    public List<SerieDTO> obtenerSeriesPorCategoria(String nombreGenero) {
        Categoria categoria = Categoria.fromEspanol(nombreGenero); //usamos el metodo fromEspañol para convertir el String nombreGenero a tipo Categoria
        return convierteDatos(repository.findByGenero(categoria));
    }
}
