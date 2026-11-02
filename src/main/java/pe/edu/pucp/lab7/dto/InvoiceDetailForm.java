package pe.edu.pucp.lab7.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class InvoiceDetailForm {

    @NotNull(message = "Selecciona un producto.")
    private Integer productId;

    @NotNull(message = "Ingresa la cantidad.")
    @Positive(message = "La cantidad debe ser mayor que cero.")
    private Integer quantity;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}