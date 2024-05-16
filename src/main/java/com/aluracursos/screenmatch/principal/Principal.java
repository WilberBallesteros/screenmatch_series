package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.repository.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=fbb8ae8";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie> datosSeries = new ArrayList<>();
    private SerieRepository repositorio; //este lo creamos para pasarlo al constructorde abajo, ver ScreenmatchApplication
    private List<Serie> series;
    Optional<Serie> serieBuscada;



    //este repository lo creamos en ScreenmatchApplication
    public Principal(SerieRepository repository) {
        this.repositorio = repository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar series
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                    4 - Buscar series por titulo
                    5 - Top 5 mejores Series
                    6 - Buscar series por categoria
                    7 - Filtrar series
                    8 - Buscar episodios por titulo
                    9 - Top 5 episodios por serie
                                  
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriesPorTitulo();
                    break;
                case 5:
                    buscarTop5Series();
                    break;
                case 6:
                    buscarSeriesPorCategoria();
                    break;
                case 7:
                    filtrarSeriesPorTemporadaYEvaluacion();
                    break;
                case 8:
                    buscarEpisodiosPorTitulo();
                    break;
                case 9:
                    buscarTop5Episodios();
                    break;

                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }

    private DatosSerie getDatosSerie() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        System.out.println(json);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }

    private void buscarEpisodioPorSerie() {
        mostrarSeriesBuscadas();
        System.out.println("Escribe el nombre de la Serie de la que quieres ver los episodios: ");
        var nombreSerie = teclado.nextLine();

        //realizamos la busqueda que puede o no traer un resultado
        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase())) //filtra la serie q escribe el usuario para q no importen mayusculas ni minusculas
                .findFirst(); //encuentre la primera coincidencia

        if (serie.isPresent()) {
            var serieEncontrada = serie.get(); //si lo encuentra retorna los datos de esa serie

            List<DatosTemporadas> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DatosTemporadas datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e))) //convierte los datos episodios en nuevo Episodio
                    .collect(Collectors.toList());
            //guarda en setEpisodios
            serieEncontrada.setEpisodios(episodios);

            repositorio.save(serieEncontrada); //guardar la serieEncontrada
        }
    }

    private void buscarSerieWeb() {
        DatosSerie datos = getDatosSerie();
        Serie serie = new Serie(datos); // datos q traje de la busqueda de la API
        repositorio.save(serie); //GUARDA la serie q acabamos de buscar en el menu opciones

        //datosSeries.add(datos); //se añaden los datos
        System.out.println(datos);
    }

    private void mostrarSeriesBuscadas() {

        //esta variable esta declarada al inicio en el scope global de la clase Principal
        series = repositorio.findAll(); //hay q tener un constructor vacion en clase Serie

        //agrupar los datos por genero de la serie
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))  //ordena las series por genero
                .forEach(System.out::println);
    }

    private void buscarSeriesPorTitulo() {
        System.out.println("Escribe el nombre de la Serie que deseas buscar: ");
        var nombreSerie = teclado.nextLine();

        //este repositorio.find... viene de SerieRepository
        serieBuscada = repositorio.findByTituloContainsIgnoreCase(nombreSerie);

        if (serieBuscada.isPresent()) {
            System.out.println("La serie buscada es. " + serieBuscada.get());
        } else {
            System.out.println("Serie no encontrada");
        }
    }

    //top 5 series
    private void buscarTop5Series() {
        List<Serie> topSeries = repositorio.findTop5ByOrderByEvaluacionDesc();
        topSeries.forEach(s -> System.out.println("Serie: " + s.getTitulo() + "Evaluacion: " + s.getEvaluacion()));
    }

    //busca series por categoria
    private void buscarSeriesPorCategoria() {
        System.out.println("Escriba el genero/categoria de la serie que desea buscar: ");
        var genero = teclado.nextLine();
        var categoria = Categoria.fromEspanol(genero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Las series de la categoria " + genero);
        seriesPorCategoria.forEach(System.out::println); //imprimimos todos los datos
    }

    //si uso SQL noimporta lo q pase de totalTemporadas y evaluacion por q el query ya viene fijo
    public void filtrarSeriesPorTemporadaYEvaluacion(){
        System.out.println("¿Filtrar séries con cuántas temporadas? ");
        var totalTemporadas = teclado.nextInt();
        teclado.nextLine();
        System.out.println("a partir de cual valor quieres hacer la evaluacion? ");
        //var evaluacion = teclado.nextDouble();
        double evaluacion = Double.parseDouble(teclado.next().replace(",", ".")); //cuando escribo por ejm 7.5 me lanza error por q el S.O pide q sea con coma, este codigo ya deja con el punto
        teclado.nextLine();

        //forma fea
        //List<Serie> filtroSeries = repositorio.findByTotalTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(totalTemporadas, evaluacion);

        //forma SQL
        //List<Serie> filtroSeries = repositorio.seriesPorTemporadaTevaluacion();

        //forma jsql
        List<Serie> filtroSeries = repositorio.seriesPorTemporadaTevaluacion(totalTemporadas, evaluacion);
        System.out.println("*** Series filtradas ***");
        filtroSeries.forEach(s ->
                System.out.println(s.getTitulo() + "  - evaluacion: " + s.getEvaluacion()));
    }

    //buscar episodios por titulo
    private void buscarEpisodiosPorTitulo() {
        System.out.println("escribe el nombre del episodio que deseas buscar");
        var nombreEpisodio = teclado.nextLine();
        List<Episodio> episodiosEncontrados = repositorio.episodiosPorNombre(nombreEpisodio);
        episodiosEncontrados.forEach(e -> System.out.printf("Serie: %s Temporada %s Episodio %s Evaluación %s\n",  //printf da personalizacion a el mensaje q quede mas bonito, $s para strings, marcadro de posicion para variable tipo string
                e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo(), e.getEvaluacion())); //n e.getSerie().getTitulo(), se está accediendo a la información de la serie a la que pertenece cada episodio e.
    }

    //buscar 5 episodios por serie
    private void buscarTop5Episodios() {
        buscarSeriesPorTitulo();
        if (serieBuscada.isPresent()) {
            Serie serie = serieBuscada.get();
            List<Episodio> topEpisodios = repositorio.top5Episodios(serie);
            topEpisodios.forEach(e -> System.out.printf("Serie: %s - Temporada %s - Episodio %s - Evaluación %s\n",
                    e.getSerie().getTitulo(), e.getTemporada(), e.getTitulo(), e.getEvaluacion()));

        } //si no esta la serie el metodo buscarSeriesPorTitulo(); lo trae
    }
}

