package com.aluracursos.screenmatch.controller;

import com.aluracursos.screenmatch.dto.EpisodioDTO;
import com.aluracursos.screenmatch.dto.SerieDTO;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.repositorio.SerieRepository;
import com.aluracursos.screenmatch.service.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/series")
public class SerieController {

    @Autowired
    private SeriesService servicio;

    @GetMapping()
    public List<SerieDTO> getSeries(){
        return servicio.getSeries();
    }

    @GetMapping("/top5")
    public List<SerieDTO> getTop5(){
        return servicio.getTop5();
    }

    @GetMapping("/lanzamientos")
    public List<SerieDTO> getRecentReleases(){
        return servicio.getRecentReleases();
    }

    @GetMapping("/{id}")
    public SerieDTO getById(@PathVariable Long id){
        return servicio.getById(id);
    }

    @GetMapping("/{id}/temporadas/todas")
    public List<EpisodioDTO> getAllSeasons(@PathVariable Long id){
        return servicio.getAllSeasons(id);
    }

    @GetMapping("/{id}/temporadas/{numeroTemporada}")
    public List<EpisodioDTO> getEpisodesBySeason(@PathVariable Long id, @PathVariable Long numeroTemporada){
        return servicio.getEpisodesBySeason(id, numeroTemporada);
    }

    @GetMapping("/categoria/{nombreGenero}")
    public List<SerieDTO> getSeriesByGenre(@PathVariable String nombreGenero){
        return servicio.getSeriesByGenre(nombreGenero);
    }
}
