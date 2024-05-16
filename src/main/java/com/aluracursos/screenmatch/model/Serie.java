package com.aluracursos.screenmatch.model;

import com.aluracursos.screenmatch.service.ConsultaChatGPT;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.*;

import java.util.List;
import java.util.OptionalDouble;

@Entity //clase entidad, es una tabla en la bd
@Table(name = "series") //cambiamos el nombre en la base de datos
public class Serie {

    @Id() //es un id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //id autoincrementable
    private Long Id;

    @Column(unique = true)  //ningun nombre de serie se repita, sea unico
    private String titulo;

    private Integer totalTemporadas;
    private Double evaluacion;
    private String poster;

    //STRING toma el texto en general: ACCION, DRAMA, ETC
    @Enumerated(EnumType.STRING) //si en vez de STRING es ORDINAL, mapea cada enum y les pone 0, 1, 2 ejm ACCION 0
    private Categoria genero; //enum q es convertir las categorias a constantes

    private String actores;
    private String sinopsis;

    //@Transient  //No quiero mapear este atributo en la BD

    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, fetch = FetchType.EAGER)  //este serie es el atributo de la clase episodio, cascade q haga insercion update o lo q sea tambien en la otra tabla, fetch = FetchType.EAGER q traiga los datos anticipadamente q los muestre cuando seleccionamos mostrar en el menu 3 q traiga de manera ansiosa esos datos
    private List<Episodio> episodios; //no olvidar hacer get y set de esta

    //constructor

    public Serie() {

    }
    public Serie(DatosSerie datosSerie) {
        this.titulo = datosSerie.titulo();
        this.totalTemporadas = datosSerie.totalTemporadas();
        this.evaluacion = OptionalDouble.of(Double.valueOf(datosSerie.evaluacion())).orElse(0); //tenemos o no un optional de double y transforma el string q viene a double
        this.poster = datosSerie.poster();
        this.genero = Categoria.fromString(datosSerie.genero().split(",")[0].trim()); //split trae solo la primera categoria popr q a veces la serie tiene artas categorias, y el trim es para q no traiga ningun valor vacio
        this.actores = datosSerie.actores();
        this.sinopsis = datosSerie.sinopsis();
        //this.sinopsis = ConsultaChatGPT.obtenerTraduccion( datosSerie.sinopsis() ); //para q chat GPT traduzca la sinopsis
    }

    //getters and setters


    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getTotalTemporadas() {
        return totalTemporadas;
    }

    public void setTotalTemporadas(Integer totalTemporadas) {
        this.totalTemporadas = totalTemporadas;
    }

    public Double getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(Double evaluacion) {
        this.evaluacion = evaluacion;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public Categoria getGenero() {
        return genero;
    }

    public void setGenero(Categoria genero) {
        this.genero = genero;
    }

    public String getActores() {
        return actores;
    }

    public void setActores(String actores) {
        this.actores = actores;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public List<Episodio> getEpisodios() {
        return episodios;
    }

    public void setEpisodios(List<Episodio> episodios) {
        //ESTO ES IMPORTANTE HACERLO PARA Q QUEDE GUARDADO EL ID DE LA SERIE DE CADA EPISODIO
        //para cada episodio debe aceptar en cadaserie el valor de ella misma de la serie q estamos
        //esto al buscar episodios
        //ejm buscamos los episodios de lucifer y v a decir, cuando haga el setSerie, dira, estoy trabajando con el ID de lucifer
        episodios.forEach(e -> e.setSerie(this)); //set serie la relacion debe ser en ambas clases
        this.episodios = episodios;
    }

    //PARA LOGAR VER LOS EPISODIOS HAY Q PONER ESOS EPISODIOS EN EL toString

    @Override
    public String toString() {
        return
                "genero=" + genero +
                " titulo='" + titulo + '\'' +
                ", totalTemporadas=" + totalTemporadas +
                ", evaluacion=" + evaluacion +
                ", poster='" + poster + '\'' +
                ", actores='" + actores + '\'' +
                ", sinopsis='" + sinopsis + '\'' + ", episodios='" + episodios + '\'';
    }
}
