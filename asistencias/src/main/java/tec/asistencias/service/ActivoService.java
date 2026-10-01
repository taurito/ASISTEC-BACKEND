package tec.asistencias.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tec.asistencias.entity.Activo;
import tec.asistencias.repository.ActivoRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActivoService {

    private final ActivoRepository activoRepository;

    public List<Activo> listarTodos(){
        return activoRepository.findAll();
    }

    public Optional<Activo> buscarPorId(Long id){
        return activoRepository.findById(id);
    }

    public Activo guardar(Activo activo){
        return activoRepository.save(activo);
    }

    public void eliminar(Long id){
        activoRepository.deleteById(id);
    }
}
