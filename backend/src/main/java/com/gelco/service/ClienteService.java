package com.gelco.service;

import com.gelco.model.Cliente;
import com.gelco.repository.ClienteRepository;
import com.gelco.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;

    public List<Cliente> getAll() {
        return clienteRepository.findAll();
    }

    public Cliente getById(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
    }

    public Cliente create(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Cliente update(Long id, Cliente datos) {
        Cliente cliente = getById(id);
        if (datos.getNombre() != null) cliente.setNombre(datos.getNombre());
        if (datos.getTelefono() != null) cliente.setTelefono(datos.getTelefono());
        if (datos.getDireccion() != null) cliente.setDireccion(datos.getDireccion());
        if (datos.getPreferencias() != null) cliente.setPreferencias(datos.getPreferencias());
        return clienteRepository.save(cliente);
    }

    public void delete(Long id) {
        clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        clienteRepository.deleteById(id);
    }

    public Map<String, Object> getClienteConStats(Long clienteId) {
        Cliente cliente = getById(clienteId);
        Map<String, Object> result = new HashMap<>();
        result.put("cliente", cliente);
        result.put("totalPedidos", pedidoRepository.countByClienteId(clienteId));
        result.put("tienePendiente", pedidoRepository
                .existsByClienteIdAndEstado(clienteId, "Pendiente"));
        return result;
    }

    public List<Map<String, Object>> getAllConStats() {
        return clienteRepository.findAll().stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("nombre", c.getNombre());
            map.put("telefono", c.getTelefono());
            map.put("direccion", c.getDireccion());
            map.put("preferencias", c.getPreferencias());
            map.put("totalPedidos", pedidoRepository.countByClienteId(c.getId()));
            map.put("tienePendiente", pedidoRepository
                    .existsByClienteIdAndEstado(c.getId(), "Pendiente"));
            return map;
        }).toList();
    }
}