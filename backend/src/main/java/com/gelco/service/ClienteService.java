package com.gelco.service;

import com.gelco.model.Cliente;
import com.gelco.model.Consultora;
import com.gelco.repository.ClienteRepository;
import com.gelco.repository.ConsultoraRepository;
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
    private final ConsultoraRepository consultoraRepository;

    public List<Cliente> getAllByConsultora(Long consultoraId) {
        return clienteRepository.findByConsultoraId(consultoraId);
    }

    public Cliente getByIdAndConsultora(Long id, Long consultoraId) {
        return clienteRepository.findByIdAndConsultoraId(id, consultoraId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
    }

    public Cliente create(Cliente cliente, Long consultoraId) {
        Consultora consultora = consultoraRepository.findById(consultoraId)
                .orElseThrow(() -> new IllegalArgumentException("Consultora no encontrada"));
        cliente.setConsultora(consultora);
        return clienteRepository.save(cliente);
    }

    public Cliente update(Long id, Cliente datos, Long consultoraId) {
        Cliente cliente = getByIdAndConsultora(id, consultoraId);
        if (datos.getNombre() != null) cliente.setNombre(datos.getNombre());
        if (datos.getTelefono() != null) cliente.setTelefono(datos.getTelefono());
        if (datos.getDireccion() != null) cliente.setDireccion(datos.getDireccion());
        if (datos.getPreferencias() != null) cliente.setPreferencias(datos.getPreferencias());
        return clienteRepository.save(cliente);
    }

    public void delete(Long id, Long consultoraId) {
        if (!clienteRepository.existsByIdAndConsultoraId(id, consultoraId)) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }
        clienteRepository.deleteById(id);
    }

    public Map<String, Object> getClienteConStats(Long clienteId, Long consultoraId) {
        Cliente cliente = getByIdAndConsultora(clienteId, consultoraId);
        Map<String, Object> result = new HashMap<>();
        result.put("id", cliente.getId());
        result.put("nombre", cliente.getNombre());
        result.put("telefono", cliente.getTelefono());
        result.put("direccion", cliente.getDireccion());
        result.put("preferencias", cliente.getPreferencias());
        result.put("totalPedidos", pedidoRepository.countByClienteId(clienteId));

        boolean tienePendiente = pedidoRepository.existsByClienteIdAndEstado(clienteId, "En proceso") ||
                pedidoRepository.existsByClienteIdAndEstado(clienteId, "En camino");

        result.put("tienePendiente", tienePendiente);
        return result;
    }

    public List<Map<String, Object>> getAllConStatsByConsultora(Long consultoraId) {
        Map<Long, Boolean> pendientesMap = new HashMap<>();
        pedidoRepository.countPendientesByConsultoraIdGroupByCliente(consultoraId)
                .forEach(row -> pendientesMap.put((Long) row[0], true));

        return clienteRepository.findByConsultoraId(consultoraId).stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("nombre", c.getNombre());
            map.put("telefono", c.getTelefono());
            map.put("direccion", c.getDireccion());
            map.put("preferencias", c.getPreferencias());
            map.put("totalPedidos", pedidoRepository.countByClienteId(c.getId()));
            map.put("tienePendiente", pendientesMap.getOrDefault(c.getId(), false));
            return map;
        }).toList();
    }
}