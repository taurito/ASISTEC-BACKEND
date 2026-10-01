package tec.asistencias.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tec.asistencias.entity.EstadoAsistencia;
import tec.asistencias.repository.EstadoAsistenciaRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EstadoAsistenciaService {

    private final EstadoAsistenciaRepository estadoAsistenciaRepository;

    public List<EstadoAsistencia> listarTodas(){
        return estadoAsistenciaRepository.findAll();
    }

    public Optional<EstadoAsistencia> buscarPorId(Long id){
        return estadoAsistenciaRepository.findById(id);
    }

    public EstadoAsistencia guardar(EstadoAsistencia estadoAsistencia){
        return estadoAsistenciaRepository.save(estadoAsistencia);
    }

    public void eliminar(Long id){
        estadoAsistenciaRepository.deleteById(id);
    }
}
