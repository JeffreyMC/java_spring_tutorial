package com.aluracursos.screenmatch.service;

import com.aluracursos.screenmatch.dto.EpisodioDTO;
import com.aluracursos.screenmatch.dto.SerieDTO;
import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.Serie;
import com.aluracursos.screenmatch.repositorio.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SeriesService {
    @Autowired
    private SerieRepository repository;

    public List<SerieDTO> getSeries(){
        return convierteDatos(repository.findAll());
    }

    public List<SerieDTO> getTop5() {
        return convierteDatos(repository.findTop5ByOrderByEvaluacionDesc());
    }

    public List<SerieDTO> getRecentReleases(){
        return convierteDatos(repository.lanzamientosMasRecientes());
    }

    public SerieDTO getById(Long id){
        Optional<Serie> serie = repository.findById(id);
        if(serie.isPresent()){
            Serie s = serie.get();
            return new SerieDTO(s.getId(), s.getTitulo(), s.getTotalDeTemporadas(), s.getEvaluacion(),
                    s.getGenero(), s.getActores(), s.getSinopsis(), s.getPoster());
        }
        return null;
    }

    public List<EpisodioDTO> getAllSeasons(Long id) {
        Optional<Serie> serie = repository.findById(id);

        if(serie.isPresent()){
            Serie s = serie.get();
            return s.getEpisodios().stream()
                    .map(e -> new EpisodioDTO(e.getTemporada(), e.getTitulo(), e.getNumeroEpisodio()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    public List<EpisodioDTO> getEpisodesBySeason(Long id, Long numeroTemporada) {
        return repository.getEpisodesBySeason(id, numeroTemporada).stream()
                .map(e -> new EpisodioDTO(e.getTemporada(), e.getTitulo(), e.getNumeroEpisodio()))
                .collect(Collectors.toList());
    }

    public List<SerieDTO> getSeriesByGenre(String nombreGenero) {
        Categoria categoria = Categoria.fromEspanol(nombreGenero);

        return convierteDatos(repository.findByGenero(categoria));
    }

    public List<SerieDTO> convierteDatos(List<Serie> serie){
        return serie.stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalDeTemporadas(), s.getEvaluacion(),
                        s.getGenero(), s.getActores(), s.getSinopsis(), s.getPoster()))
                .collect(Collectors.toList());
    }
}
