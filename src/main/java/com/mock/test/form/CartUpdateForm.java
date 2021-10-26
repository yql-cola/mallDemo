package com.mock.test.form;

import lombok.Data;

@Data
public class CartUpdateForm {
    private Integer quantity;
    private Boolean selected;

    public CartUpdateForm() {
    }

    public CartUpdateForm(Integer quantity, Boolean selected) {
        this.quantity = quantity;
        this.selected = selected;
    }
}
