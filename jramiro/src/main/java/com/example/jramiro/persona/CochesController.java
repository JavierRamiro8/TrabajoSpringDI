package com.example.jramiro.persona;

import java.util.Optional;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping(path = "/coche")

@RequiredArgsConstructor
public class CochesController {
    @Autowired

    private final CochesService cocheService;
    private final CochesRepository cocheRepository;

    @PostMapping(path = "/crear")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Coche Creado correctamente"),
            @ApiResponse(responseCode = "205", description = "Coche con datos parciales")
    })
    public ResponseEntity<String> insertarPersona(@RequestBody Coches coches) {
        if (coches.getMarca().isEmpty() || coches.getModelo().isEmpty() || coches.getSaldo() == 0
                || coches.getTipo().isEmpty()) {
            cocheService.crearCoches(coches);
            return ResponseEntity.status(205)
                    .body("Se ha creado el coche pero incompleto, este es el coche creado: \nMarca: "
                            + coches.getMarca() + "\nModelo: " + coches.getModelo() + "\nTipo: " + coches.getTipo()
                            + "\nSaldo: " + coches.getSaldo());
        } else {
            cocheService.crearCoches(coches);
            return ResponseEntity
                    .ok("Coche creado con exito, este es el coche creado: \nMarca: " + coches.getMarca() + "\nModelo: "
                            + coches.getModelo() + "\nTipo: " + coches.getTipo() + "\nSaldo: " + coches.getSaldo());
        }
    }

    @PutMapping("borrarCoche/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Coche Borrado correctamente"),
            @ApiResponse(responseCode = "209", description = "Coche no existe en la Base de Datos")
    })
    public ResponseEntity<String> borrarCoche(@PathVariable String id) {
        if (cocheService.borrarCoche(Integer.parseInt(id))) {
            return ResponseEntity.ok("Se ha borrado el Coche correctamente");
        }
        return ResponseEntity.status(209).body("Ese Coche no existe");
    }

    @PutMapping("borrarTodo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Coche Borrado correctamente"),
            @ApiResponse(responseCode = "209", description = "No hay coches en la Base de Datos")
    })
    public ResponseEntity<String> borrarTodo() {
        if (cocheService.borrarTodosCoches() == true) {
            return ResponseEntity.ok("Se ha borrado todos los coches");
        }
        return ResponseEntity.status(209).body("No existe ningun coche");
    }

    @GetMapping("buscarCoche/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Coche Encontrado"),
            @ApiResponse(responseCode = "209", description = "Coche no encontrado")
    })
    public ResponseEntity<Optional<Coches>> buscarCoche(@PathVariable String id) {
        Optional<Coches> cocheExiste = cocheService.buscarCoche(Integer.parseInt(id));
        if (!cocheExiste.isPresent()) {
            return ResponseEntity.status(209).body(cocheService.buscarCoche(Integer.parseInt(id)));
        }
        return ResponseEntity.ok(cocheService.buscarCoche(Integer.parseInt(id)));
    }

    @GetMapping("obtenerTodosCoches")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Coches encontrados", content = {
                    @Content(schema = @Schema(implementation = Coches.class), mediaType = "application/json") })
    })
    public ResponseEntity<List<Coches>> obtenerCoches() {
        List<Coches> cochesEncontrados = cocheService.obtenerTodosCoches();
        return ResponseEntity.ok(cochesEncontrados);
    }

    @PostMapping(path = "modificarCoche/{id}")

    @ApiResponses({

            @ApiResponse(responseCode = "200", description = "Coche Modificado correctamente"),
            @ApiResponse(responseCode = "201", description = "Coche Parcialmente Modificado correctamente"),
            @ApiResponse(responseCode = "209", description = "Coche No Encontrado")
    })
    public ResponseEntity<String> modificarCoche(@PathVariable String id, @RequestBody Coches cocheModificado) {
        Optional<Coches> cocheOriginalOptional = cocheService.buscarCoche(Integer.parseInt(id));
        if (cocheOriginalOptional.isPresent()) {
            Coches cocheOriginal = cocheOriginalOptional.get();
            Coches cocheActualizado = new Coches();
            cocheActualizado.setId(cocheOriginal.getId());
            cocheActualizado.setMarca(
                    cocheModificado.getMarca() != null ? cocheModificado.getMarca() : cocheOriginal.getMarca());
            cocheActualizado.setModelo(
                    cocheModificado.getModelo() != null ? cocheModificado.getModelo() : cocheOriginal.getModelo());
            cocheActualizado
                    .setTipo(cocheModificado.getTipo() != null ? cocheModificado.getTipo() : cocheOriginal.getTipo());
            cocheActualizado
                    .setSaldo(cocheModificado.getSaldo() >= 0 ? cocheModificado.getSaldo() : cocheOriginal.getSaldo());
            if (sonIguales(cocheOriginal, cocheActualizado)) {
                cocheService.actualizarCoche(cocheOriginal, cocheActualizado);
                return ResponseEntity.ok("Coche modificado completamente, este es el coche que acabas de modificar:\n"
                        + "Marca: " + cocheActualizado.getMarca() + "\n Modelo: " + cocheActualizado.getModelo()
                        + "\nTipo: " + cocheActualizado.getTipo() + "\nSaldo: " + cocheActualizado.getSaldo());
            } else {
                cocheService.actualizarCoche(cocheOriginal, cocheActualizado);
                return ResponseEntity.status(201)
                        .body("Coche modificado parcialmente, este es el coche que acabas de modificar:\n" + "Marca: "
                                + cocheActualizado.getMarca() + "\n Modelo: " + cocheActualizado.getModelo()
                                + "\nTipo: " + cocheActualizado.getTipo() + "\nSaldo: " + cocheActualizado.getSaldo());
            }
        } else {
            return ResponseEntity.status(209).body("Coche no encontrado");
        }
    }

    private boolean sonIguales(Coches cocheOriginal, Coches cocheModificado) {
        int contador = 0;
        if (!cocheOriginal.getMarca().equals(cocheModificado.getMarca())) {
            contador++;
        }
        if (!cocheOriginal.getModelo().equals(cocheModificado.getModelo())) {
            contador++;
        }
        if (!cocheOriginal.getTipo().equals(cocheModificado.getTipo())) {
            contador++;
        }

        if (cocheOriginal.getSaldo() != cocheModificado.getSaldo()) {
            contador++;
        }
        if (contador == 4) {
            return true;
        }
        return false;
    }

    @PatchMapping(path = "aumentarSaldo/{id}")
    @ApiResponses({

            @ApiResponse(responseCode = "200", description = "Saldo del Coche Modificado correctamente"),
            @ApiResponse(responseCode = "201", description = "Saldo Puesto y modificado correctamente"),
            @ApiResponse(responseCode = "209", description = "Coche No Encontrado")
    })

    public ResponseEntity<String> aumentarSaldo(@PathVariable String id, @RequestBody float saldoNuevo) {
        int salidaAumentoSaldo = cocheService.aumentarSaldo(Integer.valueOf(id), saldoNuevo);
        Optional<Coches> cocheOptional = cocheRepository.findById(Integer.parseInt(id));
        if (salidaAumentoSaldo == 1) {
            return ResponseEntity.status(201)
                    .body("Saldo inicializado y puesto con exito, nuevo saldo: " + cocheOptional.get().getSaldo());

        } else if (salidaAumentoSaldo == 2) {
            return ResponseEntity.ok("Saldo Editado con exito, nuevo saldo: " + cocheOptional.get().getSaldo());
        }
        return ResponseEntity.status(209).body("ID del coche no encontrado");
    }

    @PatchMapping(path = "reducirSaldo/{id}")
    @ApiResponses({

            @ApiResponse(responseCode = "200", description = "Saldo del Coche Modificado correctamente"),
            @ApiResponse(responseCode = "201", description = "Saldo Puesto y modificado correctamente"),
            @ApiResponse(responseCode = "209", description = "Coche No Encontrado")
    })

    public ResponseEntity<String> reducirSaldo(@PathVariable String id, @RequestBody float saldoNuevo) {
        int salidaReducirSaldo = cocheService.reducirSaldo(Integer.valueOf(id), saldoNuevo);
        Optional<Coches> cocheOptional = cocheRepository.findById(Integer.parseInt(id));
        if (salidaReducirSaldo == 1) {
            return ResponseEntity.status(201).body(
                    "Saldo reducido y negativo aplicado con exito, nuevo saldo: " + cocheOptional.get().getSaldo());

        } else if (salidaReducirSaldo == 2) {
            return ResponseEntity
                    .ok("Saldo reducido y positivo aplicado con exito, nuevo saldo: " + cocheOptional.get().getSaldo());
        }
        return ResponseEntity.status(209).body("ID del coche no encontrado");
    }

    @GetMapping(path = "mediaSaldo")
    @ApiResponses({

            @ApiResponse(responseCode = "200", description = "media de saldo positivo"),
            @ApiResponse(responseCode = "201", description = "media de saldo negativo")
    })

    public ResponseEntity<String> mediaSaldo() {
        int salidaMediaSaldo = cocheService.mediaSaldo();
        if (salidaMediaSaldo < 0) {
            return ResponseEntity.status(201)
                    .body("media de saldo de los coches negativo, este es la media calculada: " + salidaMediaSaldo);
        }
        return ResponseEntity.status(201)
                .body("media de saldo de los coches positivo, este es la media calculada: " + salidaMediaSaldo);
    }
}
