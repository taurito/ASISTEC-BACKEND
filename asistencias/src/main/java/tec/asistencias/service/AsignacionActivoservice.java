package tec.asistencias.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tec.asistencias.entity.AsignacionActivo;
import tec.asistencias.repository.AsignacionActivoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AsignacionActivoservice{

    private final AsignacionActivoRepository asignacionActivoRepository;

    public List<AsignacionActivo> listarTodas(){
        return asignacionActivoRepository.findAll();
    }

    public Optional<AsignacionActivo> buscarPorId(Long id){
        return asignacionActivoRepository.findById(id);
    }

    public AsignacionActivo guardar(AsignacionActivo asignacionActivo){

        //Verificar si el activo ya tiene una asignacion vigente
        Optional<AsignacionActivo> asignacionActual =
                asignacionActivoRepository
                        .findByActivoIdAndVigenteTrue(
                                asignacionActivo.getActivo().getId()
                        );
        //Si estamos creando una nueva asignacion
        //y ya existe una vigente. la cerramos.
        if(asignacionActivo.getId() == null && asignacionActual.isPresent()){

            AsignacionActivo anterior =asignacionActual.get();

            anterior.setVigente(false);
            anterior.setFechaDevolucion(LocalDateTime.now());

            asignacionActivoRepository.save(anterior);
        }

        return asignacionActivoRepository.save(asignacionActivo);
    }

    public Optional<AsignacionActivo> buscarAsignacionActual(Long activoId){
        return asignacionActivoRepository.findByActivoIdAndVigenteTrue(activoId);
    }

    public List<AsignacionActivo> listarHistorialPorActivo(Long activoId){
        return asignacionActivoRepository.findByActivoIdOrderByFechaAsignacionDesc(activoId);
    }

    public void eliminar(Long id){
        asignacionActivoRepository.deleteById(id);
    }
}
