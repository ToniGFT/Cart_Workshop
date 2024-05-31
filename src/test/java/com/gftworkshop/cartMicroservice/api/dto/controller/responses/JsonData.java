package com.gftworkshop.cartMicroservice.api.dto.controller.responses;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum JsonData {

    PRODUCT("""
            {
              "cart": {
                "id": 1
              },
              "productId": 1,
              "productName": "Pride and Prejudice",
              "productCategory": "Books",
              "productDescription": "Book by Jane Austen",
              "quantity": 10,
              "price": 20.00
            }"""),

    PRODUCTS(
            """
                    [
                        {
                            "id": 1,
                            "name": "Product1",
                            "description": "Description1",
                            "price": 10.0,
                            "currentStock": 100,
                            "weight": 1.0
                        },
                        {
                              "id": 2,
                              "productId": 2,
                              "productName": "Building Blocks",
                              "productDescription": "Agent word occur number chair.",
                              "quantity": 2,
                              "price": 100.0
                        }
                    ]
                    """
    ),

    VOLUMEPROMOTION_PRODUCT("""
                {
                    "id": 1,
                    "name": "string",
                    "description": "string",
                    "price": 10.0,
                    "categoryId": 0,
                    "weight": 0,
                    "currentStock": 100,
                    "minStock": 0
                }
            """),

    USER("""
                {
                    "id": %d,
                    "username": "john_doe",
                    "email": "john@example.com"
                }
            """);

    private final String json;

    public String getJson() {
        return json;
    }

    public String getJson(int id) {
        if (this == USER) {
            return String.format(json, id);
        }
        return json;
    }
}
