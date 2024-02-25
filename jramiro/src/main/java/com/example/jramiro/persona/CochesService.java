package com.example.jramiro.persona;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class CochesService {
    @Autowired
    private CochesRepository cocheRepository;

    public void crearCoches(Coches coches) {
        cocheRepository.save(coches);
    }

    public boolean borrarCoche(Integer id) {
        if (cocheRepository.findById(id).isPresent()) {
            cocheRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean borrarTodosCoches() {
        cocheRepository.deleteAll();
        return true;
    }

    public Optional<Coches> buscarCoche(Integer id) {
        return cocheRepository.findById(id);
    }

    public List<Coches> obtenerTodosCoches() {
        return cocheRepository.findAll();
    }

    public Coches actualizarCoche(Coches cocheOriginal, Coches cocheModificado) {
        if (cocheModificado.getMarca() != null) {
            cocheOriginal.setMarca(cocheModificado.getMarca());
        }
        if (cocheModificado.getModelo() != null) {
            cocheOriginal.setModelo(cocheModificado.getModelo());
        }
        if (cocheModificado.getTipo() != null) {
            cocheOriginal.setTipo(cocheModificado.getTipo());
        }
        if (cocheModificado.getSaldo() >= 0) {
            cocheOriginal.setSaldo(cocheModificado.getSaldo());
        }
        return cocheRepository.save(cocheOriginal);
    }

    public int aumentarSaldo(Integer id, float cantidad) {
        Optional<Coches> cocheOptional = cocheRepository.findById(id);
        if (cocheOptional.isPresent()) {
            Coches coche = cocheOptional.get();
            float cocheAnt = coche.getSaldo();
            float cocheNew = cocheAnt + cantidad;
            coche.setSaldo(cocheNew);
            cocheRepository.save(coche);
            if (cocheAnt == 0) {
                return 1;
            } else {
                return 2;
            }
        } else {
            return 3;
        }
    }

    public int reducirSaldo(Integer id, float cantidad) {
        Optional<Coches> cocheOptional = cocheRepository.findById(id);
        if (cocheOptional.isPresent()) {
            Coches coche = cocheOptional.get();
            float cocheAnt = coche.getSaldo();
            float cocheNew = cocheAnt - cantidad;
            if (cocheNew < 0) {
                coche.setSaldo(cocheNew);
                cocheRepository.save(coche);
                return 1;
            } else {
                coche.setSaldo(cocheNew);
                cocheRepository.save(coche);
                return 2;
            }
        } else {
            return 3;
        }
    }

    public int mediaSaldo() {
        List<Coches> cocheOptional = cocheRepository.findAll();
        int mediaSaldo = 0;
        for (int i = 0; i < cocheOptional.size(); i++) {
            mediaSaldo += cocheOptional.get(i).getSaldo();
        }
        mediaSaldo = mediaSaldo / cocheOptional.size();
        return mediaSaldo;
    }
}