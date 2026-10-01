package tec.asistencias.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tec.asistencias.entity.Marca;
import tec.asistencias.repository.MarcaRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MarcaService {

    private final MarcaRepository marcaRepository;

    public List<Marca> listarTodos(){
        return marcaRepository.findAll();
    }

    public Optional<Marca> buscarPorId(Long id){
        return marcaRepository.findById(id);
    }

    public Marca guardar(Marca marca){
        return marcaRepository.save(marca);
    }

    public void eliminar(Long id){
        marcaRepository.deleteById(id);
    }
}
