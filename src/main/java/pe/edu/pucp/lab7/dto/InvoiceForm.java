package pe.edu.pucp.lab7.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InvoiceForm {

    @NotBlank(message = "Selecciona el tipo de comprobante.")
    @Pattern(
            regexp = "Factura|Boleta",
            message = "El tipo debe ser Factura o Boleta."
    )
    private String type;

    @NotNull(message = "Selecciona la fecha.")
    @PastOrPresent(message = "La fecha no puede ser futura.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull(message = "Selecciona un cliente.")
    private Integer customerId;

    @Valid
    @NotEmpty(message = "Agrega al menos un producto.")
    private List<InvoiceDetailForm> details = new ArrayList<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public List<InvoiceDetailForm> getDetails() {
        return details;
    }

    public void setDetails(List<InvoiceDetailForm> details) {
        this.details = details;
    }
}
