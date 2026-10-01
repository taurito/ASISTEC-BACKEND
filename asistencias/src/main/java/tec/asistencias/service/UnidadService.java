package tec.asistencias.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tec.asistencias.entity.Unidad;
import tec.asistencias.repository.UnidadRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UnidadService {

    private final UnidadRepository unidadRepository;

    public List<Unidad> listarTodas(){
        return unidadRepository.findAll();
    }

    public Optional<Unidad> buscarPorId(Long id){
        return unidadRepository.findById(id);
    }

    public Unidad guardar(Unidad unidad){
        return unidadRepository.save(unidad);
    }

    public void eliminar(Long id){
        unidadRepository.deleteById(id);
    }
}
