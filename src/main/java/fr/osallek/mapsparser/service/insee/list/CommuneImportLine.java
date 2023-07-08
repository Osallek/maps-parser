package com.oveo.api.controller.object.dto.bo.vehicle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.oveo.common.model.entity.vehicle.VehiclePrice;

@JsonPropertyOrder({"Vin", "Prix achat immédiat", "Prix de départ enchère", "Prix de réserve enchère"})
public record VehiclePriceImportLine(@JsonProperty("Vin") String vin, @JsonProperty("Prix achat immédiat") Double scPrice,
                                     @JsonProperty("Prix de départ enchère") Double startPrice, @JsonProperty("Prix de réserve enchère") Double reservePrice,
                                     @JsonProperty("Cotation ALCOPA") Double alcopaPrice) {

    @JsonIgnore
    public VehiclePrice toVehiclePrice() {
        VehiclePrice price = new VehiclePrice();
        price.setVin(this.vin);
        price.setScPrice(this.scPrice);
        price.setStartPrice(this.startPrice);

        if (this.reservePrice != null && this.startPrice != null) {
            price.setReservePrice(Math.max(this.startPrice, this.reservePrice));
        }

        price.setAtcPrice(null);
        price.setAlcopaPrice(this.alcopaPrice);

        return price;
    }
}
