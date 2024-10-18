package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.repositorio.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;
import jdk.swing.interop.SwingInterOpUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private final String URL_BASE = "http://www.omdbapi.com/?t=";
    private final String API_KEY = System.getenv("API_KEY_OMDB");
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie> datosSeries = new ArrayList<>();
    private SerieRepository repositorio;
    private List<Serie> series;

    public Principal(SerieRepository repository) {
        this.repositorio = repository;
    }

    public void mostrarMenu() {
        var opcion = -1;
        while(opcion != 0){
            var menu = """
                    1 - Buscar series
                    2 - Buscar episodio
                    3 - Mostrar series buscadas
                    4 - Buscar serie por titulo
                    5 - Mostrar top 5 de series
                    6 - Mostrar series por genero
                    7 - Buscar serie por numero de temporadas y calificacion
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion){
                case 1 -> buscarSerieWeb();
                case 2 -> buscarEpisodioPorSerie();
                case 3 -> mostrarSeriesBuscadas();
                case 4 -> buscarSeriePorTitulo();
                case 5 -> mostrarTop5Series();
                case 6 -> buscarSeriePorCategoria();
                case 7 -> buscarSeriePorCantTemporadaYCalificacion();
                case 0 -> System.out.println("Cerrando la aplicacion...");
                default -> System.out.println("Opcion invalida. Intente de nuevo");
            }
        }
    }

    private DatosSerie getDatosSerie(){
        System.out.println("Escribe el nombre de la serie que deseas buscar: ");
        var nombreSerie = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE+nombreSerie.replace(" ", "+")+API_KEY);
        System.out.println(json);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }

    private void buscarEpisodioPorSerie(){
        mostrarSeriesBuscadas();
        System.out.println("Escribe el nombre de la serie en la que deseas buscar los episodios: ");
        var nombreSerie = teclado.nextLine();

        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                .findFirst();

        if(serie.isPresent()){
            var serieEncontrada = serie.get();
            List<DatosTemporadas> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalDeTemporadas(); i++) {
                var json = consumoAPI.obtenerDatos(URL_BASE+serieEncontrada.getTitulo().replace(" ", "+")+ "&season=" + i +API_KEY);
                DatosTemporadas datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numeroTemporada(), e)))
                    .collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        }else{
            System.out.println("No se encontro la serie.");
        }

    }

    private void buscarSerieWeb(){
        DatosSerie datos = getDatosSerie();
        Serie serie = new Serie(datos);
        repositorio.save(serie);
        //datosSeries.add(datos);
        System.out.println(datos);
    }

    private void mostrarSeriesBuscadas() {
        series = repositorio.findAll();

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo(){

        System.out.println("Escribe el nombre de la serie que deseas buscar: ");
        var nombreSerie = teclado.nextLine();
        Optional<Serie> serieBuscada = repositorio.findByTituloContainsIgnoreCase(nombreSerie);

        if(serieBuscada.isPresent()){
            System.out.println("La serie buscada es: " + serieBuscada.get());
        }else{
            System.out.println("Serie no encontrada");
        }
    }

    private void mostrarTop5Series(){
        List<Serie> topSeries = repositorio.findTop5ByOrderByEvaluacionDesc();

        topSeries.forEach(s -> System.out.println("Nombre de la serie: " + s.getTitulo() +
                " Evaluacion: " + s.getEvaluacion()));
    }

    private void buscarSeriePorCategoria(){
        System.out.println("Escriba el genero/categoria de la serie: ");
        var genero = teclado.nextLine();
        var categoria = Categoria.fromEspanol(genero);

        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);

        System.out.println("Las serie del genero " + genero + " son: ");
        seriesPorCategoria.forEach(System.out::println);
    }

    private void buscarSeriePorCantTemporadaYCalificacion(){
        System.out.println("Escriba la cantidad de temporadas: ");
        var totalTemporadas = Integer.valueOf(teclado.nextLine());
        System.out.println("Escribe la calificacion minima (de 0 a 10)");
        var calificacion = Double.valueOf(teclado.nextLine());

        List<Serie> serieEncontradas = repositorio
                .findByTotalDeTemporadasAndEvaluacionGreaterThanEqual(totalTemporadas, calificacion);

        System.out.println("Las series encontradas son: ");
        serieEncontradas.forEach(s -> System.out.println("Nombre: " + s.getTitulo() +
                ", Calificacion: " + s.getEvaluacion() +
                ", Total de temporadas: " + s.getTotalDeTemporadas()));

    }

}
