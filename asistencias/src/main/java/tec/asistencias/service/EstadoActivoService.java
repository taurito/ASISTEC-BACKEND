package tec.asistencias.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tec.asistencias.entity.EstadoActivo;
import tec.asistencias.repository.EstadoActivoRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EstadoActivoService {

    private final EstadoActivoRepository estadoActivoRepository;

    public List<EstadoActivo> listaTodos(){
        return estadoActivoRepository.findAll();
    }

    public Optional<EstadoActivo> buscarPorId(Long id){
        return estadoActivoRepository.findById(id);
    }

    public EstadoActivo guardar(EstadoActivo estadoActivo){
        return estadoActivoRepository.save(estadoActivo);
    }

    public void eliminar(Long id){
        estadoActivoRepository.deleteById(id);
    }
}
