package tec.asistencias.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tec.asistencias.entity.TipoActivo;
import tec.asistencias.repository.TipoActivoRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TipoActivoService {

    private final TipoActivoRepository tipoActivoRepository;

    public List<TipoActivo> listarTodos(){
        return tipoActivoRepository.findAll();
    }

    public Optional<TipoActivo> buscarPorId(Long id){
        return tipoActivoRepository.findById(id);
    }

    public TipoActivo guardar(TipoActivo tipoActivo){
        return tipoActivoRepository.save(tipoActivo);
    }

    public void eliminar(Long id){
        tipoActivoRepository.deleteById(id);
    }
}
