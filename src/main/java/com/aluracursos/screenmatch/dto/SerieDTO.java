package com.aluracursos.screenmatch.dto;

import com.aluracursos.screenmatch.model.Categoria;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

//traigo los datos de serie q quiero mostar al usuario (no traigo el id ni episodios)
//al final si traigo el id por q lo necesito para q muestre la infomracion de la serie dependiendo el id
public record SerieDTO(
         Long id,
         String titulo,
         Integer totalTemporadas,
         Double evaluacion,
         String poster,
         Categoria genero, //enum q es convertir las categorias a constantes
         String actores,
         String sinopsis
) {


}
