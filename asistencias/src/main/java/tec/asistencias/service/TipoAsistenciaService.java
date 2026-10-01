package tec.asistencias.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tec.asistencias.entity.TipoAsistencia;
import tec.asistencias.repository.TipoAsistenciaRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TipoAsistenciaService {

    private final TipoAsistenciaRepository tipoAsistenciaRepository;

    public List<TipoAsistencia> listarTodos(){
        return tipoAsistenciaRepository.findAll();
    }

    public Optional<TipoAsistencia> buscarPorId(Long id){
        return tipoAsistenciaRepository.findById(id);
    }

    public TipoAsistencia guardar(TipoAsistencia tipoAsistencia){
        return tipoAsistenciaRepository.save(tipoAsistencia);
    }

    public void eliminar(Long id){
        tipoAsistenciaRepository.deleteById(id);
    }
}
