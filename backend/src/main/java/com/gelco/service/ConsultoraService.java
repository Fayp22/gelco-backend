package com.gelco.service;

import com.gelco.dto.ConsultoraResponse;
import com.gelco.model.Consultora;
import com.gelco.model.Usuario;
import com.gelco.repository.ConsultoraRepository;
import com.gelco.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultoraService {

    private final ConsultoraRepository consultoraRepository;
    private final UsuarioRepository usuarioRepository;

    public List<ConsultoraResponse> getAllConsultoras() {
        try {
            return consultoraRepository.findAll()
                    .stream()
                    .map(ConsultoraResponse::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener consultoras: " + e.getMessage());
        }
    }



    public ConsultoraResponse getConsultoraById(Long id) {
        try {
            Consultora consultora = consultoraRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Consultora no encontrada"));
            return ConsultoraResponse.fromEntity(consultora);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener consultora: " + e.getMessage());
        }
    }

    public ConsultoraResponse getConsultoraByUsuario(Long usuarioId) {
        try {
            Consultora consultora = consultoraRepository.findByUsuarioId(usuarioId)
                    .orElseThrow(() -> new IllegalArgumentException("Consultora no encontrada para este usuario"));
            return ConsultoraResponse.fromEntity(consultora);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener consultora: " + e.getMessage());
        }
    }

    public ConsultoraResponse createConsultora(Long usuarioId, String dni, String direccion, String telefono, String nivel) {
        try {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            Consultora consultora = new Consultora();
            consultora.setUsuario(usuario);
            consultora.setDni(dni);
            consultora.setDireccion(direccion);
            consultora.setTelefono(telefono);
            consultora.setNivel(nivel);

            Consultora savedConsultora = consultoraRepository.save(consultora);
            return ConsultoraResponse.fromEntity(savedConsultora);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al crear consultora: " + e.getMessage());
        }
    }

    public ConsultoraResponse updateConsultora(Long id, String dni, String direccion, String telefono, String nivel) {
        try {
            Consultora consultora = consultoraRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Consultora no encontrada"));

            if (dni != null) consultora.setDni(dni);
            if (direccion != null) consultora.setDireccion(direccion);
            if (telefono != null) consultora.setTelefono(telefono);
            if (nivel != null) consultora.setNivel(nivel);

            Consultora updatedConsultora = consultoraRepository.save(consultora);
            return ConsultoraResponse.fromEntity(updatedConsultora);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar consultora: " + e.getMessage());
        }
    }

    public void deleteConsultora(Long id) {
        try {
            Consultora consultora = consultoraRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Consultora no encontrada"));
            consultoraRepository.delete(consultora);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar consultora: " + e.getMessage());
        }
    }
}
