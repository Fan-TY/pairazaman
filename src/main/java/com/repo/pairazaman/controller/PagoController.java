package com.repo.pairazaman.controller;

import com.repo.pairazaman.cliente.Cliente;
import com.repo.pairazaman.historial.Pago;
import com.repo.pairazaman.historial.PagoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String estado,
                         @RequestParam(required = false) String filtro,
                         Model model) {
        List<Pago> pagos = pagoService.listarPagos(estado, filtro);
        List<Pago> morosos = pagoService.listarMorosos();

        BigDecimal totalRecaudado = pagos.stream()
                .filter(p -> "PAGADO".equalsIgnoreCase(p.getEstado()) && p.getMonto() != null)
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPorCobrar = pagos.stream()
                .filter(p -> !"PAGADO".equalsIgnoreCase(p.getEstado()) && p.getMonto() != null)
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("pagos", pagos);
        model.addAttribute("estadoSeleccionado", estado != null ? estado : "");
        model.addAttribute("filtro", filtro != null ? filtro : "");
        model.addAttribute("totalRecaudado", totalRecaudado);
        model.addAttribute("totalPorCobrar", totalPorCobrar);
        model.addAttribute("cantidadMorosos", morosos.size());
        return "pagos/listar";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(@RequestParam(required = false) String q,
                                  @RequestParam(required = false) Long clienteId,
                                  Model model) {
        List<Cliente> clientes = pagoService.buscarClientes(q);
        model.addAttribute("clientes", clientes);
        model.addAttribute("q", q != null ? q : "");
        model.addAttribute("fechaHoy", LocalDate.now());

        if (clienteId != null) {
            Cliente clienteSeleccionado = pagoService.obtenerCliente(clienteId);
            BigDecimal deudaActual = pagoService.obtenerDeudaCliente(clienteId);
            List<Pago> cuotasPendientes = pagoService.obtenerCuotasPendientesCliente(clienteId);

            model.addAttribute("clienteSeleccionado", clienteSeleccionado);
            model.addAttribute("deudaActual", deudaActual);
            model.addAttribute("cuotasPendientes", cuotasPendientes);
        }

        return "pagos/form";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam Long clienteId,
                          @RequestParam(required = false) Long cuotaPendienteId,
                          @RequestParam String concepto,
                          @RequestParam(required = false) String medioPago,
                          @RequestParam BigDecimal monto,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaPago,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaVencimiento,
                          @RequestParam(defaultValue = "PAGADO") String estado,
                          @RequestParam(required = false) String observacion,
                          RedirectAttributes redirectAttributes) {
        try {
            Pago pagoGuardado = pagoService.registrarPago(
                    clienteId, cuotaPendienteId, concepto, medioPago,
                    monto, fechaPago, fechaVencimiento, estado, observacion
            );

            if ("PAGADO".equalsIgnoreCase(pagoGuardado.getEstado())) {
                redirectAttributes.addFlashAttribute("exito",
                        "Pago registrado correctamente. Comprobante emitido: " + pagoGuardado.getNumeroComprobante());
                return "redirect:/pagos/comprobante/" + pagoGuardado.getId();
            } else {
                redirectAttributes.addFlashAttribute("exito",
                        "Cuenta por cobrar (" + pagoGuardado.getEstado() + ") registrada correctamente.");
                return "redirect:/pagos";
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/pagos/nuevo?clienteId=" + clienteId;
        }
    }

    @PostMapping("/{id}/cobrar")
    public String cobrarCuota(@PathVariable Long id,
                              @RequestParam(defaultValue = "EFECTIVO") String medioPago,
                              RedirectAttributes redirectAttributes) {
        Pago pago = pagoService.marcarComoPagado(id, medioPago);
        redirectAttributes.addFlashAttribute("exito",
                "Deuda cobrada exitosamente. Comprobante generado: " + pago.getNumeroComprobante());
        return "redirect:/pagos/comprobante/" + pago.getId();
    }

    @GetMapping("/comprobante/{id}")
    public String verComprobante(@PathVariable Long id, Model model) {
        Pago pago = pagoService.obtenerPagoPorId(id);
        BigDecimal deudaRestante = pagoService.obtenerDeudaCliente(pago.getCliente().getId());

        model.addAttribute("pago", pago);
        model.addAttribute("cliente", pago.getCliente());
        model.addAttribute("deudaRestante", deudaRestante);
        return "pagos/comprobante";
    }

    @GetMapping("/morosos")
    public String listarMorosos(Model model) {
        List<Pago> morosos = pagoService.listarMorosos();
        BigDecimal totalVencido = morosos.stream()
                .filter(p -> p.getMonto() != null)
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("morosos", morosos);
        model.addAttribute("totalVencido", totalVencido);
        return "pagos/morosos";
    }
}
