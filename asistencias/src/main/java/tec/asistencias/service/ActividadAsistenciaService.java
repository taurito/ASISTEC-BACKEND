package tec.asistencias.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tec.asistencias.entity.ActividadAsistencia;
import tec.asistencias.repository.ActividadAsistenciaRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActividadAsistenciaService {

    private final ActividadAsistenciaRepository actividadAsistenciaRepository;

    public List<ActividadAsistencia> listarTodos(){
        return actividadAsistenciaRepository.findAll();
    }

    public Optional<ActividadAsistencia> buscarPorId(Long id){
        return actividadAsistenciaRepository.findById(id);
    }

    public List<ActividadAsistencia> listarPorAsistencia(Long asistenciaId){

        return actividadAsistenciaRepository.findByAsistenciaIdOrderByFechaActividadAsc(asistenciaId);
    }

    public ActividadAsistencia guardar(ActividadAsistencia actividad){
        return actividadAsistenciaRepository.save(actividad);
    }

    public void eliminar(Long id){
        actividadAsistenciaRepository.deleteById(id);
    }
}
