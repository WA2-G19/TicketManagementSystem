package it.polito.wa2.g19.server.products

class ProductNotFoundException : RuntimeException("There is no object with given EAN")
class DuplicatedProductException : RuntimeException("A product with the same EAN is already present")
