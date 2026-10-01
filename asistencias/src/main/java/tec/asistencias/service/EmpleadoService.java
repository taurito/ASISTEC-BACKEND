package tec.asistencias.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tec.asistencias.entity.Empleado;
import tec.asistencias.repository.EmpleadoRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    public List<Empleado> listarTodos(){
        return empleadoRepository.findAll();
    }

    public Optional<Empleado> buscarPorId(Long id){
        return empleadoRepository.findById(id);
    }

    public Empleado guardar(Empleado empleado){
        return empleadoRepository.save(empleado);
    }

    public void eliminar(Long id){
        empleadoRepository.deleteById(id);
    }
}
