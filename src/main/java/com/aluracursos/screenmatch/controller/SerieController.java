package com.aluracursos.screenmatch.controller;

import com.aluracursos.screenmatch.dto.EpisodioDTO;
import com.aluracursos.screenmatch.dto.SerieDTO;
import com.aluracursos.screenmatch.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//El controlador se encarga de manejar las solicitudes HTTP y devolver las respuestas

@RestController //trabajamos con una rest api
@RequestMapping("/series")  // /series es la URL base y las otras rutas ya no le ponemos esta al inicio
public class SerieController {

    @Autowired
    private SerieService servicio;

    //mostrar todas las series en formato json q tengo en la bd

    @GetMapping() //ruta q mapeo o endPoint //"/series"
    public List<SerieDTO> obtenerTodasLasSeries() {

        return servicio.obtenerTodasLasSeries(); //llamamos a ese servicio
    }

    @GetMapping("/top5")
    //esta direccion es la misma q pone el front, ("/series/top5") ya no se pone la base series por q ya la mapeamos al inicio de esta clase con @RequestMapping("/series")
    public List<SerieDTO> obtenerTop5() {
        return servicio.obtenerTop5();
    }

    //sale al inicio en lanzamientos dentro del mismo index.html
    @GetMapping("lanzamientos")
    public List<SerieDTO> obtenerLanzamientosMasRecientes() {
        return servicio.obtenerLanzamientosMasRecientes();
    }

    //trae series por id
    @GetMapping("/{id}") //id va a ser un parametro dinamico ya q dependiendo el id traera cierta informacion
    public SerieDTO obtenerPorId(@PathVariable Long id) { //PathVariable es el dato q cambia en la url el id,id del mismo tipo de dato q tenemos q es Long
        return servicio.obtenerPorId(id);
    }

    //cuando le doy ver todas las temporadas las visualiza todas
    @GetMapping("/{id}/temporadas/todas")
    public List<EpisodioDTO> obtenerTodasLasTemporadas(@PathVariable Long id) {
        return servicio.obtenerTodasLasTemporadas(id);
    }

    //devuelve la temporada q escojamos
    @GetMapping("/{id}/temporadas/{numeroTemporada}") //los nombres de los pathVariables ejm numeroTemporada debe ser igual al q coloco dentro del metodo, igual q el tipo de dato
    public List<EpisodioDTO> obtenerTemporadasPorNumero(@PathVariable Long id, @PathVariable Long numeroTemporada) {  //igual al de arriba numeroTemporada
        return servicio.obtenerTemporadasPorNumero(id, numeroTemporada);
    }

    //traer series por categoria (drama, accion, etc)
    @GetMapping("/categoria/{nombreGenero}")
    public List<SerieDTO> obtenerSeriesPorCategoria(@PathVariable String nombreGenero) {
        return servicio.obtenerSeriesPorCategoria(nombreGenero);
    }
}
