package com.fridaytech.dex.core

class UnauthorizedException : Exception("Auth data is empty")
class CreateOrderException : Exception()
class CancelOrderException(maker: String, own: String) : Exception("Order maker address is - $maker, own address - $own")
